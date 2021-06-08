package org.opencds.cqf.ruler.developer.config;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Endpoint;
import org.opencds.cqf.ruler.core.api.config.FhirVersion;
import org.opencds.cqf.ruler.core.api.config.FhirVersionCondition;
import org.opencds.cqf.ruler.developer.dstu3.ApplyCqlOperationProvider;
import org.opencds.cqf.ruler.developer.dstu3.CacheValueSetsProvider;
import org.opencds.cqf.ruler.developer.dstu3.CodeSystemUpdateProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.cql.common.provider.EvaluationProviderFactory;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.api.dao.IFhirSystemDao;

@Configuration
@Conditional(FhirVersionCondition.class)
@FhirVersion(FhirVersionEnum.DSTU3)
public class DeveloperConfigDstu3 {
    @Bean
    CodeSystemUpdateProvider codeSystemUpdateProviderDstu3(DaoRegistry daoRegistry){
        return new CodeSystemUpdateProvider(daoRegistry);
    }

    @Bean
    ApplyCqlOperationProvider applyCqlOperationProviderDstu3(EvaluationProviderFactory providerFactory, IFhirResourceDao<Bundle> bundleDao, FhirContext fhirContext){
        return new ApplyCqlOperationProvider(providerFactory, bundleDao, fhirContext);
    }

    @Bean
    CacheValueSetsProvider cacheValueSetsProviderDstu3(IFhirSystemDao<Bundle, ?> systemDao, IFhirResourceDao<Endpoint> endpointDao){
        return new CacheValueSetsProvider(systemDao, endpointDao);
    }
}
