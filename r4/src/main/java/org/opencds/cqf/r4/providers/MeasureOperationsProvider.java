package org.opencds.cqf.r4.providers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.opencds.cqf.cql.service.factory.DataProviderFactory;

import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Measure;
import org.hl7.fhir.r4.model.MeasureReport;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.opencds.cqf.common.providers.LibraryResolutionProvider;
import org.opencds.cqf.library.r4.NarrativeProvider;
import org.opencds.cqf.r4.processors.MeasureOperationsProcessor;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.dao.DaoRegistry;
import ca.uhn.fhir.jpa.rp.r4.MeasureResourceProvider;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;

public class MeasureOperationsProvider {

    private MeasureOperationsProcessor measureOperationsProcessor;
    private static final Logger logger = LoggerFactory.getLogger(MeasureOperationsProvider.class);

    public MeasureOperationsProvider(DaoRegistry registry, DataProviderFactory dataProviderFactory, TerminologyProvider localSystemTerminologyProvider, NarrativeProvider narrativeProvider, HQMFProvider hqmfProvider, LibraryResolutionProvider<org.hl7.fhir.r4.model.Library> libraryResolutionProvider,
    MeasureResourceProvider measureResourceProvider, FhirContext fhirContext) {
        measureOperationsProcessor = new MeasureOperationsProcessor(registry, dataProviderFactory, localSystemTerminologyProvider, narrativeProvider, hqmfProvider, libraryResolutionProvider, measureResourceProvider, fhirContext);
    }

    @Operation(name = "$hqmf", idempotent = true, type = Measure.class)
    public Parameters hqmf(@IdParam IdType theId) {
        return measureOperationsProcessor.hqmf(theId);
    }

    @Operation(name = "$refresh-generated-content", type = Measure.class)
    public MethodOutcome refreshGeneratedContent(HttpServletRequest theRequest, RequestDetails theRequestDetails,
            @IdParam IdType theId) {
        return measureOperationsProcessor.refreshGeneratedContent(theRequest, theRequestDetails, theId);
    }

    @Operation(name = "$get-narrative", idempotent = true, type = Measure.class)
    public Parameters getNarrative(@IdParam IdType theId) {
        return measureOperationsProcessor.getNarrative(theId);
    }

    /*
     *
     * NOTE that the source, user, and pass parameters are not standard parameters
     * for the FHIR $evaluate-measure operation
     *
     */
    @Operation(name = "$evaluate-measure", idempotent = true, type = Measure.class)
    public MeasureReport evaluateMeasure(@IdParam IdType theId, @OperationParam(name = "periodStart") String periodStart,
            @OperationParam(name = "periodEnd") String periodEnd, @OperationParam(name = "measure") String measureRef,
            @OperationParam(name = "reportType") String reportType, @OperationParam(name = "patient") String patientRef,
            @OperationParam(name = "productLine") String productLine,
            @OperationParam(name = "practitioner") String practitionerRef,
            @OperationParam(name = "lastReceivedOn") String lastReceivedOn,
            @OperationParam(name = "endpoint") Endpoint endpoint) throws InternalErrorException, FHIRException {
        return measureOperationsProcessor.evaluateMeasure(theId, periodStart, periodEnd, measureRef, reportType, patientRef, productLine, practitionerRef, lastReceivedOn, endpoint);
    }

    // @Operation(name = "$evaluate-measure-with-source", idempotent = true)
    // public MeasureReport evaluateMeasure(@IdParam IdType theId,
    //         @OperationParam(name = "sourceData", min = 1, max = 1, type = Bundle.class) Bundle sourceData,
    //         @OperationParam(name = "periodStart", min = 1, max = 1) String periodStart,
    //         @OperationParam(name = "periodEnd", min = 1, max = 1) String periodEnd) {
    //     return measureOperationsProcessor.evaluateMeasure(theId, sourceData, periodStart, periodEnd);
    // }

    @Operation(name = "$care-gaps", idempotent = true, type = Measure.class)
    public Bundle careGapsReport(@OperationParam(name = "periodStart") String periodStart,
            @OperationParam(name = "periodEnd") String periodEnd, @OperationParam(name = "topic") String topic,
            @OperationParam(name = "patient") String patientRef, @OperationParam(name = "endpoint") Endpoint endpoint) {
        return measureOperationsProcessor.careGapsReport(periodStart, periodEnd, topic, patientRef, endpoint);
    }

    @Operation(name = "$collect-data", idempotent = true, type = Measure.class)
    public Parameters collectData(@IdParam IdType theId, @OperationParam(name = "periodStart") String periodStart,
            @OperationParam(name = "periodEnd") String periodEnd, @OperationParam(name = "patient") String patientRef,
            @OperationParam(name = "practitioner") String practitionerRef,
            @OperationParam(name = "lastReceivedOn") String lastReceivedOn, @OperationParam(name = "endpoint") Endpoint endpoint) throws FHIRException {
        // TODO: Spec says that the periods are not required, but I am not sure what to
        // do when they aren't supplied so I made them required
        return measureOperationsProcessor.collectData(theId, periodStart, periodEnd, patientRef, practitionerRef, lastReceivedOn, endpoint);
    }

    // TODO - this needs a lot of work
    @Operation(name = "$data-requirements", idempotent = true, type = Measure.class)
    public org.hl7.fhir.r4.model.Library dataRequirements(@IdParam IdType theId,
            @OperationParam(name = "startPeriod") String startPeriod,
            @OperationParam(name = "endPeriod") String endPeriod) throws InternalErrorException, FHIRException {
        return measureOperationsProcessor.dataRequirements(theId, startPeriod, endPeriod);
    }

    @Operation(name = "$submit-data", idempotent = true, type = Measure.class)
    public Resource submitData(RequestDetails details, @IdParam IdType theId,
            @OperationParam(name = "measurereport", min = 1, max = 1, type = MeasureReport.class) MeasureReport report,
            @OperationParam(name = "resource") List<IAnyResource> resources) {
        /*
         * TODO - resource validation using $data-requirements operation (params are the
         * provided id and the measurement period from the MeasureReport)
         * 
         * TODO - profile validation ... not sure how that would work ... (get
         * StructureDefinition from URL or must it be stored in Ruler?)
         */

        return measureOperationsProcessor.submitData(details, theId, report, resources);
    }
}
