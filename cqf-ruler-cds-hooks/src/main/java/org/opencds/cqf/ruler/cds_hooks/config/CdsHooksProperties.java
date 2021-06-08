package org.opencds.cqf.ruler.cds_hooks.config;

public class CdsHooksProperties {

     // ************************* CDS_HOOKS ****************
     public static Integer getCdsHooksFhirServerMaxCodesPerQuery() {
        return HapiProperties.getIntegerProperty(CDSHOOKS_FHIRSERVER_MAXCODESPERQUERY, 64);
    }

    public static Boolean getCdsHooksFhirServerExpandValueSets() {
        return HapiProperties.getBooleanProperty(CDSHOOKS_FHIRSERVER_EXPANDVALUESETS, true);
    }

    public static SearchStyleEnum getCdsHooksFhirServerSearchStyleEnum() {
        String searchStyleEnumString = HapiProperties.getProperty(CDSHOOKS_FHIRSERVER_SEARCHSTYLE);

        if (searchStyleEnumString != null && searchStyleEnumString.length() > 0) {
            return SearchStyleEnum.valueOf(searchStyleEnumString);
        }

        return SearchStyleEnum.GET;
    }
    public static Integer getCdsHooksPreFetchMaxUriLength() { return HapiProperties.getIntegerProperty(CDSHOOKS_PREFETCH_MAXURILENGTH, 8000);}
    
}
