package org.opencds.cqf.ruler.config;

import org.opencds.cqf.ruler.server.CqfRulerServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import ca.uhn.fhir.jpa.mdm.MdmConfig;
import ca.uhn.fhir.jpa.starter.BaseJpaRestfulServer;
import ca.uhn.fhir.jpa.starter.FhirTesterConfig;
import ca.uhn.fhir.jpa.starter.annotations.OnEitherVersion;
import ca.uhn.fhir.jpa.subscription.channel.config.SubscriptionChannelConfig;
import ca.uhn.fhir.jpa.subscription.match.config.SubscriptionProcessorConfig;
import ca.uhn.fhir.jpa.subscription.match.config.WebsocketDispatcherConfig;
import ca.uhn.fhir.jpa.subscription.submit.config.SubscriptionSubmitterConfig;

@Configuration
@Import({ SubscriptionSubmitterConfig.class, SubscriptionProcessorConfig.class, SubscriptionChannelConfig.class,
    WebsocketDispatcherConfig.class, MdmConfig.class })
@ComponentScan(basePackageClasses = BaseJpaRestfulServer.class)
public class CqfRulerConfig {

  @Autowired
  AutowireCapableBeanFactory beanFactory;

  @Bean
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Conditional(OnEitherVersion.class)
  public ServletRegistrationBean hapiServletRegistration() {
    ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
    CqfRulerServer jpaRestfulServer = new CqfRulerServer();
    beanFactory.autowireBean(jpaRestfulServer);
    servletRegistrationBean.setServlet(jpaRestfulServer);
    servletRegistrationBean.addUrlMappings("/fhir/*");
    servletRegistrationBean.setLoadOnStartup(1);

    return servletRegistrationBean;
  }

  @Bean
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public ServletRegistrationBean overlayRegistrationBean() {

    AnnotationConfigWebApplicationContext annotationConfigWebApplicationContext = new AnnotationConfigWebApplicationContext();
    annotationConfigWebApplicationContext.register(FhirTesterConfig.class);

    DispatcherServlet dispatcherServlet = new DispatcherServlet(annotationConfigWebApplicationContext);
    dispatcherServlet.setContextClass(AnnotationConfigWebApplicationContext.class);
    dispatcherServlet.setContextConfigLocation(FhirTesterConfig.class.getName());

    ServletRegistrationBean registrationBean = new ServletRegistrationBean();
    registrationBean.setServlet(dispatcherServlet);
    registrationBean.addUrlMappings("/*");
    registrationBean.setLoadOnStartup(1);
    return registrationBean;
  }
}
