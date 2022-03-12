package org.denhac.keycloakspi;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class DenhacUserStorageProviderFactory implements UserStorageProviderFactory<DenhacStorageProvider> {
    @Override
    public DenhacStorageProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        return new DenhacStorageProvider(
                keycloakSession,
                componentModel,
                DenhacStorageProviderConfiguration.createFromEnv()
        );
    }

    @Override
    public String getId() {
        return "denhac-storage-provider";
    }
}
