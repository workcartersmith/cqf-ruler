package org.opencds.cqf.ruler.sdc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "hapi.fhir.sdc")
@Configuration
@EnableConfigurationProperties
public class SdcProperties {

    private OAuth oauth;
    private QuestionnaireResponseExtract questionnaireResponseExtract;
    private ObservationTransform observationTransform;

    public OAuth getOauth() {
        return oauth;
    }

    public void setOauth(OAuth oauth) {
        this.oauth = oauth;
    }

    public QuestionnaireResponseExtract getQuestionnaireResponseExtract() {
        return questionnaireResponseExtract;
    }

    public void setQuestionnaireResponseExtract(QuestionnaireResponseExtract questionnaireResponseExtract) {
        this.questionnaireResponseExtract = questionnaireResponseExtract;
    }

    public ObservationTransform getObservationTransform() {
        return observationTransform;
    }

    public void setObservationTransform(ObservationTransform observationTransform) {
        this.observationTransform = observationTransform;
    }

    public static class OAuth {
        private Boolean enabled = false;
        private Security security;
        private Service service;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Security getSecurity() {
            return security;
        }

        public void setSecurity(Security security) {
            this.security = security;
        }

        public Service getService() {
            return service;
        }

        public void setService(Service service) {
            this.service = service;
        }

        public static class Security {
            private Boolean cors = true;
            private String url = "";
            private String ext_auth_url = "";
            private String ext_auth_value_uri = "";
            private String ext_token_url = "";
            private String ext_token_value_uri = "";

            public Boolean getCors() {
                return cors;
            }
            public void setCors(Boolean cors) {
                this.cors = cors;
            }
            public String getUrl() {
                return url;
            }
            public void setUrl(String url) {
                this.url = url;
            }
            public String getExt_auth_url() {
                return ext_auth_url;
            }
            public void setExt_auth_url(String ext_auth_url) {
                this.ext_auth_url = ext_auth_url;
            }
            public String getExt_auth_value_uri() {
                return ext_auth_value_uri;
            }
            public void setExt_auth_value_uri(String ext_auth_value_uri) {
                this.ext_auth_value_uri = ext_auth_value_uri;
            }
            public String getExt_token_url() {
                return ext_token_url;
            }
            public void setExt_token_url(String ext_token_url) {
                this.ext_token_url = ext_token_url;
            }
            public String getExt_token_value_uri() {
                return ext_token_value_uri;
            }
            public void setExt_token_value_uri(String ext_token_value_uri) {
                this.ext_token_value_uri = ext_token_value_uri;
            }

        }

        public static class Service {
            private String system = "";
            private String code = "";
            private String display = "";
            private String text = "";

            public String getSystem() {
                return system;
            }
            public void setSystem(String system) {
                this.system = system;
            }
            public String getCode() {
                return code;
            }
            public void setCode(String code) {
                this.code = code;
            }
            public String getDisplay() {
                return display;
            }
            public void setDisplay(String display) {
                this.display = display;
            }
            public String getText() {
                return text;
            }
            public void setText(String text) {
                this.text = text;
            }
        }
    }

    public static class QuestionnaireResponseExtract {
        private Boolean enabled = false;
        private String endpoint = "";
        private String username = "";
        private String password = "";

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class ObservationTransform {
        private Boolean enabled = false;
        private String username = "";
        private String password = "";
        private Boolean replace_code = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Boolean getReplace_code() {
            return replace_code;
        }

        public void setReplace_code(Boolean replace_code) {
            this.replace_code = replace_code;
        }
    }
}
