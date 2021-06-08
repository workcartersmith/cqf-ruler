package org.opencds.cqf.ruler.core.api.config;

import java.util.List;

import org.springframework.util.MultiValueMap;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import ca.uhn.fhir.context.FhirVersionEnum;

public class FhirVersionCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata metadata) {
      FhirVersionEnum version = FhirVersionEnum.forVersionString(conditionContext.
        getEnvironment()
        .getProperty("hapi.fhir.fhir_version")
        .toUpperCase());

      MultiValueMap<String, Object> map = metadata.getAllAnnotationAttributes(FhirVersion.class.getName());
      if (map == null || map.isEmpty() || !map.containsKey("value")) {
        throw new IllegalArgumentException("You must use the \"FhirVersionCondition\" annotation in combination with a \"FhirVersion\" annotation to specify which versions of fhir the config applies to.");
      }
  
      List<Object> versions = map.get("value");
      if (versions == null || versions.isEmpty()) {
        throw new IllegalArgumentException("The \"FhirVersion\" annotation requires you to specify which versions the config applies to.");
      }

      for (Object v : versions) {
        FhirVersionEnum[] enums = (FhirVersionEnum[])v;
        for (FhirVersionEnum e : enums) {
          if (((FhirVersionEnum)e).equals(version)) {
            return true;
          }
        }
      }

      return false;
    }
  }
