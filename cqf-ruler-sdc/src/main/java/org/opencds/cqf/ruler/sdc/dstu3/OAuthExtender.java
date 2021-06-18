package org.opencds.cqf.ruler.sdc.dstu3;

import org.hl7.fhir.dstu3.model.CapabilityStatement;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.UriType;
import org.opencds.cqf.ruler.core.api.capability.CapabilityStatementExtender;
import org.opencds.cqf.ruler.sdc.config.SdcProperties;
/**
 * This class adds OAuth redirect information to the CapabilityStatement
 */
public class OAuthExtender implements CapabilityStatementExtender<CapabilityStatement> {

    private SdcProperties sdcProperties;

    public OAuthExtender(SdcProperties sdcProperties) {
        this.sdcProperties = sdcProperties;
    }

    @Override
    public CapabilityStatement extend(CapabilityStatement capabilityStatement) {
        capabilityStatement.getRestFirstRep().getSecurity().setCors(sdcProperties.getOauth().getSecurity().getCors());
        Extension securityExtension = capabilityStatement.getRestFirstRep().getSecurity().addExtension();
        securityExtension.setUrl(sdcProperties.getOauth().getSecurity().getUrl());
        // security.extension.extension
        Extension securityExtExt = securityExtension.addExtension();
        securityExtExt.setUrl(sdcProperties.getOauth().getSecurity().getExt_auth_url());
        securityExtExt.setValue(new UriType(sdcProperties.getOauth().getSecurity().getExt_auth_value_uri()));
        Extension securityTokenExt = securityExtension.addExtension();
        securityTokenExt.setUrl(sdcProperties.getOauth().getSecurity().getExt_token_url());
        securityTokenExt.setValue(new UriType(sdcProperties.getOauth().getSecurity().getExt_token_value_uri()));

        // security.extension.service
        Coding coding = new Coding();
        coding.setSystem(sdcProperties.getOauth().getService().getSystem());
        coding.setCode(sdcProperties.getOauth().getService().getCode());
        coding.setDisplay(sdcProperties.getOauth().getService().getDisplay());
        CodeableConcept codeConcept = new CodeableConcept();
        codeConcept.addCoding(coding);
        capabilityStatement.getRestFirstRep().getSecurity().getService().add(codeConcept);
        // capabilityStatement.getRestFirstRep().getSecurity().getService() //how do we handle "text" on the sample not part of getService

        return capabilityStatement;
    }
}
