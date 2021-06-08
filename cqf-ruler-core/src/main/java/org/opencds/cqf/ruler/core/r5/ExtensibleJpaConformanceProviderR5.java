package org.opencds.cqf.ruler.core.r5;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.instance.model.api.IBaseConformance;
import org.hl7.fhir.r5.model.CapabilityStatement;
import org.opencds.cqf.ruler.core.api.capability.CapabilityStatementExtender;
import org.opencds.cqf.ruler.core.api.capability.ExtensibleCapabilityStatementProvider;

import ca.uhn.fhir.context.support.IValidationSupport;
import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.api.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.JpaCapabilityStatementProvider;
import ca.uhn.fhir.rest.annotation.Metadata;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.util.ISearchParamRegistry;

public class ExtensibleJpaConformanceProviderR5 extends JpaCapabilityStatementProvider implements ExtensibleCapabilityStatementProvider {

    private List<CapabilityStatementExtender<CapabilityStatement>> extenders;

    @Inject
    public ExtensibleJpaConformanceProviderR5(RestfulServer theRestfulServer, IFhirSystemDao<?, ?> theSystemDao,
            DaoConfig theDaoConfig, ISearchParamRegistry theSearchParamRegistry,
            IValidationSupport theValidationSupport, List<CapabilityStatementExtender<CapabilityStatement>> extenders) {
        super(theRestfulServer, theSystemDao, theDaoConfig, theSearchParamRegistry, theValidationSupport);
        this.extenders = extenders;

        this.setImplementationDescription("CQF Ruler FHIR R5 Server");
        this.setRestfulServer(theRestfulServer);
    }

    @Metadata
    @Override
    public IBaseConformance getServerConformance(HttpServletRequest theRequest, RequestDetails theRequestDetails) {
        IBaseConformance retVal = super.getServerConformance(theRequest, theRequestDetails);

        if (extenders != null) {
            for (CapabilityStatementExtender<CapabilityStatement> extender : extenders) {
                extender.extend((CapabilityStatement)retVal);
            }
        }

        return retVal;
    }
}