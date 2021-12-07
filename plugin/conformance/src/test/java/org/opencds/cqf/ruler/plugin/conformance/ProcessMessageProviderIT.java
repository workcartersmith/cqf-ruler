package org.opencds.cqf.ruler.plugin.conformance;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opencds.cqf.ruler.Application;
import org.opencds.cqf.ruler.plugin.utility.ResolutionUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {Application.class, ConformanceConfig.class},
        properties = {"hapi.fhir.fhir_version=r4", "hapi.fhir.conformance.enabled=true" })
public class ProcessMessageProviderIT implements ResolutionUtilities {
    private Logger log = LoggerFactory.getLogger(ProcessMessageProviderIT.class);

    private IGenericClient ourClient;
    private FhirContext ourCtx;

    @Autowired
    private DaoRegistry ourRegistry;

    @LocalServerPort
    private int port;

    @BeforeEach
    void beforeEach() {

        ourCtx = FhirContext.forCached(FhirVersionEnum.R4);
        ourCtx.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        ourCtx.getRestfulClientFactory().setSocketTimeout(1200 * 1000);
        String ourServerBase = "http://localhost:" + port + "/fhir/";
        ourClient = ourCtx.newRestfulGenericClient(ourServerBase);
    }

//    @Test
//    public void testProcessMessage() throws IOException {
//
//        resolveByLocation(ourRegistry, "transaction-bundle-test.json", ourCtx);
//
//        Bundle test = (Bundle)ourCtx.newJsonParser().parseResource(stringFromResource("transaction-bundle-test.json"));
//
//        Parameters params = new Parameters();
//        params.addParameter().setName("theMessageToProcess").setResource(test);
//
//        Bundle actual = ourClient.operation().onType(Bundle.class).named("$process-message-bundle")
//                .withParameters(params)
//                .returnResourceType(Bundle.class)
//                .execute();
//
//        assertNotNull(actual);
//    }

}
