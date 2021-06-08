package org.opencds.cqf.ruler.core.config;

import java.util.List;

import org.hl7.fhir.r4.model.CapabilityStatement;
import org.opencds.cqf.ruler.core.api.capability.CapabilityStatementExtender;
import org.opencds.cqf.ruler.core.api.config.FhirVersion;
import org.opencds.cqf.ruler.core.api.config.FhirVersionCondition;
import org.opencds.cqf.ruler.core.r4.ExtensibleJpaConformanceProviderR4;
import org.opencds.cqf.ruler.core.r4.SoftwareVersionCapabilityStatementExtender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.context.support.IValidationSupport;
import ca.uhn.fhir.cql.config.CqlR4Config;
import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.api.dao.IFhirSystemDao;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.util.ISearchParamRegistry;

@Configuration
@Import(CqlR4Config.class)
@Conditional(FhirVersionCondition.class)
@FhirVersion(FhirVersionEnum.R4)
public class CqfRulerConfigR4 {
    @Bean
    SoftwareVersionCapabilityStatementExtender softwareVersionCapabilityStatementExtender() {
        return new SoftwareVersionCapabilityStatementExtender();
    }

    @Bean
    @Lazy
    ExtensibleJpaConformanceProviderR4 extensibleJpaConformanceProvider(RestfulServer theRestfulServer, IFhirSystemDao<?, ?> theSystemDao,
    DaoConfig theDaoConfig, ISearchParamRegistry theSearchParamRegistry,
    IValidationSupport theValidationSupport, List<CapabilityStatementExtender<CapabilityStatement>> extenders) {
        return new ExtensibleJpaConformanceProviderR4(theRestfulServer, theSystemDao, theDaoConfig, theSearchParamRegistry, theValidationSupport, extenders);
    }
}
