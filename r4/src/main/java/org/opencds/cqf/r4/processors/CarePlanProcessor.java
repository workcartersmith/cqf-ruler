package org.opencds.cqf.r4.processors;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.dao.DaoRegistry;
import ca.uhn.fhir.jpa.dao.IFhirResourceDao;
import ca.uhn.fhir.rest.client.api.IGenericClient;

import org.hl7.fhir.r4.model.*;

import java.util.List;

import org.hl7.fhir.r4.model.Task.TaskStatus;
import org.hl7.fhir.r4.model.CarePlan.CarePlanStatus;
import org.opencds.cqf.common.helpers.ClientHelper;
import org.hl7.fhir.exceptions.FHIRException;


public class CarePlanProcessor {

    private FhirContext fhirContext;
    private IGenericClient workFlowClient;
    private IFhirResourceDao<Endpoint> endpointDao;

    public CarePlanProcessor(FhirContext fhirContext, DaoRegistry registry) {
        this.fhirContext = fhirContext;
        this.endpointDao = registry.getResourceDao(Endpoint.class);
        workFlowClient = ClientHelper.getClient(fhirContext, new Endpoint().setAddress("http://localhost:8080/cqf-ruler-r4/fhir/"));
    }

    /*
        $execute Operation
        if we dont want to expose the operation we can just remove the provider, but still have the functionality
        This is similar to the cqf-tooling separation of Processing and Operations
    */
    //TODO: add dataEndpoint id parameter for grabbing an endpoint that already exists
    public CarePlan execute(CarePlan carePlan, Endpoint dataEndpoint, String patientId, Parameters parameters) {
        if(dataEndpoint == null) {
            //this should be created at start of ruler
            dataEndpoint = endpointDao.read(new IdType("local-endpoint"));
        }
        else {
            endpointDao.update(dataEndpoint);
        }
        //Save CarePlan to DB
        workFlowClient = ClientHelper.getClient(fhirContext, dataEndpoint);
        workFlowClient.update().resource(carePlan).execute();
        carePlan.setStatus(CarePlanStatus.ACTIVE);
        workFlowClient.update().resource(carePlan).execute();

        List<Resource> containedResources = carePlan.getContained();
        containedResources.forEach(task -> forContained(task));
        System.out.println("Tasks scheduled. ");
        return carePlan;
    }

    private void forContained(Resource resource) {
        resource.setId(resource.getIdElement().getIdPart().replaceAll("#", ""));
        switch (resource.fhirType()) {
            case "Task": 
                //schedule Tasks
                scheduleTask((Task)resource); 
                //Save Tasks to DB 
                workFlowClient.update().resource((Task)resource).execute(); break;
            default : 
                throw new FHIRException("Unkown Fhir Resource. " + resource.getId());
        }

    }

    private void scheduleTask(Task task) {
        task.setStatus(TaskStatus.INPROGRESS);
        System.out.println("Task " + task.getIdElement().getIdPart() + " scheduled.");
    }

    /*
        $execute Operation
        if we dont want to expose the operation we can just remove the provider, but still have the functionality
        This is similar to the cqf-tooling separation of Processing and Operations
    */

}