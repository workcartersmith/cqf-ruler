package org.opencds.cqf.ruler.sdc.config;

import org.opencds.cqf.ruler.core.api.config.FhirVersion;
import org.opencds.cqf.ruler.core.api.config.FhirVersionCondition;
import org.opencds.cqf.ruler.sdc.r4.OAuthExtender;
import org.opencds.cqf.ruler.sdc.r4.ObservationProvider;
import org.opencds.cqf.ruler.sdc.r4.QuestionnaireProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;

@Configuration
@Conditional(FhirVersionCondition.class)
@FhirVersion(FhirVersionEnum.R4)
public class SdcConfigR4 {
    @Bean
    @ConditionalOnProperty(prefix = "hapi.fhir.sdc.oauth", value = "enabled", havingValue = "true", matchIfMissing = false)
    public OAuthExtender oauthExtender(SdcProperties sdcProperties) {
        return new OAuthExtender(sdcProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "hapi.fhir.sdc.observation_transform", value = "enabled", havingValue = "true", matchIfMissing = false)
    public ObservationProvider observationProvider(FhirContext fhirContext, SdcProperties sdcProperties) {
        return new ObservationProvider(fhirContext, sdcProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "hapi.fhir.sdc.questionnaire_response_extract", value = "enabled", havingValue = "true", matchIfMissing = false)
    public QuestionnaireProvider questionnaireProvider(FhirContext fhirContext, SdcProperties sdcProperties) {
        return new QuestionnaireProvider(fhirContext, sdcProperties);
    }
}
