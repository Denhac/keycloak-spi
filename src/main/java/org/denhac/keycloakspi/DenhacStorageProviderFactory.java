package org.denhac.keycloakspi;

import org.jboss.logging.Logger;

import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.UserStorageProviderFactory;
import org.keycloak.storage.UserStorageProviderModel;
import org.keycloak.storage.user.ImportSynchronization;
import org.keycloak.storage.user.SynchronizationResult;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class DenhacStorageProviderFactory implements
        UserStorageProviderFactory<DenhacStorageProvider>,
        ImportSynchronization {

    private static final Logger logger = Logger.getLogger(DenhacStorageProviderFactory.class);

    @Override
    public DenhacStorageProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        logger.info("create called");
        try {
            var config = componentModel.getConfig();
            return new DenhacStorageProvider(
                    keycloakSession,
                    componentModel,
                    new DenhacStorageProviderConfiguration(
                            new URL(config.getFirst(DenhacStorageProviderConfiguration.DENHAC_BASE_URL)),
                            config.getFirst(DenhacStorageProviderConfiguration.DENHAC_ACCESS_KEY),
                            config.getFirst(DenhacStorageProviderConfiguration.DENHAC_ACCESS_KEY_SECRET)
                    )
            );
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Unable to parse base url", e);
        }
    }

    @Override
    public String getId() {
        return "denhac-storage-provider";
    }

    protected final static List<ProviderConfigProperty> configProperties;

    static {
        configProperties = ProviderConfigurationBuilder.create()
                .property()
                    .name(DenhacStorageProviderConfiguration.DENHAC_BASE_URL)
                    .label(DenhacStorageProviderConfiguration.DENHAC_BASE_URL)
                    .type(ProviderConfigProperty.STRING_TYPE)
                    .defaultValue(System.getenv("DENHAC_BASE_URL"))
                    .add()
                .property()
                    .name(DenhacStorageProviderConfiguration.DENHAC_ACCESS_KEY)
                    .label(DenhacStorageProviderConfiguration.DENHAC_ACCESS_KEY)
                    .type(ProviderConfigProperty.STRING_TYPE)
                    .defaultValue(System.getenv("DENHAC_ACCESS_KEY"))
                    .add()
                .property()
                    .name(DenhacStorageProviderConfiguration.DENHAC_ACCESS_KEY_SECRET)
                    .label(DenhacStorageProviderConfiguration.DENHAC_ACCESS_KEY_SECRET)
                    .type(ProviderConfigProperty.STRING_TYPE)
                    .defaultValue(System.getenv("DENHAC_ACCESS_KEY_SECRET"))
                    .secret(true)
                    .add()
                .build();
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        logger.info("getConfigProperties called");
        return configProperties;
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) throws ComponentValidationException {
        logger.info("validateConfiguration called");
        var baseURL = config.getConfig().getFirst(DenhacStorageProviderConfiguration.DENHAC_BASE_URL);
        var accessKey = config.getConfig().getFirst(DenhacStorageProviderConfiguration.DENHAC_ACCESS_KEY);
        var accessSecret = config.getConfig().getFirst(DenhacStorageProviderConfiguration.DENHAC_ACCESS_KEY_SECRET);

        if (baseURL == null || baseURL.isEmpty()) {
            throw new ComponentValidationException("Base URL required");
        }
        try {
            new URL(baseURL);
        } catch (MalformedURLException e) {
            throw new ComponentValidationException("Unable to parse URL", e);
        }

        if (accessKey == null || accessKey.isEmpty()) {
            throw new ComponentValidationException("denhac access key required");
        }

        if (accessSecret == null || accessSecret.isEmpty()) {
            throw new ComponentValidationException("denhac access key secret required");
        }
    }

    @Override
    public SynchronizationResult sync(KeycloakSessionFactory sessionFactory, String realmId, UserStorageProviderModel model) {
        logger.info("sync called");

        logger.infof("realmId: %s, model: %s", realmId, model.toString());

        var syncResult = new SynchronizationResult();

        KeycloakModelUtils.runJobInTransaction(sessionFactory, session -> {
            logger.info("sync tx started");
            RealmModel realm = session.realms().getRealm(realmId);
            logger.infof("realm id %s", realm.getId());
            logger.infof("realm string %s", realm.toString());
            session.getContext().setRealm(realm);

            DenhacStorageProvider dsp = (DenhacStorageProvider) session.getProvider(UserStorageProvider.class, model);

            List<UserModel> users = dsp.denhacUserRepo.getUsers(realm);
            logger.infof("user count: %d", users.size());

            for (UserModel user : users) {
                logger.infof("syncing user: %s", user.getUsername());

                var u = session.userLocalStorage().getUserByUsername(realm, user.getUsername());
                if (u == null) {
                    logger.infof("user %s not found. creating", user.getUsername());
                    u = session.userLocalStorage().addUser(realm, user.getUsername());
                    syncResult.increaseAdded();
                } else {
                    logger.infof("user %s found. updating", user.getUsername());
                    syncResult.increaseUpdated();
                }
                u.setEmail(user.getEmail());
                u.setFirstName(user.getFirstName());
                u.setLastName(user.getLastName());
                u.setEnabled(user.isEnabled());
                u.setEmailVerified(true);
                // This is what maps user login to our provider
                u.setFederationLink(model.getId());
            }
        });

        logger.infof("added %d updated %d", syncResult.getAdded(), syncResult.getUpdated());

        return syncResult;
    }

    @Override
    public SynchronizationResult syncSince(Date lastSync, KeycloakSessionFactory sessionFactory, String realmId, UserStorageProviderModel model) {
        logger.infof("syncSince %s", lastSync.toString());
        return sync(sessionFactory, realmId, model);
    }
}
