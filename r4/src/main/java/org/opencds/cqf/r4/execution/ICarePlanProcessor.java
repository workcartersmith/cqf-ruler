package org.opencds.cqf.r4.execution;

import org.hl7.fhir.instance.model.api.IAnyResource;

public interface ICarePlanProcessor<C> {
    public IAnyResource execute(C carePlan);
}