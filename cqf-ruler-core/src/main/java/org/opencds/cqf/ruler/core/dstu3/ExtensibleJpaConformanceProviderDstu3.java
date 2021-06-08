package org.opencds.cqf.ruler.core.dstu3;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CapabilityStatement;
import org.hl7.fhir.dstu3.model.Meta;
import org.opencds.cqf.ruler.core.api.capability.CapabilityStatementExtender;
import org.opencds.cqf.ruler.core.api.capability.ExtensibleCapabilityStatementProvider;

import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.api.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.dstu3.JpaConformanceProviderDstu3;
import ca.uhn.fhir.rest.annotation.Metadata;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.util.ISearchParamRegistry;

public class ExtensibleJpaConformanceProviderDstu3 extends JpaConformanceProviderDstu3 implements ExtensibleCapabilityStatementProvider {

    private List<CapabilityStatementExtender<CapabilityStatement>> extenders;

    @Inject
    public ExtensibleJpaConformanceProviderDstu3(RestfulServer theRestfulServer, IFhirSystemDao<Bundle, Meta> theSystemDao, DaoConfig theDaoConfig, ISearchParamRegistry theSearchParamRegistry, List<CapabilityStatementExtender<CapabilityStatement>> extenders) {
        super(theRestfulServer, theSystemDao, theDaoConfig, theSearchParamRegistry);
        this.extenders = extenders;

        this.setImplementationDescription("CQF Ruler FHIR DSTU3 Server");
        this.setRestfulServer(theRestfulServer);
    }

    @Metadata
    @Override
    public CapabilityStatement getServerConformance(HttpServletRequest theRequest, RequestDetails theRequestDetails) {
        CapabilityStatement retVal;
        retVal = super.getServerConformance(theRequest, theRequestDetails);

        if (extenders != null) {
            for (CapabilityStatementExtender<CapabilityStatement> extender : extenders) {
                extender.extend(retVal);
            }
        }

        return retVal;
    }
}