package org.denhac.keycloakspi;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

public class DenhacUserAdapter extends AbstractUserAdapterFederatedStorage {

    private final DenhacUser user;

    public DenhacUserAdapter(KeycloakSession session, RealmModel realm, ComponentModel storageProviderModel, DenhacUser user) {

        super(session, realm, storageProviderModel);
        this.storageId = new StorageId(storageProviderModel.getId(), user.getId());
        this.user = user;
        this.setFirstName(user.getFirstName());
        this.setLastName(user.getLastName());
        this.setEmail(user.getEmail());
        this.setEnabled(user.getMembershipStatus() == DenhacUser.MembershipStatus.ACTIVE);
        // TODO: timestamp?
        // TODO: roles
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public void setUsername(String s) {
        user.setUsername(s);
    }
}
