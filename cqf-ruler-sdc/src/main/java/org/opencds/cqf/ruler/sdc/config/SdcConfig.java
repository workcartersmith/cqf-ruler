package org.opencds.cqf.ruler.sdc.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ SdcConfigDstu3.class, SdcConfigR4.class })
@ConditionalOnProperty(value = "hapi.fhir.sdc.enabled", havingValue = "true", matchIfMissing = false)
public class SdcConfig {

}
