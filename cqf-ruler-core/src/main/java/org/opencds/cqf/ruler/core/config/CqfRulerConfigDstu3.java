package org.opencds.cqf.ruler.core.config;

import java.util.List;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CapabilityStatement;
import org.hl7.fhir.dstu3.model.Meta;
import org.opencds.cqf.ruler.core.api.capability.CapabilityStatementExtender;
import org.opencds.cqf.ruler.core.api.config.FhirVersion;
import org.opencds.cqf.ruler.core.api.config.FhirVersionCondition;
import org.opencds.cqf.ruler.core.dstu3.ExtensibleJpaConformanceProviderDstu3;
import org.opencds.cqf.ruler.core.dstu3.SoftwareVersionCapabilityStatementExtender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.cql.config.CqlDstu3Config;
import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.api.dao.IFhirSystemDao;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.util.ISearchParamRegistry;

@Configuration
@Import(CqlDstu3Config.class)
@Conditional(FhirVersionCondition.class)
@FhirVersion(FhirVersionEnum.DSTU3)
public class CqfRulerConfigDstu3 {
    @Bean
    SoftwareVersionCapabilityStatementExtender softwareVersionCapabilityStatementExtender() {
        return new SoftwareVersionCapabilityStatementExtender();
    }

    @Bean
    @Lazy
    ExtensibleJpaConformanceProviderDstu3 extensibleJpaConformanceProvider(RestfulServer theRestfulServer, IFhirSystemDao<Bundle, Meta> theSystemDao, DaoConfig theDaoConfig, ISearchParamRegistry theSearchParamRegistry, List<CapabilityStatementExtender<CapabilityStatement>> extenders) {
        return new ExtensibleJpaConformanceProviderDstu3(theRestfulServer, theSystemDao, theDaoConfig, theSearchParamRegistry, extenders);
    }
}
