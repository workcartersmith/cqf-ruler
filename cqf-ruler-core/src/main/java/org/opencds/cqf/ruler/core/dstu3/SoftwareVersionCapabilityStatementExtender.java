package org.opencds.cqf.ruler.core.dstu3;

import org.hl7.fhir.dstu3.model.CapabilityStatement;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.StringType;
import org.opencds.cqf.ruler.core.api.capability.CapabilityStatementExtender;

public class SoftwareVersionCapabilityStatementExtender implements CapabilityStatementExtender<CapabilityStatement> {

    @Override
    public CapabilityStatement extend(CapabilityStatement capabilityStatement) {

        Extension softwareModuleExtension = new Extension().setUrl("http://hl7.org/fhir/StructureDefinition/capabilitystatement-softwareModule");
        Extension softwareModuleNameExtension = new Extension().setUrl("name").setValue(new StringType("CQF Ruler FHIR DSTU3 Server"));
        Extension softwareModuleVersionExtension = new Extension().setUrl("version").setValue(new StringType(SoftwareVersionCapabilityStatementExtender.class.getPackage().getImplementationVersion()));
        softwareModuleExtension.addExtension(softwareModuleNameExtension);
        softwareModuleExtension.addExtension(softwareModuleVersionExtension);
        capabilityStatement.getSoftware().addExtension(softwareModuleExtension);

        return capabilityStatement;
    }
    
}
