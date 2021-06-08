package org.opencds.cqf.ruler.core.dstu2;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.opencds.cqf.ruler.core.api.capability.CapabilityStatementExtender;
import org.opencds.cqf.ruler.core.api.capability.ExtensibleCapabilityStatementProvider;

import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.api.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.JpaConformanceProviderDstu2;
import ca.uhn.fhir.model.dstu2.composite.MetaDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Conformance;
import ca.uhn.fhir.rest.annotation.Metadata;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.RestfulServer;

public class ExtensibleJpaConformanceProviderDstu2 extends JpaConformanceProviderDstu2 implements ExtensibleCapabilityStatementProvider {

    private List<CapabilityStatementExtender<Conformance>> extenders;

    @Inject
    public ExtensibleJpaConformanceProviderDstu2(RestfulServer theRestfulServer, IFhirSystemDao<Bundle, MetaDt> theSystemDao, DaoConfig theDaoConfig, List<CapabilityStatementExtender<Conformance>> extenders) {
        super(theRestfulServer, theSystemDao, theDaoConfig);
        this.extenders = extenders;

        this.setImplementationDescription("CQF Ruler FHIR DSTU2 Server");
        this.setRestfulServer(theRestfulServer);
    }

    @Metadata
    @Override
    public Conformance getServerConformance(HttpServletRequest theRequest, RequestDetails theRequestDetails) {
        Conformance retVal;
        retVal = super.getServerConformance(theRequest, theRequestDetails);

        if (extenders != null) {
            for (CapabilityStatementExtender<Conformance> extender : extenders) {
                extender.extend(retVal);
            }
        }

        return retVal;
    }
}