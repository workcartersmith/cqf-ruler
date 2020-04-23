package org.opencds.cqf.r4.servlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.alphora.cql.service.factory.DataProviderFactory;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.Library;
import org.hl7.fhir.r4.model.Measure;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.ValueSet;
import org.opencds.cqf.common.config.HapiProperties;
import org.opencds.cqf.common.factories.DefaultDataProviderFactory;
import org.opencds.cqf.common.retrieve.JpaFhirRetrieveProvider;
import org.opencds.cqf.cql.searchparam.SearchParameterResolver;
import org.opencds.cqf.library.r4.NarrativeProvider;
import org.opencds.cqf.measure.r4.CodeTerminologyRef;
import org.opencds.cqf.measure.r4.CqfMeasure;
import org.opencds.cqf.measure.r4.PopulationCriteriaMap;
import org.opencds.cqf.measure.r4.VersionedTerminologyRef;
import org.opencds.cqf.r4.providers.*;
import org.springframework.context.ApplicationContext;
import org.springframework.web.cors.CorsConfiguration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.jpa.dao.DaoRegistry;
import ca.uhn.fhir.jpa.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.BaseJpaResourceProvider;
import ca.uhn.fhir.jpa.provider.TerminologyUploaderProvider;
import ca.uhn.fhir.jpa.provider.r4.JpaConformanceProviderR4;
import ca.uhn.fhir.jpa.provider.r4.JpaSystemProviderR4;
import ca.uhn.fhir.jpa.rp.r4.LibraryResourceProvider;
import ca.uhn.fhir.jpa.rp.r4.MeasureResourceProvider;
import ca.uhn.fhir.jpa.rp.r4.ValueSetResourceProvider;
import ca.uhn.fhir.jpa.search.DatabaseBackedPagingProvider;
import ca.uhn.fhir.jpa.term.api.ITermReadSvcR4;
import ca.uhn.fhir.jpa.util.ResourceProviderFactory;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.server.HardcodedServerAddressStrategy;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;
import org.hl7.fhir.r4.model.*;

public class BaseServlet extends RestfulServer {
    DaoRegistry registry;
    FhirContext fhirContext;

    @SuppressWarnings("unchecked")
    @Override
    protected void initialize() throws ServletException {
        super.initialize();

        // System level providers
        ApplicationContext appCtx = (ApplicationContext) getServletContext()
                .getAttribute("org.springframework.web.context.WebApplicationContext.ROOT");

        // Fhir Context
        this.fhirContext = appCtx.getBean(FhirContext.class);
        this.fhirContext.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        this.fhirContext.getRestfulClientFactory().setSocketTimeout(100 * 1000);
		this.fhirContext.registerCustomType(VersionedTerminologyRef.class);
		this.fhirContext.registerCustomType(CodeTerminologyRef.class);
		this.fhirContext.registerCustomType(PopulationCriteriaMap.class);
		this.fhirContext.registerCustomType(CqfMeasure.class);
        setFhirContext(this.fhirContext);


        // System and Resource Daos
        IFhirSystemDao<Bundle, Meta> systemDao = appCtx.getBean("mySystemDaoR4", IFhirSystemDao.class);
        this.registry = appCtx.getBean(DaoRegistry.class);

        // System and Resource Providers
        Object systemProvider = appCtx.getBean("mySystemProviderR4", JpaSystemProviderR4.class);
        registerProvider(systemProvider);


        ResourceProviderFactory resourceProviders = appCtx.getBean("myResourceProvidersR4", ResourceProviderFactory.class);
        registerProviders(resourceProviders.createProviders());

        JpaConformanceProviderR4 confProvider = new JpaConformanceProviderR4(this, systemDao, appCtx.getBean(DaoConfig.class));
        confProvider.setImplementationDescription("CQF Ruler FHIR R4 Server");
        setServerConformanceProvider(confProvider);

        JpaTerminologyProvider localSystemTerminologyProvider = new JpaTerminologyProvider(appCtx.getBean("terminologyService",  ITermReadSvcR4.class), getFhirContext(), (ValueSetResourceProvider)this.getResourceProvider(ValueSet.class));

        Map<String, Endpoint> endpointIndex = new HashMap<String, Endpoint>();
        DataProviderFactory dataProviderFactory = new DefaultDataProviderFactory<Endpoint>(registry, fhirContext, endpointIndex, null);
        resolveProviders(dataProviderFactory, localSystemTerminologyProvider, this.registry);

        // CdsHooksServlet.provider = provider;

        /*
         * ETag Support
         */
        setETagSupport(HapiProperties.getEtagSupport());

        /*
         * This server tries to dynamically generate narratives
         */
        FhirContext ctx = getFhirContext();
        ctx.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());

