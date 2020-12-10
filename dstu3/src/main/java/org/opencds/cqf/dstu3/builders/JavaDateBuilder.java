package org.opencds.cqf.dstu3.builders;

import java.util.Date;

import ca.uhn.fhir.cql.common.builder.BaseBuilder;
import org.opencds.cqf.cql.engine.runtime.DateTime;

public class JavaDateBuilder extends BaseBuilder<Date> {

    public JavaDateBuilder() {
        super(new Date());
    }

    public JavaDateBuilder buildFromDateTime(DateTime dateTime) {
        complexProperty = Date.from(dateTime.getDateTime().toInstant());
        return this;
    }
}
