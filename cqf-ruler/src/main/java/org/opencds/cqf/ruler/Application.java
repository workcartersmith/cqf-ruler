package org.opencds.cqf.ruler;

import org.opencds.cqf.ruler.config.CqfRulerConfigR4;
import org.springframework.boot.SpringApplication;

public class Application {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(
      ca.uhn.fhir.jpa.starter.Application.class,
      CqfRulerConfigR4.class);

    app.run(args);

    //Server is now accessible at eg. http://localhost:8080/fhir/metadata
    //UI is now accessible at http://localhost:8080/
  }
}