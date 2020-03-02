package org.opencds.cqf.common.helpers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ClientHelper {

    @PostConstruct
    public void init() {
        ClientHelper.environment = this.environmentTemp;
    }

    @Autowired
    public Environment environmentTemp;

    public static Environment environment;

    /* TODO - depending on future needs:
            1.  add OAuth
            2.  change if to switch to accommodate additional FHIR versions
     */
    private static IGenericClient getRestClient(String whichFHIRVersion, String url){
        FhirContext fhirContext = whichFHIRVersion.equalsIgnoreCase("dstu3")? FhirContextHelper.getFhirContextDstu3() : FhirContextHelper.getFhirContextR4();
        fhirContext.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        return fhirContext.newRestfulGenericClient(url);
    }

    // Overload in case you need to specify a specific version of the context
    public static IGenericClient getClient(String whichFHIRVersion, String url, String user, String password)
    {
        IGenericClient client = getRestClient(whichFHIRVersion, url);
        registerAuth(client, user, password);

        return client;
    }

    private static void registerAuth(IGenericClient client, String userId, String password) {
        if (userId != null) {
            BasicAuthInterceptor authInterceptor = new BasicAuthInterceptor(userId, password);
            client.registerInterceptor(authInterceptor);
        }
    }
}