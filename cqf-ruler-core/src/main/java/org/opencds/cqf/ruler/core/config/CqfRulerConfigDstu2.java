package org.opencds.cqf.ruler.core.config;

import java.util.List;

import org.opencds.cqf.ruler.core.api.capability.CapabilityStatementExtender;
import org.opencds.cqf.ruler.core.api.config.FhirVersion;
import org.opencds.cqf.ruler.core.api.config.FhirVersionCondition;
import org.opencds.cqf.ruler.core.dstu2.ExtensibleJpaConformanceProviderDstu2;
import org.opencds.cqf.ruler.core.dstu2.SoftwareVersionConformanceExtender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.api.dao.IFhirSystemDao;
import ca.uhn.fhir.model.dstu2.composite.MetaDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Conformance;
import ca.uhn.fhir.rest.server.RestfulServer;


@Configuration
// @Import(CqlDstu2Config.class) -- TODO: Add this if hapi-fhir-jpaserver-cql ever supports DSTU2
@Conditional(FhirVersionCondition.class)
@FhirVersion(FhirVersionEnum.DSTU2)
public class CqfRulerConfigDstu2 {
    @Bean
    SoftwareVersionConformanceExtender softwareVersionConformanceExtender() {
        return new SoftwareVersionConformanceExtender();
    }

    @Bean
    @Lazy
    ExtensibleJpaConformanceProviderDstu2 extensibleJpaConformanceProvider(RestfulServer theRestfulServer, IFhirSystemDao<Bundle, MetaDt> theSystemDao, DaoConfig theDaoConfig, List<CapabilityStatementExtender<Conformance>> extenders) {
        return new ExtensibleJpaConformanceProviderDstu2(theRestfulServer, theSystemDao, theDaoConfig, extenders);
    }
}
