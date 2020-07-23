package org.opencds.cqf.r4.providers;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import junit.framework.JUnit4TestCaseFacade;
import org.apache.commons.io.IOUtils;
import org.h2.util.json.JSONObject;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.junit.jupiter.api.Test;
import java.io.FileInputStream;
import ca.uhn.fhir.context.FhirContext;


import java.io.*;
import java.net.URLDecoder;

import static org.junit.jupiter.api.Assertions.*;

class QuestionnaireProviderTest{

    @Test
    public void testQuestionnaireProviderExtract() {
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("questionnaireResponses/QR.json");
            String qrStr = IOUtils.toString(in, Charsets.UTF_8);
            QuestionnaireResponse qrOut = new QuestionnaireResponse();
//            qrOut.resource = new JsonParser().parse(qrStr).getAsJsonObject().get("resource");
//                static FhirContext fhirContextClinical = FhirContext.forDstu3();
                FhirContext fhirContextR4 = FhirContext.forR4();
qrOut = (QuestionnaireResponse) fhirContextR4.newJsonParser().parseResource(qrStr);

            QuestionnaireProvider questionnaireProvider = new QuestionnaireProvider(null);
            Bundle obsBundle = questionnaireProvider.extractObservationFromQuestionnaireResponse(qrOut);
System.out.println(obsBundle);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}