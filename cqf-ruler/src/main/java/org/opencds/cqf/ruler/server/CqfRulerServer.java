package org.opencds.cqf.ruler.server;

import javax.servlet.ServletException;

import org.opencds.cqf.ruler.core.api.capability.ExtensibleCapabilityStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import ca.uhn.fhir.jpa.starter.BaseJpaRestfulServer;

public class CqfRulerServer extends BaseJpaRestfulServer {

    @Autowired
    private ApplicationContext appCtx;

    @Override
    public void initialize() throws ServletException {
        super.initialize();
        this.setServerConformanceProvider(appCtx.getBean(ExtensibleCapabilityStatementProvider.class));
    }  
}
