package org.opencds.cqf.ruler.plugin.conformance;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hapi.fhir.conformance")
public class ConformanceProperties {

    private Boolean enabled = true;

    public Boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

}
