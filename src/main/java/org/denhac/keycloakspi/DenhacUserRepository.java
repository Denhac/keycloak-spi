package org.denhac.keycloakspi;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import okhttp3.*;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DenhacUserRepository {
    private static final Logger logger = org.jboss.logging.Logger.getLogger(DenhacStorageProvider.class);

    private final DenhacStorageProviderConfiguration conifg;
    private final OkHttpClient client;
    private final KeycloakSession session;
    private final ComponentModel componentModel;

    public DenhacUserRepository(
            DenhacStorageProviderConfiguration conifg,
            KeycloakSession session,
            ComponentModel componentModel
    ) {
        this.conifg = conifg;
        this.client = new OkHttpClient();
        this.session = session;
        this.componentModel = componentModel;
    }

    public boolean validateUser(String username, String password) {
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        Request request = newRequest()
                .url(this.conifg.validateEndpoint())
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            switch (response.code()) {
                case 200: {
                    logger.infof("200: Successfully authenticated %s", username);
                    return true;
                }
                case 401: {
                    logger.infof("401: Authentication rejected for %s", username);
                    return false;
                }
                default: {
                    // TODO: log something broke
                    logger.warnf("unexpected status: %s for: %s", response.code(), username);
                    return false;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("something went wrong checking user login", e);
        }
    }

    public List<UserModel> getUsers(RealmModel realmModel) {
        logger.info("Get all users has been called");

        List<DenhacUser> denhacUsers;

        Request request = newRequest()
                .url(this.conifg.listUsersEndpoint())
                .get()
                .build();

        logger.infof("get all user req: %s", request.toString());
        logger.info(request.url());
        logger.info(request.headers());

        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, DenhacUser.class);
        JsonAdapter<List<DenhacUser>> denhacUserJsonAdapter = moshi.adapter(type);

        try (Response response = this.client.newCall(request).execute()) {
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

                return denhacUsers.stream().map(denhacUser -> new DenhacUserAdapter(this.session, realmModel, this.componentModel, denhacUser)).collect(Collectors.toList());
            } else {
                throw new RuntimeException(String.format("unexpected status: %d", response.code()));
            }
        } catch (IOException | JsonDataException e) {
            throw new RuntimeException("unable to fetch users", e);
        }
    }

    public UserModel getUserByID(String userID, RealmModel realmModel) {
        logger.info("getUserByID called");

        DenhacUser user;

        Request request = newRequest()
                // TODO array out of bounds?
                .url(this.conifg.getUserEndpoint(userID.split(":")[2]))
                .get()
                .build();

        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<DenhacUser> denhacUserJsonAdapter = moshi.adapter(DenhacUser.class);

        try (Response response = this.client.newCall(request).execute()) {
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
                logger.info("Response is successful for getMember call");

                logger.info(response);
                logger.info(response.body());

                user = denhacUserJsonAdapter.fromJson(response.body().source());

                return new DenhacUserAdapter(this.session, realmModel, this.componentModel, user);
            } else {
                throw new RuntimeException(String.format("unexpected status: %d", response.code()));
            }
        } catch (IOException | JsonDataException e) {
            throw new RuntimeException("unable to fetch user: " + userID, e);
        }
    }

    private Request.Builder newRequest() {
        return new Request.Builder().addHeader(this.conifg.accessKey, this.conifg.accessSecret);
    }
}
