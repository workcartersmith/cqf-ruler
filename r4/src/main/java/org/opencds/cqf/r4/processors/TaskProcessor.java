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

public class TaskProcessor {

    private IGenericClient workFlowClient;
    private IFhirResourceDao<Endpoint> endpointDao;

    public TaskProcessor(FhirContext fhirContext, DaoRegistry registry) {
        this.endpointDao = registry.getResourceDao(Endpoint.class);
        workFlowClient = ClientHelper.getClient(fhirContext, endpointDao.read(new IdType("local-endpoint")));
    }

    public Resource taskExecute(Task task, String patientId) throws InstantiationException {
        workFlowClient.read().resource(Task.class).withId(task.getIdElement()).execute();
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
        if (basedOnReferences.isEmpty() || basedOnReferences == null) {
            throw new RuntimeException("Task must fullfill a request in order to be applied. i.e. must have a basedOn element containing a reference to a Resource");
        }
        List<CarePlan> carePlansAssociatedWithTask = new LinkedList<CarePlan>();
        basedOnReferences.stream()
            .filter(reference -> reference.getReference().contains("CarePlan/"))
            .map(reference -> workFlowClient.read().resource(CarePlan.class).withId(reference.getReference()).execute())
            .forEach(carePlan -> carePlansAssociatedWithTask.add((CarePlan)carePlan));
        
        if (basedOnReferences.isEmpty()) {
            throw new RuntimeException("$taskApply only supports tasks based on CarePlans as of now.");  
        }

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
            }
            if(allTasksCompleted) {
                carePlan.setStatus(CarePlanStatus.COMPLETED);
                workFlowClient.update().resource(carePlan).execute();
            }
        }
        workFlowClient.update().resource(task).execute();
    }

}