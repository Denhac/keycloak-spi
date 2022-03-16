package org.denhac.keycloakspi;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
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

    private final KeycloakSession keycloakSession;
    private final ComponentModel componentModel;
    private final DenhacStorageProviderConfiguration config;
    private final DenhacUserRepository denhacUserRepo;

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
        logger.info("isValid called");

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
    public UserModel getUserById(String userId, RealmModel realmModel) {
        logger.info("getUserById called");
        return this.denhacUserRepo.getUserByID(userId, realmModel);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realmModel) {
        logger.info("getUserByUsername called");
        return null;
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realmModel) {
        logger.info("getUserByEmail called");
        return null;
    }

    @Override
    public List<UserModel> getUsers(RealmModel realmModel) {
        logger.info("getUsers called");
        return this.denhacUserRepo.getUsers(realmModel);
    }

    @Override
    public List<UserModel> getUsers(RealmModel realmModel, int firstResult, int maxResults) {
        logger.info("getUsers offset called");
        return this.getUsers(realmModel);
    }

    @Override
    public List<UserModel> searchForUser(String s, RealmModel realmModel) {
        logger.info("Search for user string: " + s);
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> searchForUser(String s, RealmModel realmModel, int i, int i1) {
        logger.info("Search for user with i, i1: " + s + "[i=" + i + "] [i1=" + i1 + "]");
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> map, RealmModel realmModel) {
        logger.info("Search for user<string, string>: " + map.toString());
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> map, RealmModel realmModel, int i, int i1) {
        logger.info("Search for user<string, string, i, i1: " + map.toString() + " [i=" + i + "] [i1=" + i1 + "]");
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realmModel, GroupModel groupModel) {
        logger.info("getGroupMembers called");
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realmModel, GroupModel groupModel, int i, int i1) {
        logger.info("getGroupMembers with i/i1 called");
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String s, String s1, RealmModel realmModel) {
        logger.info("Search for user by user attribute: " + s + " " + s1);
        return Collections.emptyList();
    }

    @Override
    public UserModel validate(RealmModel realm, UserModel user) {
        logger.info("validate called");
        return this.getUserById(user.getId(), realm);
    }
}
