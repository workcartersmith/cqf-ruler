package org.opencds.cqf.r4.providers;

import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Measure;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.opencds.cqf.common.helpers.ClientHelperDos;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.dao.DaoRegistry;
import ca.uhn.fhir.jpa.dao.IFhirResourceDao;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class NotifySubmitRequestedProvider {

    private FhirContext fhirContext;
    private DaoRegistry daoRegistry;

    public NotifySubmitRequestedProvider(FhirContext fhirContext, DaoRegistry daoRegistry) {
        this.fhirContext = fhirContext;
        this.daoRegistry = daoRegistry;
    }

    @Operation(name = "$notify-submit-requested", idempotent = false, type = Measure.class)
    public Parameters notifySubmitData(@IdParam IdType theId, @RequiredParam(name = "periodStart") String periodStart,
                                       @RequiredParam(name = "periodEnd") String periodEnd, @OptionalParam(name = "patient") String patientRef,
                                       @OptionalParam(name = "practitioner") String practitionerRef,
                                       @OptionalParam(name = "lastReceivedOn") String lastReceivedOn) {

        // Get the collect data endpoint
        IFhirResourceDao<Endpoint> dao = this.daoRegistry.getResourceDao(Endpoint.class);

        Endpoint collectDataEndpoint = dao.read(new IdType("collect-data"));

        IGenericClient collectClient = ClientHelperDos.getClient(fhirContext, collectDataEndpoint);

        Parameters collectDataParameters = new Parameters();

        // TODO: Populate parameters
        collectDataParameters.addParameter().setName("periodStart").setValue(new StringType(periodStart));
        collectDataParameters.addParameter().setName("periodEnd").setValue(new StringType(periodEnd));
        if (patientRef != null) {
            collectDataParameters.addParameter().setName("patient").setValue(new StringType(patientRef));
        }

        if (practitionerRef != null) {
            collectDataParameters.addParameter().setName("practitioner").setValue(new StringType(practitionerRef));
        }

        if (lastReceivedOn != null) {
            collectDataParameters.addParameter().setName("lastReceivedOn").setValue(new StringType(lastReceivedOn));
        }

        Endpoint dataEndpoint = dao.read(new IdType("data-endpoint"));
        Endpoint terminologyEndpoint = dao.read(new IdType("terminology-endpoint"));
        Endpoint measureEndpoint = dao.read(new IdType("measure-endpoint"));

        collectDataParameters.addParameter().setName("dataEndpoint").setResource(dataEndpoint);
        collectDataParameters.addParameter().setName("terminologyEndpoint").setResource(terminologyEndpoint);
        collectDataParameters.addParameter().setName("measureEndpoint").setResource(measureEndpoint);

        // call collect-data
        Parameters parameters = collectClient.operation()
                .onInstance(theId)
                .named("$collect-data")
                .withParameters(collectDataParameters).execute();

        // get submit-data endpoint
        Endpoint submitDataEndpoint  = dao.read(new IdType("submit-data"));

        // call submit-data with collect-data response
        IGenericClient submitClient = ClientHelperDos.getClient(fhirContext, submitDataEndpoint);

        Parameters result = submitClient.operation()
                .onInstance(theId)
                .named("$submit-data")
                .withParameters(parameters).execute();

        return result;
    }
}
