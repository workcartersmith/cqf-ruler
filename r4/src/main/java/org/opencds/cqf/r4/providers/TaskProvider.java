package org.opencds.cqf.r4.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.dao.DaoRegistry;
import ca.uhn.fhir.rest.annotation.*;

import org.hl7.fhir.r4.model.*;

import org.opencds.cqf.r4.processors.TaskProcessor;

public class TaskProvider {

    private TaskProcessor TaskProcessor;

    public TaskProvider(FhirContext fhirContext, DaoRegistry registry) {
        TaskProcessor = new TaskProcessor(fhirContext, registry);
    }

    @Operation(name = "$taskExecute", type = Task.class)
    public Resource taskExecute(@OperationParam(name = "task", min = 1, max = 1, type = Task.class) Task task, @RequiredParam(name = "subject") String patientId) throws InstantiationException {
        return TaskProcessor.taskExecute(task, patientId);
    }
}