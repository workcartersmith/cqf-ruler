package org.opencds.cqf.r4.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.dao.DaoRegistry;
import ca.uhn.fhir.rest.annotation.*;

import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;

import org.opencds.cqf.r4.processors.TaskProcessor;

public class TaskProvider {

    private FhirContext fhirContext;
    private DaoRegistry registry;

    public TaskProvider(FhirContext fhirContext, DaoRegistry registry) {
        
    }

    //check Fhir Standard for operations
    //have some logic that switch based on task type
    //need to pass in specifc task manager to execute tasks
    //big long switch for now
    //define how to retrieve patient
    //end goal register task managers
    //*important*!! Fhir Standard Interfaces
    @Operation(name = "$execute", type = Task.class)
    public IAnyResource execute(@OperationParam(name = "task", min = 1, max = 1, type = Task.class) Task task) throws InstantiationException {
        TaskProcessor taskProcessor = new TaskProcessor(fhirContext, registry);
        return taskProcessor.execute(task);
    }
}