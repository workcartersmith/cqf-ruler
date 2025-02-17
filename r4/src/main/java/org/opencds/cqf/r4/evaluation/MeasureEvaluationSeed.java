package org.opencds.cqf.r4.evaluation;

import java.util.Date;
import java.util.List;

import ca.uhn.fhir.jpa.partition.SystemRequestDetails;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import org.apache.commons.lang3.tuple.Triple;
import org.cqframework.cql.elm.execution.Library;
import org.hl7.fhir.r4.model.Measure;
import org.opencds.cqf.common.evaluation.EvaluationProviderFactory;
import org.opencds.cqf.common.helpers.DateHelper;
import org.opencds.cqf.common.helpers.LoggingHelper;
import org.opencds.cqf.common.helpers.UsingHelper;
import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.execution.LibraryLoader;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;

import ca.uhn.fhir.cql.common.provider.LibraryResolutionProvider;
import ca.uhn.fhir.cql.r4.helper.LibraryHelper;

public class MeasureEvaluationSeed {
    private Measure measure;
    private Context context;
    private Interval measurementPeriod;
    private LibraryLoader libraryLoader;
    private LibraryResolutionProvider<org.hl7.fhir.r4.model.Library> libraryResourceProvider;
    private EvaluationProviderFactory providerFactory;
    private DataProvider dataProvider;
    private LibraryHelper libraryHelper;

    public MeasureEvaluationSeed(EvaluationProviderFactory providerFactory, LibraryLoader libraryLoader,
            LibraryResolutionProvider<org.hl7.fhir.r4.model.Library> libraryResourceProvider, LibraryHelper libraryHelper) {
        this.providerFactory = providerFactory;
        this.libraryLoader = libraryLoader;
        this.libraryResourceProvider = libraryResourceProvider;
        this.libraryHelper = libraryHelper;
    }

    public Measure getMeasure() {
        return this.measure;
    }

    public Context getContext() {
        return this.context;
    }

    public Interval getMeasurementPeriod() {
        return this.measurementPeriod;
    }

    public DataProvider getDataProvider() {
        return this.dataProvider;
    }

    public void setup(Measure measure, String periodStart, String periodEnd, String productLine, String source,
            String user, String pass) {
        this.measure = measure;

        RequestDetails requestDetails = new SystemRequestDetails();

        this.libraryHelper.loadLibraries(measure, this.libraryLoader, this.libraryResourceProvider, requestDetails);

        // resolve primary library
        Library library = this.libraryHelper.resolvePrimaryLibrary(measure, libraryLoader, this.libraryResourceProvider, requestDetails);

        // resolve execution context
        context = new Context(library);
        context.setExpressionCaching(true);
        context.setDebugMap(LoggingHelper.getDebugMap());
        context.registerLibraryLoader(libraryLoader);

        List<Triple<String, String, String>> usingDefs = UsingHelper.getUsingUrlAndVersion(library.getUsings());

        if (usingDefs.size() > 1) {
            throw new IllegalArgumentException(
                    "Evaluation of Measure using multiple Models is not supported at this time.");
        }

        // If there are no Usings, there is probably not any place the Terminology
        // actually used so I think the assumption that at least one provider exists is
        // ok.
        TerminologyProvider terminologyProvider = null;
        if (usingDefs.size() > 0) {
            // Creates a terminology provider based on the first using statement. This
            // assumes the terminology
            // server matches the FHIR version of the CQL.
            terminologyProvider = this.providerFactory.createTerminologyProvider(usingDefs.get(0).getLeft(),
                    usingDefs.get(0).getMiddle(), source, user, pass);
            context.registerTerminologyProvider(terminologyProvider);
        }

        for (Triple<String, String, String> def : usingDefs) {
            this.dataProvider = this.providerFactory.createDataProvider(def.getLeft(), def.getMiddle(),
                    terminologyProvider);
            context.registerDataProvider(def.getRight(), dataProvider);
        }

        // resolve the measurement period
        measurementPeriod = new Interval(DateHelper.resolveRequestDate(periodStart, true), true,
                DateHelper.resolveRequestDate(periodEnd, false), true);

        context.setParameter(null, "Measurement Period",
                new Interval(DateTime.fromJavaDate((Date) measurementPeriod.getStart()), true,
                        DateTime.fromJavaDate((Date) measurementPeriod.getEnd()), true));

        if (productLine != null) {
            context.setParameter(null, "Product Line", productLine);
        }
    }
}
