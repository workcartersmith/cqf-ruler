package org.opencds.cqf.ruler.plugin.conformance;

import ca.uhn.fhir.jpa.starter.annotations.OnR4Condition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "hapi.fhir.sdc", name ="enabled", havingValue = "true")
public class ConformanceConfig {

    @Bean
    public ConformanceProperties SDCProperties() {
        return new ConformanceProperties();
    }

    @Bean
    @Conditional(OnR4Condition.class)
    public ProcessMessageProvider r4ProcessMessageProvider() {
        return new org.opencds.cqf.ruler.plugin.conformance.ProcessMessageProvider();
    }
}