        /*
         * Default to JSON and pretty printing
         */
        setDefaultPrettyPrint(HapiProperties.getDefaultPrettyPrint());

        /*
         * Default encoding
         */
        setDefaultResponseEncoding(HapiProperties.getDefaultEncoding());

        /*
         * This configures the server to page search results to and from
         * the database, instead of only paging them to memory. This may mean
         * a performance hit when performing searches that return lots of results,
         * but makes the server much more scalable.
         */
        setPagingProvider(appCtx.getBean(DatabaseBackedPagingProvider.class));

        /*
         * This interceptor formats the output using nice colourful
         * HTML output when the request is detected to come from a
         * browser.
         */
        ResponseHighlighterInterceptor responseHighlighterInterceptor = appCtx.getBean(ResponseHighlighterInterceptor.class);
        this.registerInterceptor(responseHighlighterInterceptor);

        /*
         * If you are hosting this server at a specific DNS name, the server will try to
         * figure out the FHIR base URL based on what the web container tells it, but
         * this doesn't always work. If you are setting links in your search bundles that
         * just refer to "localhost", you might want to use a server address strategy:
         */
        String serverAddress = HapiProperties.getServerAddress();
        if (serverAddress != null && serverAddress.length() > 0)
        {
            setServerAddressStrategy(new HardcodedServerAddressStrategy(serverAddress));
        }

        initializeLocalEndpoint(registry);

        registerProvider(appCtx.getBean(TerminologyUploaderProvider.class));

