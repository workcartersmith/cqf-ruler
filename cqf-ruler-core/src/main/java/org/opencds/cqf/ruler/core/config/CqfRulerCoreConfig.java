package org.opencds.cqf.ruler.core.config;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.opencds.cqf.cql.evaluator.spring.EvaluatorConfiguration;
import org.opencds.cqf.ruler.core.api.provider.OperationProvider;
import org.opencds.cqf.ruler.core.dal.RulerDal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.provider.ResourceProviderFactory;

@Configuration
@Import(EvaluatorConfiguration.class)
public class CqfRulerCoreConfig {

  @Autowired
  private ApplicationContext appCtx;

  @Autowired
  private ResourceProviderFactory resourceProviderFactory;

  @Autowired(required = false)
  List<OperationProvider> operationProviders;

  @PostConstruct
  public void init() {
    if (operationProviders != null && !operationProviders.isEmpty()) {
      for (OperationProvider provider : operationProviders) {
        resourceProviderFactory.addSupplier(() -> provider);
      }
    }

    Map<String, RestfulServer> servers = this.appCtx.getBeansOfType(RestfulServer.class);
    if (servers == null || servers.isEmpty()) {
      this.registerServer();
    }
  }

  @SuppressWarnings("rawtypes")
  private void registerServer() {
    Map<String, ServletRegistrationBean> servletBeans = this.appCtx.getBeansOfType(ServletRegistrationBean.class);
    if (servletBeans == null || servletBeans.isEmpty()) {
      throw new RuntimeException("Unable to find RestfulServer to register");
    }

    for (Map.Entry<String, ServletRegistrationBean> servletBean : servletBeans.entrySet()) {
      Object servlet = servletBean.getValue().getServlet();
      if (servlet instanceof RestfulServer) {
        GenericBeanDefinition gbd = new GenericBeanDefinition();
        gbd.setBeanClass(RestfulServer.class);
        gbd.setInstanceSupplier(() -> servlet);
        ((BeanDefinitionRegistry) this.appCtx.getAutowireCapableBeanFactory()).registerBeanDefinition("restfulServer",
            gbd);

        return;
      }
    }

    throw new RuntimeException("Unable to find RestfulServer to register");
  }

  @Bean
  public RulerDal rulerDal(DaoRegistry daoRegistry) {
    return new RulerDal(daoRegistry);
  }
}
