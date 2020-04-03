package org.opencds.cqf.common.providers;

import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.opencds.cqf.common.exceptions.NotImplementedException;
import org.opencds.cqf.utilities.BundleUtils;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.BundleUtil;

public class RemoteLibraryResolutionProvider<LibraryType> implements LibraryResolutionProvider<LibraryType> {

    private IGenericClient client;
    public RemoteLibraryResolutionProvider(IGenericClient client) {
        this.client = client;
    }
    @Override
    public LibraryType resolveLibraryById(String libraryId) {
        return (LibraryType)this.client.read().resource("Library").withId(libraryId).execute();
    }

    @Override
    public LibraryType resolveLibraryByName(String libraryName, String libraryVersion) {
       IBaseBundle bundle = this.client.search().byUrl(String.format("Library?name=%s&version=%s", libraryName, libraryVersion)).execute();
       return (LibraryType)BundleUtil.toListOfResources(this.client.getFhirContext(), bundle).get(0);
    }
    @Override
    public void update(LibraryType library) {
        throw new NotImplementedException();
    }

}