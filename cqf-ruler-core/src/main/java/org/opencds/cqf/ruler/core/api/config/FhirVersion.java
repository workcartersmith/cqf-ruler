package org.opencds.cqf.ruler.core.api.config;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

import ca.uhn.fhir.context.FhirVersionEnum;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FhirVersion {
    public FhirVersionEnum[] value() default FhirVersionEnum.R4;
}
