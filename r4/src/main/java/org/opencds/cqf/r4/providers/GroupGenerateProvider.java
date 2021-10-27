package org.opencds.cqf.r4.providers;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Group;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Group.GroupType;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.annotation.Operation;

@Component
public class GroupGenerateProvider {
    private final IFhirResourceDao<Patient> patientDao;

    @Inject
    public GroupGenerateProvider(IFhirResourceDao<Patient> patientDao) {
        this.patientDao = patientDao;
    }

    @Operation(name = "$generate", idempotent = true, type = Group.class)
    public Group generate() {
        Group group = new Group();

        group.setId(UUID.randomUUID().toString());
        group.setType(GroupType.PERSON);
        group.setActive(true);
        group.setActual(true);

        List<IBaseResource> patients = this.patientDao.search(SearchParameterMap.newSynchronous()).getAllResources();
        for (IBaseResource p : patients) {
            // TODO: Parameters for some type of criteria
            if (!p.getIdElement().getIdPart().contains("-")) {
                group.addMember().setEntity(new Reference(p.getIdElement()));
            }
        }

        return group;
    }
}
