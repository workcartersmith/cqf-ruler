package org.opencds.cqf.ruler.core.config;

import java.util.List;

import org.hl7.fhir.r5.model.CapabilityStatement;
import org.opencds.cqf.ruler.core.api.capability.CapabilityStatementExtender;
import org.opencds.cqf.ruler.core.api.config.FhirVersion;
import org.opencds.cqf.ruler.core.api.config.FhirVersionCondition;
import org.opencds.cqf.ruler.core.r5.ExtensibleJpaConformanceProviderR5;
import org.opencds.cqf.ruler.core.r5.SoftwareVersionCapabilityStatementExtender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.context.support.IValidationSupport;
import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.api.dao.IFhirSystemDao;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.util.ISearchParamRegistry;

@Configuration
// @Import(CqlR5Config.class) -- TODO: Add when hapi-fhir-jpaserver-cql supports it
@Conditional(FhirVersionCondition.class)
@FhirVersion(FhirVersionEnum.R5)
public class CqfRulerConfigR5 {

    @Bean
    SoftwareVersionCapabilityStatementExtender softwareVersionCapabilityStatementExtender() {
        return new SoftwareVersionCapabilityStatementExtender();
    }

    @Bean
    @Lazy
    ExtensibleJpaConformanceProviderR5 extensibleJpaConformanceProvider(RestfulServer theRestfulServer, IFhirSystemDao<?, ?> theSystemDao,
    DaoConfig theDaoConfig, ISearchParamRegistry theSearchParamRegistry,
    IValidationSupport theValidationSupport, List<CapabilityStatementExtender<CapabilityStatement>> extenders) {
        return new ExtensibleJpaConformanceProviderR5(theRestfulServer, theSystemDao, theDaoConfig, theSearchParamRegistry, theValidationSupport, extenders);
    }
}
