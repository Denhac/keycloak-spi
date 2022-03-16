package org.denhac.keycloakspi;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.ImportedUserValidation;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;

import org.jboss.logging.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DenhacStorageProvider implements
        UserStorageProvider,
        UserLookupProvider,
        UserQueryProvider,
        CredentialInputValidator,
        ImportedUserValidation {

    static final Logger logger = Logger.getLogger(DenhacStorageProvider.class);

    protected final KeycloakSession keycloakSession;
    protected final ComponentModel componentModel;
    protected final DenhacStorageProviderConfiguration config;
    protected final DenhacUserRepository denhacUserRepo;

    public DenhacStorageProvider(KeycloakSession keycloakSession, ComponentModel componentModel, DenhacStorageProviderConfiguration config) {
        logger.info("DenhacStorageProvider constructor called");

        this.keycloakSession = keycloakSession;
        this.componentModel = componentModel;
        this.config = config;
        this.denhacUserRepo = new DenhacUserRepository(config, keycloakSession, componentModel);
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        logger.info("supportsCredentialType called with: " + credentialType);
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String credentialType) {
        logger.info("isConfiguredFor called");
        return this.supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realmModel, UserModel userModel, CredentialInput credentialInput) {
        logger.infof("isValid called %s", userModel.getId());

        if (!this.isConfiguredFor(realmModel, userModel, credentialInput.getType())) {
            return false;
        }

        if (credentialInput.getChallengeResponse().length() <= 0) {
            logger.info("Challenge response was not greater than 0.");
            return false;
        }

        return this.denhacUserRepo.validateUser(userModel.getUsername(), credentialInput.getChallengeResponse());
    }

    @Override
    public void close() {
        logger.info("close called");
    }

    @Override
    public UserModel getUserById(String id, RealmModel realmModel) {
        logger.infof("getUserById called %s", id);
        StorageId userId = new StorageId(id);
        return this.denhacUserRepo.getUserById(userId.getExternalId(), realmModel);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realmModel) {
        logger.infof("getUserByUsername called %s", username);
        // TODO don't query all for this
        return (UserModel) this.denhacUserRepo.getUsers(realmModel).stream().filter(userModel -> userModel.getUsername().equals(username)).toArray()[0];
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realmModel) {
        logger.infof("getUserByEmail called %s", email);
        // TODO don't query all for this
        return (UserModel) this.denhacUserRepo.getUsers(realmModel).stream().filter(userModel -> userModel.getEmail().equals(email)).toArray()[0];
    }

    @Override
    public List<UserModel> getUsers(RealmModel realmModel) {
        logger.info("getUsers called");
        return this.denhacUserRepo.getUsers(realmModel);
    }

    @Override
    public List<UserModel> getUsers(RealmModel realmModel, int firstResult, int maxResults) {
        logger.infof("getUsers called with %d %d", firstResult, maxResults);
        // TODO better performance with offset
        return this.getUsers(realmModel);
    }

    @Override
    public List<UserModel> searchForUser(String s, RealmModel realmModel) {
        logger.infof("searchForUser called with string: %s", s);
        // TODO I think returning empty list here makes keycloak look up in local storage
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> searchForUser(String s, RealmModel realmModel, int i, int i1) {
        logger.infof("searchForUser called with string: %s %d %d", s, i, i1);
        // TODO I think returning empty list here makes keycloak look up in local storage
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> map, RealmModel realmModel) {
        logger.infof("searchForUser called with map: %s", map.toString());
        // TODO I think returning empty list here makes keycloak look up in local storage
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> map, RealmModel realmModel, int i, int i1) {
        logger.infof("searchForUser called with map: %s, %d %d", map.toString(), i, i1);
        // TODO I think returning empty list here makes keycloak look up in local storage
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String s, String s1, RealmModel realmModel) {
        logger.infof("searchForUserByUserAttribute called with %s %s", s, s1);
        // TODO I think returning empty list here makes keycloak look up in local storage
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realmModel, GroupModel groupModel) {
        logger.info("getGroupMembers called");
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realmModel, GroupModel groupModel, int i, int i1) {
        logger.infof("getGroupMembers called with %d %d", i, i1);
        return Collections.emptyList();
    }

    @Override
    public UserModel validate(RealmModel realm, UserModel user) {
        logger.infof("validate called with %s %s", user.getId(), user.getUsername());
        // Return null here to have keycloak remove user from local storage and query our provider
        return user;
    }
}
