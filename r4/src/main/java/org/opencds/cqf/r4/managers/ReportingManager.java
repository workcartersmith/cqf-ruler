package org.opencds.cqf.r4.managers;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import com.alphora.cql.service.factory.DataProviderFactory;

import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Task;
import org.opencds.cqf.common.providers.LibraryResolutionProvider;
import org.opencds.cqf.cql.terminology.TerminologyProvider;
import org.opencds.cqf.r4.processors.ActivityDefinitionApplyProcessor;
import org.opencds.cqf.r4.processors.CarePlanProcessor;
import org.opencds.cqf.r4.processors.CqlExecutionProcessor;
import org.opencds.cqf.r4.processors.PlanDefinitionApplyProcessor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.dao.DaoRegistry;
import ca.uhn.fhir.jpa.dao.IFhirResourceDao;

public class ReportingManager {

    //public void ManageReporting(PlanDefinition planDefinition, PlanDefinitionApplyProvider planDefinitionApplyProvider, CarePlanProvider carePlanProvider) {
        /*
            Need to grab eRSD from eRSD Client or EHR not sure
            PlanDefinition apply eRSD
            if CarePlan is empty then deam not Filterable
            execute CarePlan
        */

    private Endpoint eRSDEndpoint;
    private PlanDefinitionApplyProcessor planDefinitionApplyProcessor;
    private FhirContext fhirContext;
    private DaoRegistry registry;

    public ReportingManager(FhirContext fhirContext, DaoRegistry registry, LibraryResolutionProvider<org.hl7.fhir.r4.model.Library> libraryResourceProvider,
     DataProviderFactory dataProviderFactory, TerminologyProvider localSystemTerminologyProvider ) {
        IFhirResourceDao<Endpoint> endpointDao = registry.getResourceDao(Endpoint.class);
        try {
            eRSDEndpoint = endpointDao.read(new IdType("endpoint-ERSD"));
        } catch (Exception e) {
            Endpoint localEndpoint = new Endpoint();
            localEndpoint.setAddress("http://localhost:8080/cqf-ruler-r4/fhir/");
            System.out.println("endpoint-ERSD does not exist in the system. Using local Database");
            eRSDEndpoint = localEndpoint;
        }
        CqlExecutionProcessor cqlExecutionProcessor = new CqlExecutionProcessor(libraryResourceProvider, dataProviderFactory, fhirContext, localSystemTerminologyProvider);
        ActivityDefinitionApplyProcessor activityDefinitionApplyProcessor = new ActivityDefinitionApplyProcessor(fhirContext, registry, cqlExecutionProcessor);
        planDefinitionApplyProcessor = new PlanDefinitionApplyProcessor(fhirContext, activityDefinitionApplyProcessor, registry, cqlExecutionProcessor);
    }

    public Resource manage(String eRSDId, String patientId) throws FHIRException, IOException, JAXBException {
        CarePlan carePlan = applyPlanDefinition(eRSDId, patientId);
        if (carePlan == null || !carePlan.hasStatus() || !carePlan.hasContained() || !carePlan.hasCreated()) {
            return executeCarePlan(carePlan, patientId);
        }
        else {
            Task notReportableTask = new Task();
            Annotation annotation = new Annotation();
            annotation.setText("eRSD application has resulted in no further need for filtering.");
            notReportableTask.addNote(annotation);
            return notReportableTask;
        }
    }

    private CarePlan applyPlanDefinition(String eRSDId, String patientId) throws FHIRException, IOException, JAXBException {
        return planDefinitionApplyProcessor.applyPlanDefinition(new IdType(eRSDId), patientId, null, null, null, null, null, null, null, null, eRSDEndpoint);
    }

    private Resource executeCarePlan(CarePlan carePlan, String patientId) {
        CarePlanProcessor carePlanProcessor = new CarePlanProcessor(fhirContext, registry);
        Endpoint workFlowEndpoint = new Endpoint();
        workFlowEndpoint.setAddress("http://localhost:8080/cqf-ruler-r4/fhir/");
        return carePlanProcessor.execute(carePlan, workFlowEndpoint, patientId, null);
    }

    //TaskScheduler
}