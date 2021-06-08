package org.opencds.cqf.ruler.sdc.dstu3;

import org.hl7.fhir.dstu3.model.CapabilityStatement;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.UriType;
import org.opencds.cqf.ruler.core.api.capability.CapabilityStatementExtender;
import org.opencds.cqf.ruler.core.api.config.HapiProperties;
import org.springframework.stereotype.Component;

/**
 *      This class is NOT designed to be a real OAuth provider.
 *      It is designed to provide a capability statement and to pass thru the path to the real oauth verification server.
 *      It should only get instantiated if hapi.properties has oauth.enabled set to true.
 */
@Component
public class OAuthExtender implements CapabilityStatementExtender<CapabilityStatement> {

    @Override
    public CapabilityStatement extend(CapabilityStatement capabilityStatement) {
        capabilityStatement.getRestFirstRep().getSecurity().setCors(HapiProperties.getOauthSecurityCors());
        Extension securityExtension = capabilityStatement.getRestFirstRep().getSecurity().addExtension();
        securityExtension.setUrl(HapiProperties.getOauthSecurityUrl());
        // security.extension.extension
        Extension securityExtExt = securityExtension.addExtension();
        securityExtExt.setUrl(HapiProperties.getOauthSecurityExtAuthUrl());
        securityExtExt.setValue(new UriType(HapiProperties.getOauthSecurityExtAuthValueUri()));
        Extension securityTokenExt = securityExtension.addExtension();
        securityTokenExt.setUrl(HapiProperties.getOauthSecurityExtTokenUrl());
        securityTokenExt.setValue(new UriType(HapiProperties.getOauthSecurityExtTokenValueUri()));

        // security.extension.service
        Coding coding = new Coding();
        coding.setSystem(HapiProperties.getOauthServiceSystem());
        coding.setCode(HapiProperties.getOauthServiceCode());
        coding.setDisplay(HapiProperties.getOauthServiceDisplay());
        CodeableConcept codeConcept = new CodeableConcept();
        codeConcept.addCoding(coding);
        capabilityStatement.getRestFirstRep().getSecurity().getService().add(codeConcept);
        // capabilityStatement.getRestFirstRep().getSecurity().getService() //how do we handle "text" on the sample not part of getService

        return capabilityStatement;
    }

}
