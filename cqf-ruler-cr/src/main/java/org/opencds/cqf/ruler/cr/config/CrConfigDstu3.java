package org.opencds.cqf.ruler.cr.config;

import org.hl7.fhir.dstu3.model.Library;
import org.opencds.cqf.ruler.core.api.config.FhirVersion;
import org.opencds.cqf.ruler.core.api.config.FhirVersionCondition;
import org.opencds.cqf.ruler.cr.dstu3.DataRequirementsProvider;
import org.opencds.cqf.ruler.cr.dstu3.MeasureOperationsProvider;
import org.opencds.cqf.tooling.library.stu3.NarrativeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.cql.common.provider.EvaluationProviderFactory;
import ca.uhn.fhir.cql.common.provider.LibraryResolutionProvider;
import ca.uhn.fhir.cql.dstu3.helper.LibraryHelper;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.rp.dstu3.MeasureResourceProvider;

@Configuration
@Conditional(FhirVersionCondition.class)
@FhirVersion(FhirVersionEnum.DSTU3)
public class CrConfigDstu3 {

    @Bean
    MeasureOperationsProvider measureOperationsProviderCr(DaoRegistry registry, EvaluationProviderFactory factory,
            NarrativeProvider narrativeProvider, LibraryResolutionProvider<Library> libraryResolutionProvider,
            MeasureResourceProvider measureResourceProvider,
            ca.uhn.fhir.cql.dstu3.provider.MeasureOperationsProvider measureOperationsProvider,
            DataRequirementsProvider dataRequirementsProvider) {
        return new MeasureOperationsProvider(registry, factory, narrativeProvider, libraryResolutionProvider,
                measureResourceProvider, measureOperationsProvider, dataRequirementsProvider);
    }

    @Bean
    DataRequirementsProvider dataRequirementsProviderCr(LibraryHelper libraryHelper) {
        return new DataRequirementsProvider(libraryHelper);
    }

    @Bean
    NarrativeProvider narrativeProvider() {
        return new NarrativeProvider();
    }
}
