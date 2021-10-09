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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DenhacStorageProvider implements UserStorageProvider, UserLookupProvider, UserQueryProvider, CredentialInputValidator {
    private KeycloakSession keycloakSession;
    private ComponentModel componentModel;
    private String denhacValidateEndpoint;
    private String denhacListUsersEndpoint;
    private String denhacAccessKey;
    private String getDenhacAccessKeySecret;

    public DenhacStorageProvider(KeycloakSession keycloakSession, ComponentModel componentModel) {
        this.keycloakSession = keycloakSession;
        this.componentModel = componentModel;
        this.denhacValidateEndpoint = System.getenv("DENHAC_VALIDATE_ENDPOINT");
        this.denhacListUsersEndpoint = System.getenv("DENHAC_LIST_USERS_ENDPOINT");
        this.denhacAccessKey = System.getenv("DENHAC_ACCESS_KEY");
        this.getDenhacAccessKeySecret = System.getenv("DENHAC_ACCESS_KEY_SECRET");
    }

    @Override
    public boolean supportsCredentialType(String s) {
        return PasswordCredentialModel.TYPE.equals(s);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String s) {
        return this.supportsCredentialType(s);
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
        Request request = new Request.Builder()
                .addHeader(this.denhacAccessKey, getDenhacAccessKeySecret)
                .post(formBody)
                .url(this.denhacValidateEndpoint)
                .build();
        try (Response response = client.newCall(request).execute()) {
            switch (response.code()) {
                case 200: {
                    return true;
                }
                case 401: {
                    return false;
                }
                default: {
                    // TODO: log something broke
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

    }

    @Override
    public UserModel getUserById(String s, RealmModel realmModel) {
        String denhacUserId = StorageId.externalId(s);

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
        ArrayList<UserModel> users;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(this.denhacListUsersEndpoint)
                .addHeader(this.denhacAccessKey, this.getDenhacAccessKeySecret)
                .get()
                .build();

        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(ArrayList.class, DenhacUser.class);
        JsonAdapter<ArrayList<UserModel>> denhacUserJsonAdapter = moshi.adapter(type);

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
                users = denhacUserJsonAdapter.fromJson(response.body().source());
                return users;
            }
        } catch (IOException e) {
            // TODO
        } catch (JsonDataException e) {
           // TODO for dev
        }
        return null;
    }

    @Override
    public List<UserModel> getUsers(RealmModel realmModel, int firstResult, int maxResults) {

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
