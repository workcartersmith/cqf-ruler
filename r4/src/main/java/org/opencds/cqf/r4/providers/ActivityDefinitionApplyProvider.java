package org.opencds.cqf.r4.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.dao.DaoRegistry;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.exceptions.FHIRException;
import org.opencds.cqf.common.exceptions.ActivityDefinitionApplyException;
import org.opencds.cqf.r4.processors.ActivityDefinitionApplyProcessor;

/**
 * Created by Bryn on 1/16/2017.
 */
public class ActivityDefinitionApplyProvider {

    private ActivityDefinitionApplyProcessor activityDefinitionApplyProcessor;

    public ActivityDefinitionApplyProvider(FhirContext fhirContext, DaoRegistry registry, CqlExecutionProvider cqlExecutionProvider) {
        activityDefinitionApplyProcessor = new ActivityDefinitionApplyProcessor(fhirContext, registry, cqlExecutionProvider.getProcessor());
    }

    @Operation(name = "$apply", idempotent = true, type = ActivityDefinition.class)
    public Resource apply(@IdParam IdType theId, @OperationParam(name = "patient") String patientId,
            @OperationParam(name = "encounter") String encounterId,
            @OperationParam(name = "practitioner") String practitionerId,
            @OperationParam(name = "organization") String organizationId,
            @OperationParam(name = "userType") String userType,
            @OperationParam(name = "userLanguage") String userLanguage,
            @OperationParam(name = "userTaskContext") String userTaskContext,
            @OperationParam(name = "setting") String setting,
            @OperationParam(name = "settingContext") String settingContext) throws InternalErrorException, FHIRException,
            ClassNotFoundException, IllegalAccessException, InstantiationException, ActivityDefinitionApplyException {
        return activityDefinitionApplyProcessor.apply(theId, patientId, encounterId, practitionerId, organizationId, userType, userLanguage, userTaskContext, setting, settingContext);
    }

    //here in order to avoid conflicts in Github
    //TODO: remove this method once PlanDefinitionApplyProvider uses Processor and use ActivityDefinitionApplyProcessor instead
    public Resource resolveActivityDefinition(ActivityDefinition activityDefinition, String patientId,
            String practitionerId, String organizationId) throws FHIRException {
                return activityDefinitionApplyProcessor.resolveActivityDefinition(activityDefinition, patientId, practitionerId, organizationId);
    }

    public ActivityDefinitionApplyProcessor getProcessor() {
		return activityDefinitionApplyProcessor;
	}  
}
