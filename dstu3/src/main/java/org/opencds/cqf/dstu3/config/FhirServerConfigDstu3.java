package org.opencds.cqf.dstu3.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.cqframework.cql.cql2elm.CqlTranslatorOptions;
import org.cqframework.cql.cql2elm.model.Model;
import org.cqframework.cql.elm.execution.Library;
import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.common.config.HapiProperties;
import org.opencds.cqf.common.providers.CacheAwareTerminologyProvider;
import org.opencds.cqf.common.retrieve.JpaFhirRetrieveProvider;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.fhir.model.Dstu3FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;
import org.opencds.cqf.cql.engine.model.ModelResolver;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;
import org.opencds.cqf.cql.evaluator.engine.model.CachingModelResolverDecorator;
import org.opencds.cqf.dstu3.providers.ActivityDefinitionApplyProvider;
import org.opencds.cqf.dstu3.providers.ApplyCqlOperationProvider;
import org.opencds.cqf.dstu3.providers.CacheValueSetsProvider;
import org.opencds.cqf.dstu3.providers.CodeSystemUpdateProvider;
import org.opencds.cqf.dstu3.providers.CqlExecutionProvider;
import org.opencds.cqf.dstu3.providers.LibraryOperationsProvider;
import org.opencds.cqf.dstu3.providers.MeasureOperationsProvider;
import org.opencds.cqf.dstu3.providers.ObservationProvider;
import org.opencds.cqf.dstu3.providers.PlanDefinitionApplyProvider;
import org.opencds.cqf.dstu3.providers.QuestionnaireProvider;
import org.opencds.cqf.tooling.library.stu3.NarrativeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.cql.dstu3.listener.ElmCacheResourceChangeListener;
import ca.uhn.fhir.cql.dstu3.provider.JpaTerminologyProvider;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.cache.IResourceChangeListenerRegistry;
import ca.uhn.fhir.jpa.config.BaseJavaConfigDstu3;
import ca.uhn.fhir.jpa.search.DatabaseBackedPagingProvider;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;

@Configuration
@ComponentScan(basePackages = "org.opencds.cqf.dstu3")
public class FhirServerConfigDstu3 extends BaseJavaConfigDstu3 {
    protected final DataSource myDataSource;

    @Autowired
    public FhirServerConfigDstu3(DataSource myDataSource) {
        this.myDataSource = myDataSource;
    }

    /**
     * We override the paging provider definition so that we can customize the
     * default/max page sizes for search results. You can set these however you
     * want, although very large page sizes will require a lot of RAM.
     */
    @Override
    public DatabaseBackedPagingProvider databaseBackedPagingProvider() {
        DatabaseBackedPagingProvider pagingProvider = super.databaseBackedPagingProvider();
        pagingProvider.setDefaultPageSize(HapiProperties.getDefaultPageSize());
        pagingProvider.setMaximumPageSize(HapiProperties.getMaximumPageSize());
        return pagingProvider;
    }

    @Override
    @Bean()
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean retVal = super.entityManagerFactory();
        retVal.setPersistenceUnitName(HapiProperties.getPersistenceUnitName());

        try {
            retVal.setDataSource(myDataSource);
        } catch (Exception e) {
            throw new ConfigurationException("Could not set the data source due to a configuration issue", e);
        }

