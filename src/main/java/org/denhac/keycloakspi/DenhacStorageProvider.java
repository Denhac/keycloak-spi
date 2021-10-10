package org.denhac.keycloakspi;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import okhttp3.*;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;

import org.jboss.logging.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DenhacStorageProvider implements UserStorageProvider, UserLookupProvider, UserQueryProvider, CredentialInputValidator {
    static final Logger logger = Logger.getLogger(DenhacStorageProvider.class);

    private KeycloakSession keycloakSession;
    private ComponentModel componentModel;
    private String denhacValidateEndpoint;
    private String denhacListUsersEndpoint;
    private String denhacAccessKey;
    private String getDenhacAccessKeySecret;

    public DenhacStorageProvider(KeycloakSession keycloakSession, ComponentModel componentModel) {
        logger.info("DenhacStorageProvider constructor called");
        this.keycloakSession = keycloakSession;
        this.componentModel = componentModel;

        // TODO: There is a way for the provider to be passed config from the UI.
        this.denhacValidateEndpoint = System.getenv("DENHAC_VALIDATE_ENDPOINT");
        this.denhacListUsersEndpoint = System.getenv("DENHAC_LIST_USERS_ENDPOINT");
        this.denhacAccessKey = System.getenv("DENHAC_ACCESS_KEY");
        this.getDenhacAccessKeySecret = System.getenv("DENHAC_ACCESS_KEY_SECRET");
    }

    @Override
    public boolean supportsCredentialType(String type) {
        logger.info("supportsCredentialType called with: " + type);
        return PasswordCredentialModel.TYPE.equals(type);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String s) {
        return this.supportsCredentialType(s);
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

        // TODO: Pull this out
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("username", userModel.getUsername())
                .add("password", credentialInput.getChallengeResponse())
                .build();

        Request request = new Request.Builder()
                .addHeader(this.denhacAccessKey, getDenhacAccessKeySecret)
                .post(formBody)
                .url(this.denhacValidateEndpoint)
                .build();

        try (Response response = client.newCall(request).execute()) {
            switch (response.code()) {
                case 200: {
                    logger.info("200: Successfully authenticated " + userModel.getUsername());
                    return true;
                }
                case 401: {
                    logger.info("401: Authentication rejected for " + userModel.getUsername());
                    return false;
                }
                default: {
                    // TODO: log something broke
                    logger.warn(response.code() + ": Unexpected status for " + userModel.getUsername());
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void close() {
        logger.info("close called");
    }

    @Override
    public UserModel getUserById(String userId, RealmModel realmModel) {
        logger.info("getUserById called");
        String denhacUserId = StorageId.externalId(userId);

        // TODO: Fetch user by ID
        List<UserModel> users = this.getUsers(realmModel);

        List<UserModel> foundUsers = users.stream().filter(userModel -> userModel.getId().equals(userId)).collect(Collectors.toList());

        if (foundUsers.size() != 1) {
            logger.info("No users found for user ID: " + userId);

            if (foundUsers.size() > 1) {
                logger.warn("More than one user found for email: " + userId);
            }

            return null;
        }

        return foundUsers.get(0);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realmModel) {
        logger.info("getUserByUsername called");
        List<UserModel> users = this.getUsers(realmModel);


        List<UserModel> foundUsers = users.stream().filter(userModel -> userModel.getUsername().equals(username)).collect(Collectors.toList());

        if (foundUsers.size() != 1) {
            logger.info("No users found for username: " + username);

            if (foundUsers.size() > 1) {
                logger.warn("More than one user found for email: " + username);
            }

            return null;
        }

        return foundUsers.get(0);
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realmModel) {
        logger.info("getUserByEmail called");
        List<UserModel> users = this.getUsers(realmModel);

        List<UserModel> foundUsers = users.stream().filter(userModel -> userModel.getEmail().equals(email)).collect(Collectors.toList());

        if (foundUsers.size() != 1) {
            logger.info("No users found for email: " + email);

            if (foundUsers.size() > 1) {
                logger.warn("More than one user found for email: " + email);
            }

            return null;
        }

        return foundUsers.get(0);
    }

    @Override
    public List<UserModel> getUsers(RealmModel realmModel) {
        logger.info("Get all users has been called");

        List<DenhacUser> denhacUsers;

        // TODO: Pull this out
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(this.denhacListUsersEndpoint)
                .addHeader(this.denhacAccessKey, this.getDenhacAccessKeySecret)
                .get()
                .build();

        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, DenhacUser.class);
        JsonAdapter<List<DenhacUser>> denhacUserJsonAdapter = moshi.adapter(type);

        try (Response response = client.newCall(request).execute()) {
            // [
            //  {
            //    "ID": "44",
            //    "display_name": "Swag",
            //    "user_email": "swag@example.com",
            //    "first_name": "Swag",
            //    "last_name": "Swag",
            //    "membership_status": "ACTIVE"
            //  }
            // ]

            if (response.isSuccessful()) {
                logger.info("Response is successful for getUsers call");

                logger.info(response);
                logger.info(response.body());

                denhacUsers = denhacUserJsonAdapter.fromJson(response.body().source());

                return denhacUsers.stream().map(denhacUser -> new DenhacUserAdapter(this.keycloakSession, realmModel, this.componentModel, denhacUser)).collect(Collectors.toList());
            }
        } catch (IOException e) {
            logger.warn(e);
            // TODO
        } catch (JsonDataException e) {
            logger.warn(e);
           // TODO for dev
        }

        logger.info("Response did not succeed and the getUsers call is returning null");

        return Collections.emptyList();
    }

    @Override
    public List<UserModel> getUsers(RealmModel realmModel, int firstResult, int maxResults) {
        logger.info("getUsers called");
        return this.getUsers(realmModel);
    }

    @Override
    public List<UserModel> searchForUser(String s, RealmModel realmModel) {
        logger.info("Search for user string: " + s);
        return null;
    }

    @Override
    public List<UserModel> searchForUser(String s, RealmModel realmModel, int i, int i1) {
        logger.info("Search for user with i, i1: " + s + "[i=" + i + "] [i1=" + i1 + "]");
        return null;
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> map, RealmModel realmModel) {
        logger.info("Search for user<string, string>: " + map.toString());
        return null;
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> map, RealmModel realmModel, int i, int i1) {
        logger.info("Search for user<string, string, i, i1: " + map.toString() + " [i=" + i + "] [i1=" + i1 + "]");
        return null;
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
}
