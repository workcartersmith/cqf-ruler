package org.opencds.cqf.dstu3.builders;

import java.util.Date;

import ca.uhn.fhir.cql.common.builder.BaseBuilder;
import org.hl7.fhir.dstu3.model.Period;

public class PeriodBuilder extends BaseBuilder<Period> {

    public PeriodBuilder() {
        super(new Period());
    }

    public PeriodBuilder buildStart(Date start) {
        complexProperty.setStart(start);
        return this;
    }

    public PeriodBuilder buildEnd(Date end) {
        complexProperty.setEnd(end);
        return this;
    }
}
