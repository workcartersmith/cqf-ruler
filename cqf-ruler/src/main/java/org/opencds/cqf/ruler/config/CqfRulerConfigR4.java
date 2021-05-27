package org.opencds.cqf.ruler.config;


import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.hl7.fhir.r4.model.PlanDefinition;
import org.opencds.cqf.cql.engine.execution.LibraryLoader;
import org.opencds.cqf.cql.engine.fhir.converter.FhirTypeConverter;
import org.opencds.cqf.cql.engine.fhir.model.R4FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;
import org.opencds.cqf.cql.engine.model.ModelResolver;
import org.opencds.cqf.cql.evaluator.activitydefinition.r4.ActivityDefinitionProcessor;
import org.opencds.cqf.cql.evaluator.builder.Constants;
import org.opencds.cqf.cql.evaluator.builder.DataProviderFactory;
import org.opencds.cqf.cql.evaluator.builder.FhirDalFactory;
import org.opencds.cqf.cql.evaluator.builder.ModelResolverFactory;
import org.opencds.cqf.cql.evaluator.builder.TerminologyProviderFactory;
import org.opencds.cqf.cql.evaluator.builder.dal.TypedFhirDalFactory;
import org.opencds.cqf.cql.evaluator.builder.data.TypedRetrieveProviderFactory;
import org.opencds.cqf.cql.evaluator.builder.library.TypedLibraryContentProviderFactory;
import org.opencds.cqf.cql.evaluator.builder.terminology.TypedTerminologyProviderFactory;
import org.opencds.cqf.cql.evaluator.cql2elm.util.LibraryVersionSelector;
import org.opencds.cqf.cql.evaluator.engine.model.CachingModelResolverDecorator;
import org.opencds.cqf.cql.evaluator.fhir.adapter.r4.AdapterFactory;
import org.opencds.cqf.cql.evaluator.library.LibraryProcessor;
import org.opencds.cqf.cql.evaluator.spring.EvaluatorConfiguration;
import org.opencds.cqf.ruler.common.dal.RulerDal;
import org.opencds.cqf.ruler.common.factory.DefaultingDataProviderFactory;
import org.opencds.cqf.ruler.common.factory.DefaultingFhirDalFactory;
import org.opencds.cqf.ruler.common.factory.DefaultingLibraryLoaderFactory;
import org.opencds.cqf.ruler.common.factory.DefaultingTerminologyProviderFactory;
import org.opencds.cqf.ruler.r4.config.OperationsProviderLoaderR4;
import org.opencds.cqf.ruler.r4.providers.CodeSystemUpdateProvider;
import org.opencds.cqf.ruler.r4.providers.PlanDefinitionApplyProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.cql.common.provider.LibraryResolutionProvider;
import ca.uhn.fhir.cql.common.retrieve.JpaFhirRetrieveProvider;
import ca.uhn.fhir.cql.r4.helper.LibraryHelper;
import ca.uhn.fhir.cql.r4.provider.JpaTerminologyProvider;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.starter.cql.StarterCqlR4Config;

@Configuration
@Conditional(ca.uhn.fhir.jpa.starter.annotations.OnR4Condition.class)
@Import({StarterCqlR4Config.class, EvaluatorConfiguration.class})
public class CqfRulerConfigR4 {

  @Bean
  OperationsProviderLoaderR4 operationsProviderLoader() {
    return new OperationsProviderLoaderR4();
  }

  @Bean
  PlanDefinitionApplyProvider planDefinitionApplyProvider(RulerDal fhirDal, FhirContext fhirContext, ActivityDefinitionProcessor activityDefinitionProcessor,
  LibraryProcessor libraryProcessor, IFhirResourceDao<PlanDefinition> planDefinitionDao, AdapterFactory adapterFactory, FhirTypeConverter fhirTypeConverter) {
    return new PlanDefinitionApplyProvider(fhirDal, fhirContext, activityDefinitionProcessor, libraryProcessor, planDefinitionDao, adapterFactory, fhirTypeConverter);
  }


  @Bean
  ActivityDefinitionProcessor activityDefinitionProcessor(FhirContext fhirContext, RulerDal fhirDal, LibraryProcessor libraryProcessor) {
    return new ActivityDefinitionProcessor(fhirContext, fhirDal, libraryProcessor);
  }

  @Primary
  @Bean
  TerminologyProviderFactory defaultingTerminologyProviderFactory(FhirContext fhirContext,
  Set<TypedTerminologyProviderFactory> terminologyProviderFactories, JpaTerminologyProvider jpaTerminologyProvider) {
    return new DefaultingTerminologyProviderFactory(fhirContext, terminologyProviderFactories, jpaTerminologyProvider);
  }

  @Primary
  @Bean
  DataProviderFactory defaultingDataProviderFactory(FhirContext fhirContext, Set<ModelResolverFactory> modelResolverFactories,
  Set<TypedRetrieveProviderFactory> retrieveProviderFactories, ModelResolver modelResolver, JpaFhirRetrieveProvider retrieveProvider) {
    return new DefaultingDataProviderFactory(fhirContext, modelResolverFactories, retrieveProviderFactories, Triple.of(Constants.FHIR_MODEL_URI, modelResolver, retrieveProvider));
  }

  @Primary
  @Bean
  FhirDalFactory defaultingFhirDalFactory(Set<TypedFhirDalFactory> fhirDalFactories, RulerDal rulerDal) {
    return new DefaultingFhirDalFactory(fhirDalFactories, rulerDal);
  }

  @Primary
  @Bean
  DefaultingLibraryLoaderFactory defaultingLibraryLoaderFactory(FhirContext fhirContext, AdapterFactory adapterFactory,
  Set<TypedLibraryContentProviderFactory> libraryContentProviderFactories,
  LibraryVersionSelector libraryVersionSelector, LibraryLoader libraryLoader) {
    return new DefaultingLibraryLoaderFactory(fhirContext, adapterFactory, libraryContentProviderFactories, libraryVersionSelector, libraryLoader);
  }

  @Bean
  @SuppressWarnings({"rawtypes", "unchecked"})
  LibraryLoader defaultLibraryLoader(LibraryHelper libraryHelper, LibraryResolutionProvider libraryResolutionProvider) {
    return libraryHelper.createLibraryLoader(libraryResolutionProvider);
  }

  @Bean
  ModelResolver modelResolver() {
    return new CachingModelResolverDecorator(new R4FhirModelResolver());
  }

  @Bean
  JpaFhirRetrieveProvider jpaFhirRetrieveProvider(FhirContext fhirContext, DaoRegistry daoRegistry, JpaTerminologyProvider jpaTerminologyProvider) {
    JpaFhirRetrieveProvider retrieveProvider = new JpaFhirRetrieveProvider(daoRegistry,
            new SearchParameterResolver(fhirContext));
    retrieveProvider.setTerminologyProvider(jpaTerminologyProvider);
    retrieveProvider.setExpandValueSets(true);
    return retrieveProvider;
  }

  @Bean
  public CodeSystemUpdateProvider codeSystemUpdateProvider(DaoRegistry daoRegistry) {
      return new CodeSystemUpdateProvider(daoRegistry);
  }

  @Bean
  public RulerDal rulerDal(DaoRegistry daoRegistry) {
    return new RulerDal(daoRegistry);
  }

}