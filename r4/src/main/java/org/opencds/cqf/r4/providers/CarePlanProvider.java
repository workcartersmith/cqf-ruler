package org.opencds.cqf.r4.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.dao.DaoRegistry;
import ca.uhn.fhir.jpa.dao.IFhirResourceDao;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.client.api.IGenericClient;

import org.hl7.fhir.r4.model.*;

import java.util.LinkedList;
import java.util.List;

import org.hl7.fhir.r4.model.Task.TaskStatus;
import org.hl7.fhir.r4.model.CarePlan.CarePlanStatus;
import org.opencds.cqf.common.helpers.ClientHelper;
import org.opencds.cqf.r4.managers.ERSDTaskManager;
import org.hl7.fhir.exceptions.FHIRException;

public class CarePlanProvider {

    private FhirContext fhirContext;
    private IFhirResourceDao<Task> taskDao;
    private IFhirResourceDao<CarePlan> carePlanDao;
    private IFhirResourceDao<Endpoint> endpointDao;
    private IGenericClient workFlowClient;

    public CarePlanProvider(FhirContext fhirContext, DaoRegistry registry) {
        this.fhirContext = fhirContext;
        taskDao = registry.getResourceDao(Task.class);
        carePlanDao = registry.getResourceDao(CarePlan.class);
        endpointDao = registry.getResourceDao(Endpoint.class);
    }

    @Operation(name = "$execute", type = CarePlan.class)
    public CarePlan execute(@OperationParam(name = "carePlan", min = 1, max = 1, type = CarePlan.class) CarePlan carePlan,
    @OperationParam(name = "dataEndpoint", type = Endpoint.class) Endpoint dataEndpoint, @RequiredParam(name = "subject") String patientId,
    @OperationParam(name = "parameters", type = Parameters.class) Parameters parameters) throws FHIRException {

        //Save CarePlan to DB
        //TODO: if endpoint does not already exist PUT it
        Endpoint carePlanEndpoint = endpointDao.read(dataEndpoint.getIdElement());
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

    @Operation(name = "$taskApply", type = Task.class)
    public Resource taskApply(@OperationParam(name = "task") Task task, @RequiredParam(name = "subject") String patientId) throws InstantiationException {
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
            .map(reference -> carePlanDao.read(new IdType(reference.getReference())))
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
                containedTask = taskDao.read(containedTask.getIdElement());
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