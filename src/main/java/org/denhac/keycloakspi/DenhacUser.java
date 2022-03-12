package org.denhac.keycloakspi;

import org.keycloak.models.ClientModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DenhacUser implements UserModel {
    private final String id;
    private String username;

    public DenhacUser(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Long getCreatedTimestamp() {
        // todo
        return null;
    }

    @Override
    public void setCreatedTimestamp(Long timestamp) {
        // todo
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        // todo
    }

    @Override
    public void setSingleAttribute(String name, String value) {
        // todo
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        // todo
    }

    @Override
    public void removeAttribute(String name) {
        // todo
    }

    @Override
    public String getFirstAttribute(String name) {
        // todo
        return null;
    }

    @Override
    public List<String> getAttribute(String name) {
        // todo
        return null;
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        // todo
        return null;
    }

    @Override
    public Set<String> getRequiredActions() {
        // todo
        return null;
    }

    @Override
    public void addRequiredAction(String action) {
        // todo
    }

    @Override
    public void removeRequiredAction(String action) {
        // todo
    }

    @Override
    public String getFirstName() {
        // todo
        return null;
    }

    @Override
    public void setFirstName(String firstName) {
        // todo
    }

    @Override
    public String getLastName() {
        // todo
        return null;
    }

    @Override
    public void setLastName(String lastName) {
        // todo
    }

    @Override
    public String getEmail() {
        // todo
        return null;
    }

    @Override
    public void setEmail(String email) {
        // todo
    }

    @Override
    public boolean isEmailVerified() {
        // todo
        return false;
    }

    @Override
    public void setEmailVerified(boolean verified) {
        // todo
    }

    @Override
    public Set<GroupModel> getGroups() {
        // todo
        return null;
    }

    @Override
    public void joinGroup(GroupModel group) {
        // todo
    }

    @Override
    public void leaveGroup(GroupModel group) {
        // todo
    }

    @Override
    public boolean isMemberOf(GroupModel group) {
        // todo
        return false;
    }

    @Override
    public String getFederationLink() {
        // todo
        return null;
    }

    @Override
    public void setFederationLink(String link) {
        // todo
    }

    @Override
    public String getServiceAccountClientLink() {
        // todo
        return null;
    }

    @Override
    public void setServiceAccountClientLink(String clientInternalId) {
        // todo
    }

    @Override
    public Set<RoleModel> getRealmRoleMappings() {
        // todo
        return null;
    }

    @Override
    public Set<RoleModel> getClientRoleMappings(ClientModel app) {
        // todo
        return null;
    }

    @Override
    public boolean hasRole(RoleModel role) {
        // todo
        return false;
    }

    @Override
    public void grantRole(RoleModel role) {
        // todo
    }

    @Override
    public Set<RoleModel> getRoleMappings() {
        // todo
        return null;
    }

    @Override
    public void deleteRoleMapping(RoleModel role) {
        // todo
    }
}
