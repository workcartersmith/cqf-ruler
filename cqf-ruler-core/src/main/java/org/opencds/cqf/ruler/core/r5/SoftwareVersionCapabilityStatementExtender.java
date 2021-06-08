package org.opencds.cqf.ruler.core.r5;

import org.hl7.fhir.r5.model.CapabilityStatement;
import org.hl7.fhir.r5.model.Extension;
import org.hl7.fhir.r5.model.StringType;
import org.opencds.cqf.ruler.core.api.capability.CapabilityStatementExtender;

public class SoftwareVersionCapabilityStatementExtender implements CapabilityStatementExtender<CapabilityStatement> {

    @Override
    public CapabilityStatement extend(CapabilityStatement capabilityStatement) {

        Extension softwareModuleExtension = new Extension().setUrl("http://hl7.org/fhir/StructureDefinition/capabilitystatement-softwareModule");
        Extension softwareModuleNameExtension = new Extension().setUrl("name").setValue(new StringType("CQF Ruler FHIR R5 Server"));
        Extension softwareModuleVersionExtension = new Extension().setUrl("version").setValue(new StringType(SoftwareVersionCapabilityStatementExtender.class.getPackage().getImplementationVersion()));
        softwareModuleExtension.addExtension(softwareModuleNameExtension);
        softwareModuleExtension.addExtension(softwareModuleVersionExtension);
        capabilityStatement.getSoftware().addExtension(softwareModuleExtension);

        return capabilityStatement;
    }
    
}
