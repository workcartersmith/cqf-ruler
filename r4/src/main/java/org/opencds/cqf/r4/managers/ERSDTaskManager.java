package org.opencds.cqf.r4.managers;

import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.CarePlan.CarePlanActivityComponent;
import org.hl7.fhir.r4.model.Goal.GoalLifecycleStatus;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.GuidanceResponse.GuidanceResponseStatus;
import org.opencds.cqf.common.helpers.ClientHelper;

import ca.uhn.fhir.jpa.dao.DaoRegistry;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.jpa.dao.IFhirResourceDao;
import ca.uhn.fhir.context.FhirContext;

public class ERSDTaskManager {

    private DaoRegistry registry;
    private IGenericClient workFlowClient;
    private IFhirResourceDao<Endpoint> endpointDao;

    public ERSDTaskManager(FhirContext fhirContext, DaoRegistry registry, IGenericClient workFlowClient) {
        this.registry = registry;
        this.endpointDao = registry.getResourceDao(Endpoint.class);
        this.workFlowClient = workFlowClient;
    }

    public Resource forTask(Task task, GuidanceResponse guidanceResponse) throws InstantiationException {
        Resource resource = null;
        // Cant use taskCode
        for (Coding coding : task.getCode().getCoding()) {
            switch (coding.getCode()) {
                // these need to be codes
                case ("rulefilter-report"):
                    resource = fillGuidanceResponse(guidanceResponse, coding.getCode());
                    break;
                case ("create-eicr"):
                    resource = executeCreateEICR(task, guidanceResponse);
                    break;
                case ("periodic-update-eicr"):
                    resource = updateEICR(guidanceResponse);
                    break;
                case ("close-out-eicr"):
                    resource = closeOutEICR(guidanceResponse, coding.getCode());
                    break;
                case ("validate-eicr"):
                    resource = validateEicr(guidanceResponse);
                    break;
                case ("route-and-send-eicr"):
                    resource = routeAndSend(guidanceResponse);
                    break;
                default:
                    throw new InstantiationException("Unknown task Apply type.");
            }
        }
        return resource;
    }

    // Demo get $notify -> PlanDef$apply -> executeCarePlan -> eventually posts eicr
    // to fhir endpoint
    private Goal routeAndSend(GuidanceResponse guidanceResponse) {
        // go to careplan look for create-eicr get outcome ref post to endpoint
        Goal validate = new Goal();
        System.out.println("eICR Routed and Sent.");
        return validate;
    }

    private Goal validateEicr(GuidanceResponse guidanceResponse) {
        Goal validate = new Goal();
        // go back to careplan look up create eicr and look up outcome reference
        System.out.println("Validated eICR");
        return validate;
    }

    private Bundle executeCreateEICR(Task task, GuidanceResponse guidanceResponse) {
        // find activity #create-eicr
        // set outcome reference on careplan
        Bundle bundle = new Bundle();
        bundle = registry.getResourceDao(Bundle.class).read(new IdType("bundle-eicr-document-zika"));
        Reference bundleReference = new Reference();
        bundleReference.setType("Bundle");
        bundleReference.setReference(bundle.getId());
        CarePlan carePlan = null;
        for (Reference reference : task.getBasedOn()) {
            if (reference.hasType() && reference.getType().equals("CarePlan")) {
                carePlan = workFlowClient.read().resource(CarePlan.class).withId(reference.getReference()).execute();
                carePlan.getActivity().stream()
                    .filter(activity -> activity.getReference().getReference().equals("#" + task.getIdElement().getIdPart()))
                    .forEach(activity ->  activity.addOutcomeReference(bundleReference));
            }
        }
        return bundle;
    }

    private Resource updateEICR(GuidanceResponse guidanceResponse) {
        Goal eicr = new Goal();
        eicr.setId("example-eicr");
        eicr.setLifecycleStatus(GoalLifecycleStatus.ONHOLD);
        Annotation retrieveEICR = new Annotation();
        retrieveEICR.setText("Retrieve eICR using endpoint.");
        eicr.addNote(retrieveEICR);
        Annotation updatedEICRNote = new Annotation();
        updatedEICRNote.setText("Update using eICR Service.");
        eicr.addNote(updatedEICRNote);
        return eicr;
    }

    private Resource closeOutEICR(GuidanceResponse guidanceResponse, String taskCode) {
        System.out.println("eICR closed out.");
        return fillGuidanceResponse(guidanceResponse, taskCode, null);
    }

    private GuidanceResponse fillGuidanceResponse(GuidanceResponse guidanceResponse, String taskCode, Resource resource) {
        if (resource != null) {
            guidanceResponse.addContained(resource);
        }
        return fillGuidanceResponse(guidanceResponse, taskCode);
    }

    private GuidanceResponse fillGuidanceResponse(GuidanceResponse guidanceResponse, String taskCode) {
        guidanceResponse.setStatus(GuidanceResponseStatus.SUCCESS);
        Annotation resultNote = new Annotation();
        resultNote.setText(taskCode + " applied.");
        guidanceResponse.addNote(resultNote);
        return guidanceResponse;
    }

}