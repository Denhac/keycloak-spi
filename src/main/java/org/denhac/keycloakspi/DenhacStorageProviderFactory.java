package org.denhac.keycloakspi;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class DenhacStorageProviderFactory implements UserStorageProviderFactory<DenhacStorageProvider> {
    final static String PROVIDER_NAME = "denhac-storage-provider";

    @Override
    public DenhacStorageProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        return new DenhacStorageProvider(keycloakSession, componentModel);
    }

    @Override
    public String getId() {
        return DenhacStorageProviderFactory.PROVIDER_NAME;
    }
}
