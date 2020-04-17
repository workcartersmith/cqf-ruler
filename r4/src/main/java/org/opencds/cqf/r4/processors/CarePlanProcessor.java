package org.opencds.cqf.r4.processors;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.dao.DaoRegistry;
import ca.uhn.fhir.jpa.dao.IFhirResourceDao;
import ca.uhn.fhir.rest.client.api.IGenericClient;

import org.hl7.fhir.r4.model.*;

import java.util.LinkedList;
import java.util.List;

import org.hl7.fhir.r4.model.Task.TaskStatus;
import org.hl7.fhir.r4.model.CarePlan.CarePlanStatus;
import org.opencds.cqf.common.helpers.ClientHelper;
import org.opencds.cqf.r4.managers.ERSDTaskManager;
import org.hl7.fhir.exceptions.FHIRException;


public class CarePlanProcessor {

    private FhirContext fhirContext;
    private IGenericClient workFlowClient;
    private IFhirResourceDao<Endpoint> endpointDao;

    public CarePlanProcessor(FhirContext fhirContext, DaoRegistry registry) {
        this.fhirContext = fhirContext;
        this.endpointDao = registry.getResourceDao(Endpoint.class);
    }

    /*
        $execute Operation
        if we dont want to expose the operation we can just remove the provider, but still have the functionality
        This is similar to the cqf-tooling separation of Processing and Operations
    */
    public CarePlan execute(CarePlan carePlan, Endpoint dataEndpoint, String patientId, Parameters parameters) {
        if(dataEndpoint == null) {
            dataEndpoint = new Endpoint();
            dataEndpoint.setAddress("http://localhost:8080/cqf-ruler-r4/fhir/");
        }
        
        //TODO: if endpoint does not already exist PUT it
        Endpoint carePlanEndpoint = endpointDao.read(dataEndpoint.getIdElement());
        //Save CarePlan to DB
        workFlowClient = ClientHelper.getClient(fhirContext, carePlanEndpoint);
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
    public Resource taskApply(Task task, String patientId) throws InstantiationException {
        ERSDTaskManager ersdTaskManager = new ERSDTaskManager();
        GuidanceResponse guidanceResponse = new GuidanceResponse();
        String taskId = task.getIdElement().getIdPart();        
        guidanceResponse.setId("guidanceResponse-" + taskId);
        Resource result = ersdTaskManager.forTask(taskId, guidanceResponse, patientId);
        resolveStatusAndUpdate(task);
        return result;
    }

    private void resolveStatusAndUpdate(Task task) {
        task.setStatus(TaskStatus.COMPLETED);
        workFlowClient.update().resource(task).execute();
        List<Reference> basedOnReferences = task.getBasedOn();
        List<CarePlan> carePlansAssociatedWithTask = new LinkedList<CarePlan>();
        basedOnReferences.stream()
            .filter(reference -> reference.getReference().contains("CarePlan/"))
            .map(reference -> workFlowClient.read().resource(CarePlan.class).withId(reference.getReference()).execute())
            .forEach(carePlan -> carePlansAssociatedWithTask.add((CarePlan)carePlan));

        for (CarePlan carePlan : carePlansAssociatedWithTask) {
            List<Task> carePlanTasks = new LinkedList<Task>();
            carePlan.getContained().stream()
                .filter(resource -> (resource instanceof Task))
                .map(resource -> (Task)resource)
                .forEach(containedTask -> carePlanTasks.add(containedTask));
            boolean allTasksCompleted = true;
            for (Task containedTask : carePlanTasks) {
                containedTask.setId(containedTask.getIdElement().getIdPart().replaceAll("#", ""));
                containedTask = workFlowClient.read().resource(Task.class).withId(containedTask.getId()).execute();
                if(containedTask.getStatus() != TaskStatus.COMPLETED) {
                    allTasksCompleted = false;
                }
                workFlowClient.update().resource(carePlan).execute();
            }
            if(allTasksCompleted) {
                carePlan.setStatus(CarePlanStatus.COMPLETED);
                workFlowClient.update().resource(carePlan).execute();
            }
        }
        workFlowClient.update().resource(task).execute();
    }

}