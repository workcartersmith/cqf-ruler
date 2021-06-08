package org.opencds.cqf.ruler.sdc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "hapi.fhir.sdc")
@Configuration
@EnableConfigurationProperties
public class SdcProperties {

    private Boolean enabled = true;

    private Boolean oauth_enabled = false;
    private Boolean oauth_security_cors = true;
    private String oauth_security_url = "";
    private String oauth_security_ext_auth_url = "";
    private String oauth_security_ext_auth_value_uri = "";
    private String oauth_security_ext_token_url = "";
    private String oauth_security_ext_token_value_uri = "";
    private String oauth_service_system = "";
    private String oauth_service_code = "";
    private String oauth_service_display = "";
    private String oauth_service_text = "";

    private Boolean questionnaire_response_extract_enabled = false;
    private String questionnaire_response_extract_endpoint = "";
    private String questionnaire_response_extract_username = "";
    private String questionnaire_response_extract_password = "";

    private Boolean observation_transform_enabled = false;
    private String observation_transform_username = "";
    private String observation_transform_password = "";
    private Boolean observation_transform_replace_code = false;


    public static Boolean getOAuthEnabled() {
        return HapiProperties.getBooleanProperty(OAUTH_ENABLED, false);
    }

    public static Boolean getOauthSecurityCors() {
        return HapiProperties.getBooleanProperty(OAUTH_SECURITY_CORS, true);
    }

    public static String getOauthSecurityUrl() {
        return HapiProperties.getProperty(OAUTH_SECURITY_URL, "");
    }

    public static String getOauthSecurityExtAuthUrl() {
        return HapiProperties.getProperty(OAUTH_SECURITY_EXT_AUTH_URL, "");
    }

    public static String getOauthSecurityExtAuthValueUri() {
        return HapiProperties.getProperty(OAUTH_SECURITY_EXT_AUTH_VALUE_URI, "");
    }

    public static String getOauthSecurityExtTokenUrl() {
        return HapiProperties.getProperty(OAUTH_SECURITY_EXT_TOKEN_URL, "");
    }

    public static String getOauthSecurityExtTokenValueUri() {
        return HapiProperties.getProperty(OAUTH_SECURITY_EXT_TOKEN_VALUE_URI, "");
    }

    public static String getOauthServiceSystem() {
        return HapiProperties.getProperty(OAUTH_SERVICE_SYSTEM, "");
    }

    public static String getOauthServiceCode() {
        return HapiProperties.getProperty(OAUTH_SERVICE_CODE, "");
    }

    public static String getOauthServiceDisplay() {
        return HapiProperties.getProperty(OAUTH_SERVICE_DISPLAY, "");
    }

    public static String getOauthServiceText() {
        return HapiProperties.getProperty(OAUTH_SERVICE_TEXT, "");
    }

    public static Boolean getQuestionnaireResponseExtractEnabled() {
        return HapiProperties.getBooleanProperty(QUESTIONNAIRE_RESPONSE_ENABLED, false);
    }

    public static String getQuestionnaireResponseExtractEndpoint() {
        return HapiProperties.getProperty(QUESTIONNAIRE_RESPONSE_ENDPOINT);
    }

    public static String getQuestionnaireResponseExtractUserName() {
        return HapiProperties.getProperty(QUESTIONNAIRE_RESPONSE_USERNAME);
    };

    public static String getQuestionnaireResponseExtractPassword() {
        return HapiProperties.getProperty(QUESTIONNAIRE_RESPONSE_PASSWORD);
    };

    public static Boolean getObservationTransformEnabled() {
        return HapiProperties.getBooleanProperty(OBSERVATION_TRANSFORM_ENABLED, false);
    }

    public static String getObservationTransformUsername() {
        return HapiProperties.getProperty(OBSERVATION_TRANSFORM_USERNAME);
    }

    public static String getObservationTransformPassword() {
        return HapiProperties.getProperty(OBSERVATION_TRANSFORM_PASSWORD);
    }

    public static Boolean getObservationTransformReplaceCode() {
        return HapiProperties.getBooleanProperty(OBSERVATION_TRANSFORM_REPLACE_CODE, false);
    }
    
}
