package org.opencds.cqf.ruler.sdc.config;

import org.opencds.cqf.ruler.core.api.config.FhirVersion;
import org.opencds.cqf.ruler.core.api.config.FhirVersionCondition;
import org.opencds.cqf.ruler.sdc.dstu3.OAuthExtender;
import org.opencds.cqf.ruler.sdc.dstu3.ObservationProvider;
import org.opencds.cqf.ruler.sdc.dstu3.QuestionnaireProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;

@Configuration
@Conditional(FhirVersionCondition.class)
@FhirVersion(FhirVersionEnum.DSTU3)
public class SdcConfigDstu3 {

    @Bean
    @ConditionalOnProperty(value = "hapi.fhir.sdc.oauth.enabled", havingValue = "true", matchIfMissing = false)
    public OAuthExtender oauthExtender() {
        return new OAuthExtender();
    }

    @Bean
    @ConditionalOnProperty(value = "hapi.fhir.sdc.observation_transform.enabled", havingValue = "true", matchIfMissing = false)
    public ObservationProvider observationProvider(FhirContext fhirContext) {
        return new ObservationProvider(fhirContext);
    }

    @Bean
    @ConditionalOnProperty(value = "hapi.fhir.sdc.observation_extract.enabled", havingValue = "true", matchIfMissing = false)
    public QuestionnaireProvider questionnaireProvider(FhirContext fhirContext) {
        return new QuestionnaireProvider(fhirContext);
    }
}

