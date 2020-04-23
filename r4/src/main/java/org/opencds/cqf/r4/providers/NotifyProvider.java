package org.opencds.cqf.r4.providers;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import com.alphora.cql.service.factory.DataProviderFactory;

import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Resource;
import org.opencds.cqf.common.providers.LibraryResolutionProvider;
import org.opencds.cqf.cql.terminology.TerminologyProvider;
import org.opencds.cqf.r4.managers.ReportingManager;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.dao.DaoRegistry;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.RequiredParam;

public class NotifyProvider {

    private ReportingManager reportingManager;

    public NotifyProvider(DaoRegistry registry, LibraryResolutionProvider<org.hl7.fhir.r4.model.Library> libraryResourceProvider, DataProviderFactory dataProviderFactory, FhirContext fhirContext, TerminologyProvider localSystemTerminologyProvider) {
        this.reportingManager = new ReportingManager(fhirContext, registry, libraryResourceProvider, dataProviderFactory, localSystemTerminologyProvider);
    }

    /*
        TODO: add Subscribe functionality
    */
    @Operation(name = "$notify")
    public Resource applyPlanDefinition(
        //should be patient and potentially an Encounter
            @RequiredParam(name="patientId") String patientId,
            @RequiredParam(name="encounterId") String encounterId) throws FHIRException, IOException, JAXBException {
                // How do I know which PlanDefinition to apply
                String eRSDId = "plandefinition-RuleFilters-1.0.0";
        return reportingManager.manage(eRSDId, patientId);
    }
}