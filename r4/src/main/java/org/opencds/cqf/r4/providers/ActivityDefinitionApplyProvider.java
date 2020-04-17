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
        activityDefinitionApplyProcessor = new ActivityDefinitionApplyProcessor(fhirContext, registry, cqlExecutionProvider);
    }

    @Operation(name = "$apply", idempotent = true, type = ActivityDefinition.class)
    public Resource apply(@IdParam IdType theId, @RequiredParam(name = "patient") String patientId,
            @OptionalParam(name = "encounter") String encounterId,
            @OptionalParam(name = "practitioner") String practitionerId,
            @OptionalParam(name = "organization") String organizationId,
            @OptionalParam(name = "userType") String userType,
            @OptionalParam(name = "userLanguage") String userLanguage,
            @OptionalParam(name = "userTaskContext") String userTaskContext,
            @OptionalParam(name = "setting") String setting,
            @OptionalParam(name = "settingContext") String settingContext) throws InternalErrorException, FHIRException,
            ClassNotFoundException, IllegalAccessException, InstantiationException, ActivityDefinitionApplyException {
        return activityDefinitionApplyProcessor.apply(theId, patientId, encounterId, practitionerId, organizationId, userType, userLanguage, userTaskContext, setting, settingContext);
    }

    //here in order to avoid conflicts in Github
    //TODO: remove this method once PlanDefinitionApplyProvider uses Processor and use ActivityDefinitionApplyProcessor instead
    public Resource resolveActivityDefinition(ActivityDefinition activityDefinition, String patientId,
            String practitionerId, String organizationId) throws FHIRException {
                return activityDefinitionApplyProcessor.resolveActivityDefinition(activityDefinition, patientId, practitionerId, organizationId);
    }
}