        retVal.setJpaProperties(HapiProperties.getProperties());
        return retVal;
    }

    @Bean
    @Primary
    public JpaTransactionManager hapiTransactionManager(EntityManagerFactory entityManagerFactory) {
      JpaTransactionManager retVal = new JpaTransactionManager();
      retVal.setEntityManagerFactory(entityManagerFactory);
      return retVal;
    }

    @Bean(name= "myOperationProvidersDstu3")
    public List<Class<?>> operationProviders() {
        // TODO: Make this registry dynamic
        // Scan an interface, create a plugin-api, etc.
        // Basically, anything that's not included in base HAPI and implements an operation.
        List<Class<?>> classes = new ArrayList<>();
        classes.add(ActivityDefinitionApplyProvider.class);
        classes.add(ApplyCqlOperationProvider.class);
        classes.add(CacheValueSetsProvider.class);
        classes.add(CodeSystemUpdateProvider.class);
        classes.add(CqlExecutionProvider.class);
        classes.add(LibraryOperationsProvider.class);
        classes.add(MeasureOperationsProvider.class);
        classes.add(PlanDefinitionApplyProvider.class);

        // The plugin API will need to a way to determine whether a particular
        // service should be registered
        if(HapiProperties.getQuestionnaireResponseExtractEnabled()) { 
            classes.add(QuestionnaireProvider.class);
        };        

        if (HapiProperties.getObservationTransformEnabled()) {
            classes.add(ObservationProvider.class);
        }

        return classes;
    }

    @Bean() 
    public NarrativeProvider narrativeProvider() {
        return new NarrativeProvider();
    }

    @Bean
    public JpaTerminologyProvider terminologyProvider(ca.uhn.fhir.jpa.term.api.ITermReadSvcDstu3 theTerminologySvc, ca.uhn.fhir.jpa.api.dao.DaoRegistry theDaoRegistry, ca.uhn.fhir.context.support.IValidationSupport theValidationSupport) {
        return new JpaTerminologyProvider(theTerminologySvc, theDaoRegistry, theValidationSupport);
    }

    @Bean
    @Primary
    public TerminologyProvider terminologyProvider(Map<String, Iterable<Code>> terminologyCache, JpaTerminologyProvider jpaTerminologyProvider) {
        return new CacheAwareTerminologyProvider(terminologyCache, jpaTerminologyProvider);
    }

    @Bean SearchParameterResolver searchParameterResolver(FhirContext fhirContext) {
        return new SearchParameterResolver(fhirContext);
    }

    // TODO: Respect config options
    @Bean
    public DataProvider dataProvider(ModelResolver modelResolver, DaoRegistry daoRegistry, SearchParameterResolver searchParameterResolver, TerminologyProvider terminologyProvider) {
        JpaFhirRetrieveProvider retrieveProvider = new JpaFhirRetrieveProvider(daoRegistry, searchParameterResolver);
        retrieveProvider.setTerminologyProvider(terminologyProvider);
        retrieveProvider.setExpandValueSets(true);
        return new CompositeDataProvider(modelResolver, retrieveProvider);
    }
    

    @Bean(name = "dstu3ModelResolver")
    public ModelResolver modelResolver() {
        return new CachingModelResolverDecorator(new Dstu3FhirModelResolver());
    }


	@Lazy
	@Bean
	public org.opencds.cqf.dstu3.helpers.LibraryHelper libraryHelper(Map<VersionedIdentifier, Model> globalModelCache, Map<org.cqframework.cql.elm.execution.VersionedIdentifier, Library> globalLibraryCache, CqlTranslatorOptions cqlTranslatorOptions) {
		return new org.opencds.cqf.dstu3.helpers.LibraryHelper(globalModelCache, globalLibraryCache, cqlTranslatorOptions);
	}


	@Bean
	public CqlTranslatorOptions cqlTranslatorOptions() {
		return CqlTranslatorOptions.defaultOptions(); //.withCompatibilityLevel("1.3");
	}

	@Bean
	public ElmCacheResourceChangeListener elmCacheResourceChangeListener(IResourceChangeListenerRegistry resourceChangeListenerRegistry, IFhirResourceDao<org.hl7.fhir.dstu3.model.Library> libraryDao,  Map<org.cqframework.cql.elm.execution.VersionedIdentifier, Library> globalLibraryCache) {
		ElmCacheResourceChangeListener listener = new ElmCacheResourceChangeListener(libraryDao, globalLibraryCache);
		resourceChangeListenerRegistry.registerResourceResourceChangeListener("Library", SearchParameterMap.newSynchronous(), listener, 1000);
		return listener;
	}
}
