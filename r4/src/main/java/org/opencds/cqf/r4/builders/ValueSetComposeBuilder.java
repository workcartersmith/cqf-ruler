package org.opencds.cqf.r4.builders;

import java.util.List;

import org.hl7.fhir.r4.model.ValueSet;
import ca.uhn.fhir.cql.common.builder.BaseBuilder;

public class ValueSetComposeBuilder extends BaseBuilder<ValueSet.ValueSetComposeComponent> {

    public ValueSetComposeBuilder(ValueSet.ValueSetComposeComponent complexProperty) {
        super(complexProperty);
    }

    public ValueSetComposeBuilder buildIncludes(List<ValueSet.ConceptSetComponent> includes) {
        complexProperty.setInclude(includes);
        return this;
    }

    public ValueSetComposeBuilder buildIncludes(ValueSet.ConceptSetComponent include) {
        complexProperty.addInclude(include);
        return this;
    }
}
