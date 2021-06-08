package org.opencds.cqf.ruler.core.api.capability;

import org.hl7.fhir.instance.model.api.IBaseConformance;

public interface CapabilityStatementExtender<T extends IBaseConformance> {
    T extend(T capabilityStatement);
}
