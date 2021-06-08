package org.opencds.cqf.ruler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import ca.uhn.fhir.jpa.starter.BaseJpaRestfulServer;

@SpringBootApplication(exclude = { ElasticsearchRestClientAutoConfiguration.class }, scanBasePackageClasses = {
    Application.class, BaseJpaRestfulServer.class })
public class Application extends SpringBootServletInitializer {

  public static void main(String[] args) {
    System.setProperty("spring.batch.job.enabled", "false");
    SpringApplication.run(Application.class, args);

    // Server is now accessible at eg. http://localhost:8080/fhir/metadata
    // UI is now accessible at http://localhost:8080/
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    return builder.sources(Application.class);
  }
}