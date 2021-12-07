package org.opencds.cqf.ruler.plugin.testutility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.validator.routines.UrlValidator;
import org.junit.jupiter.api.Test;

public class ClientUtilitiesTest implements ClientUtilities {
    @Test
    public void testClientUrlWithTemplate() {

        String template = "http://localhost:%d/fhir";
        Integer port = 8084;
        String url = getClientUrl(template, port);

        String[] schemes = {"http","https"};
        UrlValidator urlValidator = new UrlValidator(schemes, UrlValidator.ALLOW_LOCAL_URLS);
        
        assertTrue(urlValidator.isValid(url));

        assertEquals("http://localhost:8084/fhir", url);
    }

    @Test
    public void testClientUrlWithoutTemplate() {

        Integer port = 8084;
        String url = getClientUrl(port);

        String[] schemes = {"http","https"};
        UrlValidator urlValidator = new UrlValidator(schemes, UrlValidator.ALLOW_LOCAL_URLS);
        
        assertTrue(urlValidator.isValid(url));
    }
}
