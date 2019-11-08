package org.opencds.cqf.r4.providers;

import ca.uhn.fhir.jpa.rp.r4.ObservationResourceProvider;
import ca.uhn.fhir.jpa.rp.r4.QuestionnaireResponseResourceProvider;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.r4.model.*;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class FHIRQuestionnaireResponseProvider extends QuestionnaireResponseResourceProvider {

    private JpaDataProvider provider;

    public FHIRQuestionnaireResponseProvider(JpaDataProvider provider) {
        this.provider =  provider;
    }

    // NOTE: this is likely NOT idempotent
    @Operation(name = "$cpg-next-step", idempotent = true)
    public Resource evaluate(@OperationParam(name="response") QuestionnaireResponse theResponse) throws IOException, JAXBException {
        Questionnaire questionnaire = (Questionnaire) provider.resolveResourceProvider("Questionnaire").getDao().read(new IdType(theResponse.getQuestionnaire()));
        // check the QuestionnaireResponse status -> if in-progress return Questionnaire else generate resources and return output of PlanDefinition $apply
        if (theResponse.getStatus() == QuestionnaireResponse.QuestionnaireResponseStatus.INPROGRESS) {
            return questionnaire;
        }

        else if (theResponse.getStatus() == QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED) {
            // generate resources and return output of PlanDefinition $apply
            if (!theResponse.hasSubject()) throw new RuntimeException("Subject required");
//            Patient patient = (Patient) provider.resolveResourceProvider("Patient").getDao().read(new IdType(theResponse.getSubject().getReference()));
            if (!theResponse.hasEncounter()) throw new RuntimeException("Encounter required");
//            Encounter encounter = (Encounter) provider.resolveResourceProvider("Encounter").getDao().read(new IdType(theResponse.getEncounter().getReference()));

            for (QuestionnaireResponse.QuestionnaireResponseItemComponent responseItem : theResponse.getItem()) {

                for (Questionnaire.QuestionnaireItemComponent controlItem : questionnaire.getItem()) {
                    if (responseItem.getLinkId().equals(controlItem.getLinkId())) {
                        if (controlItem.hasExtension("http://hl7.org/fhir/StructureDefinition/questionnaire-fhirType")) {
                            String resultType = controlItem.getExtensionByUrl("http://hl7.org/fhir/StructureDefinition/questionnaire-fhirType").getValueAsPrimitive().getValueAsString();

                            // TODO: generify for all types
                            Observation observation = new Observation();
                            observation.setEncounter(theResponse.getEncounter());
                            observation.setSubject(theResponse.getSubject());

                            int count = 0;

                            for (QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer : responseItem.getAnswer()) {
                                Type answerType = answer.getValue();
                                Type finalAnswer = answerType;
                                if (answerType instanceof Coding) {
                                    finalAnswer = new CodeableConcept().addCoding((Coding) answerType);
                                }
                                if (count == 0) {
                                    observation.setValue(finalAnswer);
                                }
                                else {
                                    observation.addComponent(new Observation.ObservationComponentComponent().setValue(finalAnswer));
                                }
                                count++;
                            }

                            ((ObservationResourceProvider) provider.resolveResourceProvider("Observation")).getDao().create(observation);
                        }
                        else {
                            throw new RuntimeException("fhirType extension required");
                        }
                    }
                }
            }

            CarePlan result =
                    ((FHIRPlanDefinitionResourceProvider) provider
                            .resolveResourceProvider("PlanDefinition"))
                            .applyPlanDefinition(
                                    new IdType(questionnaire.getIdElement().getIdPart()), theResponse.getSubject().getReference(),
                                    theResponse.getEncounter().getReference(), null, null, null, null, null, null, null
                            );

            return result;
        }

        else {
            throw new RuntimeException("Invalid status: " + theResponse.getStatus().toCode());
        }
    }
}