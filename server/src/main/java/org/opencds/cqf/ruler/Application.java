package org.opencds.cqf.ruler;

import org.opencds.cqf.ruler.config.TesterUIConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import ca.uhn.fhir.jpa.starter.FhirTesterConfig;
import ca.uhn.fhir.jpa.starter.annotations.OnEitherVersion;
import ca.uhn.fhir.jpa.starter.mdm.MdmConfig;
import ca.uhn.fhir.jpa.subscription.channel.config.SubscriptionChannelConfig;
import ca.uhn.fhir.jpa.subscription.match.config.SubscriptionProcessorConfig;
import ca.uhn.fhir.jpa.subscription.match.config.WebsocketDispatcherConfig;
import ca.uhn.fhir.jpa.subscription.submit.config.SubscriptionSubmitterConfig;

@ServletComponentScan(basePackageClasses = Application.class)
@ComponentScan(basePackageClasses = { ca.uhn.fhir.jpa.starter.Application.class,
    Application.class }, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
        FhirTesterConfig.class, ca.uhn.fhir.jpa.starter.Application.class }))
@SpringBootApplication(exclude = { ElasticsearchRestClientAutoConfiguration.class })
@Import({ SubscriptionSubmitterConfig.class, SubscriptionProcessorConfig.class, SubscriptionChannelConfig.class,
    WebsocketDispatcherConfig.class, MdmConfig.class, TesterUIConfig.class })
public class Application extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
    // Server is now accessible at eg. http://localhost:8080/fhir/metadata
    // UI (if enabled) is now accessible at http://localhost:8080/
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    return builder.sources(Application.class);
  }

  @Autowired
  AutowireCapableBeanFactory beanFactory;

  @Bean
  @Conditional(OnEitherVersion.class)
  public ServletRegistrationBean<Server> hapiServletRegistration() {
    ServletRegistrationBean<Server> servletRegistrationBean = new ServletRegistrationBean<Server>();
    Server server = new Server();
    beanFactory.autowireBean(server);
    servletRegistrationBean.setServlet(server);
    servletRegistrationBean.addUrlMappings("/fhir/*");
    servletRegistrationBean.setLoadOnStartup(1);

    return servletRegistrationBean;
  }
}