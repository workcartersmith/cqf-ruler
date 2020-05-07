package org.opencds.cqf.r4.managers;

import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Goal.GoalLifecycleStatus;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.GuidanceResponse.GuidanceResponseStatus;

public class ERSDTaskManager {

    public Resource forTask(Task task, GuidanceResponse guidanceResponse) throws InstantiationException {
        Resource resource = null;
        //Cant use taskCode
        for (Coding coding : task.getCode().getCoding()) {
            switch (coding.getCode()) {
                //these need to be codes
                case ("task-rulefilter-report") : resource = fillGuidanceResponse(guidanceResponse, coding.getCode()); break;
                case ("task-create-eicr") : resource = executeCreateEICR(guidanceResponse); break;
                case ("task-periodic-update-eicr") : resource = updateEICR(guidanceResponse); break;
                case ("task-close-out-eicr") : resource = closeOutEICR(guidanceResponse, coding.getCode()); break;
                default : throw new InstantiationException("Unknown task Apply type.");
            }
        } 
        return resource;
    }

    private Goal executeCreateEICR(GuidanceResponse guidanceResponse) {
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