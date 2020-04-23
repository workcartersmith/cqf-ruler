package org.opencds.cqf.r4.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.dao.DaoRegistry;
import ca.uhn.fhir.rest.annotation.*;

import org.hl7.fhir.r4.model.*;

import org.opencds.cqf.r4.processors.CarePlanProcessor;
import org.hl7.fhir.exceptions.FHIRException;

public class CarePlanProvider {

    private CarePlanProcessor carePlanProcessor;

    public CarePlanProvider(FhirContext fhirContext, DaoRegistry registry) {
        carePlanProcessor = new CarePlanProcessor(fhirContext, registry);
    }

    @Operation(name = "$execute", type = CarePlan.class)
    public CarePlan execute(@OperationParam(name = "carePlan", min = 1, max = 1, type = CarePlan.class) CarePlan carePlan,
    @OperationParam(name = "dataEndpoint", type = Endpoint.class) Endpoint dataEndpoint, @RequiredParam(name = "subject") String patientId,
    @OperationParam(name = "parameters", type = Parameters.class) Parameters parameters) throws FHIRException {
        return carePlanProcessor.execute(carePlan, dataEndpoint, patientId, parameters);
    }
}