package org.opencds.cqf.r4.managers;

import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Goal.GoalLifecycleStatus;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.GuidanceResponse.GuidanceResponseStatus;

public class ERSDTaskManager {

    public Resource forTask(String taskId, GuidanceResponse guidanceResponse, String patientId) throws InstantiationException {
        Resource resource = null;
        switch (taskId) {
            case ("task-rulefilter-report") : resource = fillGuidanceResponse(guidanceResponse, taskId, patientId); break;
            case ("task-create-eicr") : resource = executeCreateEICR(guidanceResponse, taskId, patientId); break;
            case ("task-periodic-update-eicr") : resource = updateEICR(guidanceResponse, taskId, patientId); break;
            case ("task-close-out-eicr") : resource = closeOutEICR(guidanceResponse, taskId, patientId); break;
            default : throw new InstantiationException("Unknown task Apply type.");
        }
        return resource;
    }

    private Goal executeCreateEICR(GuidanceResponse guidanceResponse, String taskId, String patientId) {
        Goal eicr = new Goal();
        eicr.setId("example-eicr");
        eicr.setLifecycleStatus(GoalLifecycleStatus.ONHOLD);
        List<Annotation> notes = new ArrayList<Annotation>();
        Annotation createEICRNote = new Annotation();
        Annotation postEICRNote = new Annotation();
        Annotation grabEndpointNote = new Annotation();
        createEICRNote.setText("create eICR using eICR Service.");
        notes.add(createEICRNote);
        postEICRNote.setText("Post eICR.");
        notes.add(postEICRNote);
        grabEndpointNote.setText("Grab EHR data endpoint needed to create eICR.");
        notes.add(grabEndpointNote);
        eicr.setNote(notes);
        return eicr;
    }

    private Resource updateEICR(GuidanceResponse guidanceResponse, String taskId, String patientId) {
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

    private Resource closeOutEICR(GuidanceResponse guidanceResponse, String taskId, String patientId) {
        System.out.println("eICR closed out.");
        return fillGuidanceResponse(guidanceResponse, taskId, patientId, null);
    }

    private GuidanceResponse fillGuidanceResponse(GuidanceResponse guidanceResponse, String taskId, String patientId, Resource resource) {
        if (resource != null) {
            guidanceResponse.addContained(resource);
        }
        return fillGuidanceResponse(guidanceResponse, taskId, patientId);
    }

    private GuidanceResponse fillGuidanceResponse(GuidanceResponse guidanceResponse, String taskId, String patientId) {
        Reference subjectReference = new Reference(); subjectReference.setReference(patientId);
        guidanceResponse.setSubject(subjectReference);
        guidanceResponse.setStatus(GuidanceResponseStatus.SUCCESS);
        Annotation resultNote = new Annotation();
        resultNote.setText(taskId + " applied.");
        guidanceResponse.addNote(resultNote);
        return guidanceResponse;
    }

}