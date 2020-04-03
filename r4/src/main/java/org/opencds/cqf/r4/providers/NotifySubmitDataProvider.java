package org.opencds.cqf.r4.providers;

import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Measure;
import org.hl7.fhir.r4.model.Parameters;
import org.opencds.cqf.common.helpers.ClientHelperDos;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.dao.DaoRegistry;
import ca.uhn.fhir.jpa.dao.IFhirResourceDao;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class NotifySubmitDataProvider {

    private FhirContext fhirContext;
    private DaoRegistry daoRegistry;

    public NotifySubmitDataProvider(FhirContext fhirContext, DaoRegistry daoRegistry) {
        this.fhirContext = fhirContext;
        this.daoRegistry = daoRegistry;
    }

    @Operation(name = "$notify-submit-data", idempotent = false, type = Measure.class)
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
                collectDataParameters.addParameter("periodStart", periodStart);
                collectDataParameters.addParameter("periodEnd", periodEnd);
                if (patientRef != null) {
                    collectDataParameters.addParameter("patient", patientRef);
                }

                if (practitionerRef != null) {
                    collectDataParameters.addParameter("practitioner", practitionerRef);
                }

                if (lastReceivedOn != null) {
                    collectDataParameters.addParameter("lastReceivedOn", lastReceivedOn);
                }


                // call collect-data
                Parameters parameters = collectClient.operation()
                    .onInstance(theId)
                    .named("$collect-data")
                    .withParameters(collectDataParameters).execute();

                // get submit-data endpoint
                Endpoint submitDataEndpoint  = dao.read(new IdType("submit-data"));

                // call submit-data with collect-data response
                IGenericClient submitClient = ClientHelperDos.getClient(fhirContext, submitDataEndpoint);

                parameters = submitClient.operation()
                .onType(Measure.class)
                .named("$submit-data")
                .withParameters(parameters).execute();

                return parameters;
            }
}