        if (HapiProperties.getCorsEnabled())
        {
            CorsConfiguration config = new CorsConfiguration();
            config.addAllowedHeader("x-fhir-starter");
            config.addAllowedHeader("Origin");
            config.addAllowedHeader("Accept");
            config.addAllowedHeader("X-Requested-With");
            config.addAllowedHeader("Content-Type");
            config.addAllowedHeader("Authorization");
            config.addAllowedHeader("Cache-Control");

            config.addAllowedOrigin(HapiProperties.getCorsAllowedOrigin());

            config.addExposedHeader("Location");
            config.addExposedHeader("Content-Location");
            config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

            // Create the interceptor and register it
            CorsInterceptor interceptor = new CorsInterceptor(config);
            registerInterceptor(interceptor);
        }
    }

	protected NarrativeProvider getNarrativeProvider() {
		return new NarrativeProvider();
	}

    // Since resource provider resolution not lazy, the providers here must be resolved in the correct
    // order of dependencies.
    private void resolveProviders(DataProviderFactory dataProviderFactory, JpaTerminologyProvider localSystemTerminologyProvider, DaoRegistry registry)
            throws ServletException
    {
        NarrativeProvider narrativeProvider = this.getNarrativeProvider();
        HQMFProvider hqmfProvider = new HQMFProvider();

        // Code System Update
        CodeSystemUpdateProvider csUpdate = new CodeSystemUpdateProvider(
            this.getDao(ValueSet.class),
            this.getDao(CodeSystem.class));
        this.registerProvider(csUpdate);

        // Cache Value Sets
        CacheValueSetsProvider cvs = new CacheValueSetsProvider(this.registry.getSystemDao(), this.getDao(Endpoint.class));
        this.registerProvider(cvs);

        //Library processing
        LibraryOperationsProvider libraryProvider = new LibraryOperationsProvider((LibraryResourceProvider)this.getResourceProvider(Library.class), narrativeProvider);
        this.registerProvider(libraryProvider);

        // CQL Execution
        CqlExecutionProvider cql = new CqlExecutionProvider(libraryProvider, dataProviderFactory, fhirContext, localSystemTerminologyProvider);
        this.registerProvider(cql);

        // Bundle processing
        ApplyCqlOperationProvider bundleProvider = new ApplyCqlOperationProvider(dataProviderFactory, localSystemTerminologyProvider, this.getDao(Bundle.class), fhirContext, cql);
        this.registerProvider(bundleProvider);

        // Measure processing
        MeasureOperationsProvider measureProvider = new MeasureOperationsProvider(this.registry, dataProviderFactory, localSystemTerminologyProvider, narrativeProvider, hqmfProvider, 
            libraryProvider, (MeasureResourceProvider)this.getResourceProvider(Measure.class), fhirContext);
        this.registerProvider(measureProvider);

        // // ActivityDefinition processing
        ActivityDefinitionApplyProvider actDefProvider = new ActivityDefinitionApplyProvider(this.fhirContext, registry, cql);
        this.registerProvider(actDefProvider);

        JpaFhirRetrieveProvider localSystemRetrieveProvider = new JpaFhirRetrieveProvider(registry, new SearchParameterResolver(this.fhirContext));

        // PlanDefinition processing
        PlanDefinitionApplyProvider planDefProvider = new PlanDefinitionApplyProvider(this.fhirContext, actDefProvider, registry, cql);
        this.registerProvider(planDefProvider);

        CarePlanProvider carePlanProvider = new CarePlanProvider(this.fhirContext, registry);
        this.registerProvider(carePlanProvider);

        TaskProvider taskProvider = new TaskProvider(this.fhirContext, registry);
        this.registerProvider(taskProvider);

        // Notify *not yet ready*
        NotifyProvider notifyProvider = new NotifyProvider(registry, libraryProvider, dataProviderFactory, fhirContext, localSystemTerminologyProvider);
        this.registerProvider(notifyProvider);

        CdsHooksServlet.setPlanDefinitionProvider(planDefProvider.getProcessor());
        CdsHooksServlet.setLibraryResolutionProvider(libraryProvider);
        CdsHooksServlet.setSystemTerminologyProvider(localSystemTerminologyProvider);
        CdsHooksServlet.setSystemRetrieveProvider(localSystemRetrieveProvider);
    }

    private void initializeLocalEndpoint(DaoRegistry registry) {
        Endpoint dataEndpoint = new Endpoint();
            dataEndpoint.setId("local-endpoint");
            dataEndpoint.setName("Local Endpoint");
            //Should make a local Endpoint helper??
            Coding connectionTypeCoding = new Coding();
            connectionTypeCoding.setSystem("http://terminology.hl7.org/CodeSystem/endpoint-connection-type");
            connectionTypeCoding.setCode("hl7-fhir-rest");
            List<CodeableConcept> payloadTypeCodeableConcepts = new ArrayList<CodeableConcept>();
            CodeableConcept payloadTypeCodeableConcept = new CodeableConcept();
            Coding payloadTypeCarePlanCoding = new Coding();
            payloadTypeCarePlanCoding.setSystem("http://hl7.org/fhir/resource-types");
            payloadTypeCarePlanCoding.setCode("CarePlan");
            Coding payloadTypeTaskCoding = new Coding();
            payloadTypeTaskCoding.setSystem("http://hl7.org/fhir/resource-types");
            payloadTypeTaskCoding.setCode("Task");
            Coding payloadTypeLibraryCoding = new Coding();
            payloadTypeLibraryCoding.setSystem("http://hl7.org/fhir/resource-types");
            payloadTypeLibraryCoding.setCode("Library");
            Coding payloadTypeMeasureCoding = new Coding();
            payloadTypeMeasureCoding.setSystem("http://hl7.org/fhir/resource-types");
            payloadTypeMeasureCoding.setCode("Measure");
            Coding payloadTypePlanDefinitionCoding = new Coding();
            payloadTypePlanDefinitionCoding.setSystem("http://hl7.org/fhir/resource-types");
            payloadTypePlanDefinitionCoding.setCode("PlanDefinition");
            payloadTypeCodeableConcept.addCoding(payloadTypeCarePlanCoding);
            payloadTypeCodeableConcept.addCoding(payloadTypeTaskCoding);
            payloadTypeCodeableConcept.addCoding(payloadTypeLibraryCoding);
            payloadTypeCodeableConcept.addCoding(payloadTypeMeasureCoding);
            payloadTypeCodeableConcept.addCoding(payloadTypePlanDefinitionCoding);
            payloadTypeCodeableConcepts.add(payloadTypeCodeableConcept);

            dataEndpoint.setConnectionType(connectionTypeCoding);
            dataEndpoint.setPayloadType(payloadTypeCodeableConcepts);
            dataEndpoint.setAddress(HapiProperties.getServerAddress());
            registry.getResourceDao(Endpoint.class).update(dataEndpoint);
    }

    protected <T extends IBaseResource> IFhirResourceDao<T> getDao(Class<T> clazz) {
        return this.registry.getResourceDao(clazz);
    }


    protected <T extends IBaseResource> BaseJpaResourceProvider<T>  getResourceProvider(Class<T> clazz) {
        return (BaseJpaResourceProvider<T> ) this.getResourceProviders().stream()
        .filter(x -> x.getResourceType().getSimpleName().equals(clazz.getSimpleName())).findFirst().get();
    }
}
