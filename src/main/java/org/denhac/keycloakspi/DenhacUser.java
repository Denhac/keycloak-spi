package org.denhac.keycloakspi;

import com.squareup.moshi.Json;
import org.keycloak.models.ClientModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DenhacUser implements UserModel {

    @Json(name = "ID")
    private String id;
    @Json(name = "display_name")
    private String username;
    @Json(name = "user_email")
    private String email;
    @Json(name = "first_name")
    private String firstName;
    @Json(name = "last_name")
    private String lastName;
    @Json(name = "membership_status")
    private MembershipStatus membershipStatus;

    private Long createdTimestamp;
    private boolean enabled;

    enum MembershipStatus {
        ACTIVE,
        INACTIVE
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
    public void setUsername(String s) {
        this.username = s;
    }

    @Override
    public Long getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(Long aLong) {
        this.createdTimestamp = aLong;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean b) {
        this.enabled = b;
    }

    @Override
    public void setSingleAttribute(String s, String s1) {

    }

    @Override
    public void setAttribute(String s, List<String> list) {

    }

    @Override
    public void removeAttribute(String s) {

    }

    @Override
    public String getFirstAttribute(String s) {
        return null;
    }

    @Override
    public List<String> getAttribute(String s) {
        return null;
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        return null;
    }

    @Override
    public Set<String> getRequiredActions() {
        return null;
    }

    @Override
    public void addRequiredAction(String s) {

    }

    @Override
    public void removeRequiredAction(String s) {

    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public void setFirstName(String s) {
        this.firstName = s;
    }

    @Override
    public String getLastName() {
        return this.lastName;
    }

    @Override
    public void setLastName(String s) {
        this.lastName = s;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public void setEmail(String s) {
        this.email = s;
    }

    @Override
    public boolean isEmailVerified() {
        return true;
    }

    @Override
    public void setEmailVerified(boolean b) {
    }

    @Override
    public Set<GroupModel> getGroups() {
        return null;
    }

    @Override
    public void joinGroup(GroupModel groupModel) {

    }

    @Override
    public void leaveGroup(GroupModel groupModel) {

    }

    @Override
    public boolean isMemberOf(GroupModel groupModel) {
        return false;
    }

    @Override
    public String getFederationLink() {
        return null;
    }

    @Override
    public void setFederationLink(String s) {

    }

    @Override
    public String getServiceAccountClientLink() {
        return null;
    }

    @Override
    public void setServiceAccountClientLink(String s) {

    }

    @Override
    public Set<RoleModel> getRealmRoleMappings() {
        return null;
    }

    @Override
    public Set<RoleModel> getClientRoleMappings(ClientModel clientModel) {
        return null;
    }

    @Override
    public boolean hasRole(RoleModel roleModel) {
        return false;
    }

    @Override
    public void grantRole(RoleModel roleModel) {

    }

    @Override
    public Set<RoleModel> getRoleMappings() {
        return null;
    }

    @Override
    public void deleteRoleMapping(RoleModel roleModel) {

    }
}
