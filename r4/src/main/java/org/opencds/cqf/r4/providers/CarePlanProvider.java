package org.opencds.cqf.r4.providers;

import ca.uhn.fhir.rest.annotation.*;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Goal.GoalLifecycleStatus;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.GuidanceResponse.GuidanceResponseStatus;
import org.hl7.fhir.exceptions.FHIRException;

public class CarePlanProvider {

    @Operation(name = "$execute", idempotent = true, type = CarePlan.class)
    public void execute(@IdParam IdType theId, @OperationParam(name = "carePlan") CarePlan carePlan,
    @OperationParam(name = "endpoint") Endpoint endpoint, @RequiredParam(name = "subject") String patientId) throws FHIRException {

        List<Resource> containedResources = carePlan.getContained();
        containedResources.forEach(task -> forContained(task));
    }

    private void forContained(Resource resource) {
        switch (resource.fhirType()) {
            case "Task": scheduleTask(resource); break;
            default : 
                throw new FHIRException("Unkown Fhir Resource. " + resource.getId());
        }

    }

    private void scheduleTask(Resource task) {
        System.out.println("Task " + task.getId() + " scheduled.");
    }

    @Operation(name = "$taskApply", idempotent = true, type = Task.class)
    public GuidanceResponse taskApply(@IdParam IdType theId, @OperationParam(name = "task") Task task, @RequiredParam(name = "subject") String patientId) throws InstantiationException {

        GuidanceResponse guidanceResponse = new GuidanceResponse();
        String taskId = task.getId();        
        guidanceResponse.setId("guidanceResponse-" + taskId);
        switch (taskId) {
            case ("task-rulefilter-report") : fillGuidanceResponse(guidanceResponse, taskId, patientId); break;
            case ("task-create-eicr") : executeCreateEICR(guidanceResponse, taskId, patientId); break;
            case ("task-periodic-update-eicr") : updateEICR(guidanceResponse, taskId, patientId); break;
            case ("task-close-out-eicr") : closeOutEICR(guidanceResponse, taskId, patientId); break;
            default : throw new InstantiationException("Unknown task Apply type.");
        }
        return guidanceResponse;

    }

    private Resource executeCreateEICR(GuidanceResponse guidanceResponse, String taskId, String patientId) {
        Goal eicr = new Goal();
        eicr.setId("example-eicr");
        eicr.setLifecycleStatus(GoalLifecycleStatus.ONHOLD);
        List<Annotation> notes = new ArrayList<Annotation>();
        Annotation createEICRNote = new Annotation();
        Annotation postEICRNote = new Annotation();
        Annotation grabEndpointNote = new Annotation();
        createEICRNote.addChild("create eICR using eICR Service.");
        notes.add(createEICRNote);
        postEICRNote.addChild("Post eICR.");
        notes.add(createEICRNote);
        grabEndpointNote.addChild("Grab EHR data endpoint needed to create eICR.");
        notes.add(grabEndpointNote);
        eicr.setNote(notes);
        fillGuidanceResponse(guidanceResponse, taskId, patientId);
        return eicr;
    }

    private Resource updateEICR(GuidanceResponse guidanceResponse, String taskId, String patientId) {
        return executeCreateEICR(guidanceResponse, taskId, patientId);
    }

    private void closeOutEICR(GuidanceResponse guidanceResponse, String taskId, String patientId) {
        fillGuidanceResponse(guidanceResponse, taskId, patientId);
        System.out.println("eICR closed out.");
    }

    private GuidanceResponse fillGuidanceResponse(GuidanceResponse guidanceResponse, String taskId, String patientId) {
        Reference subjectReference = new Reference(); subjectReference.setReference(patientId);
        guidanceResponse.setSubject(subjectReference);
        guidanceResponse.setStatus(GuidanceResponseStatus.SUCCESS);
        Annotation resultNote = new Annotation();
        resultNote.addChild(taskId + " applied.");
        guidanceResponse.addNote(resultNote);
        return guidanceResponse;
    }

}