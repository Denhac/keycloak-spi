package org.denhac.keycloakspi;

import okhttp3.*;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DenhacStorageProvider implements UserStorageProvider, UserLookupProvider, UserQueryProvider, CredentialInputValidator {
    static final Logger logger = Logger.getLogger(DenhacStorageProvider.class);

    private KeycloakSession keycloakSession;
    private ComponentModel componentModel;
    private final DenhacStorageProviderConfiguration configuration;

    public DenhacStorageProvider(
            KeycloakSession keycloakSession,
            ComponentModel componentModel,
            DenhacStorageProviderConfiguration configuration
    ) {
        this.keycloakSession = keycloakSession;
        this.componentModel = componentModel;
        this.configuration = configuration;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String credentialType) {
        return this.supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realmModel, UserModel userModel, CredentialInput credentialInput) {
        if (!this.isConfiguredFor(realmModel, userModel, credentialInput.getType())) {
            return false;
        }

        if (credentialInput.getChallengeResponse().length() <= 0) {
            return false;
        }

        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("username", userModel.getUsername())
                .add("password", credentialInput.getChallengeResponse())
                .build();

        try {
            Request request = new Request.Builder()
                    .addHeader(this.configuration.accessKey, this.configuration.accessSecret)
                    .post(formBody)
                    .url(this.configuration.validateEndpoint().toString())
                    .build();

            Response response = client.newCall(request).execute();

            switch (response.code()) {
                case 200: {
                    return true;
                }
                case 401: {
                    return false;
                }
                default: {
                    logger.warn(
                            String.format("Response code %d was detected, expected 200 or 401", response.code())
                    );

                    return false;
                }
            }
        } catch (MalformedURLException e) {
            logger.error("Invalid API call to validate the user");
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("Invalid response detected");
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public UserModel getUserById(String userId, RealmModel realmModel) {
        String denhacUserId = StorageId.externalId(userId);

        // TODO: Fetch user by ID

        return null;
    }

    @Override
    public UserModel getUserByUsername(String s, RealmModel realmModel) {
        return null;
    }

    @Override
    public UserModel getUserByEmail(String s, RealmModel realmModel) {
        return null;
    }

    @Override
    public List<UserModel> getUsers(RealmModel realmModel) {
        ArrayList<UserModel> users = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        try {
            Request request = new Request.Builder()
                    .url(this.configuration.listUsersEndpoint().toString())
                    .addHeader(this.configuration.accessKey, this.configuration.accessSecret)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            return users;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<UserModel> getUsers(RealmModel realmModel, int i, int i1) {
        return null;
    }

    @Override
    public List<UserModel> searchForUser(String s, RealmModel realmModel) {
        return null;
    }

    @Override
    public List<UserModel> searchForUser(String s, RealmModel realmModel, int i, int i1) {
        return null;
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> map, RealmModel realmModel) {
        return null;
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> map, RealmModel realmModel, int i, int i1) {
        return null;
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realmModel, GroupModel groupModel) {
        return null;
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realmModel, GroupModel groupModel, int i, int i1) {
        return null;
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String s, String s1, RealmModel realmModel) {
        return null;
    }
}