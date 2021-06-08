package org.opencds.cqf.ruler.core.dstu2;

import org.hl7.fhir.dstu2.model.Conformance;
import org.hl7.fhir.dstu2.model.Extension;
import org.hl7.fhir.dstu2.model.StringType;
import org.opencds.cqf.ruler.core.api.capability.CapabilityStatementExtender;

public class SoftwareVersionConformanceExtender implements CapabilityStatementExtender<Conformance> {

    @Override
    public Conformance extend(Conformance conformance) {

        Extension softwareModuleExtension = new Extension().setUrl("http://hl7.org/fhir/StructureDefinition/capabilitystatement-softwareModule");
        Extension softwareModuleNameExtension = new Extension().setUrl("name").setValue(new StringType("CQF Ruler FHIR dstu2 Server"));
        Extension softwareModuleVersionExtension = new Extension().setUrl("version").setValue(new StringType(SoftwareVersionConformanceExtender.class.getPackage().getImplementationVersion()));
        softwareModuleExtension.addExtension(softwareModuleNameExtension);
        softwareModuleExtension.addExtension(softwareModuleVersionExtension);
        conformance.getSoftware().addExtension(softwareModuleExtension);

        return conformance;
    }
    
}
