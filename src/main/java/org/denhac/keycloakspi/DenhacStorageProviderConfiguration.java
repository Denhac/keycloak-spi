package org.denhac.keycloakspi;

import org.jboss.logging.Logger;

import java.net.MalformedURLException;
import java.net.URL;

public class DenhacStorageProviderConfiguration {
    private static final Logger logger = Logger.getLogger(DenhacStorageProviderConfiguration.class);

    public static final String DENHAC_BASE_URL = "denhac base url";
    public static final String DENHAC_ACCESS_KEY = "denhac access key";
    public static final String DENHAC_ACCESS_KEY_SECRET = "denhac access key secret";

    public String accessKey;
    public String accessSecret;
    public URL baseURL;

    public DenhacStorageProviderConfiguration(URL baseURL, String accessKey, String accessSecret) {
        this.baseURL = baseURL;
        this.accessKey = accessKey;
        this.accessSecret = accessSecret;

        logger.infof("baseURL: %s, accessKey: %s, accessSecret: %s", baseURL, accessKey, accessSecret);
    }

    public URL validateEndpoint() {
        try {
            return new URL(this.baseURL, "validate-user");
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Unable to construct url", e);
        }
    }

    public URL getUserEndpoint(String userID) {
        logger.info("getUserEndpoint: " + userID);
        try {
            var u = new URL(this.baseURL, "get-member/" + userID);
            logger.infof("URL is: %s", u.toString());
            return u;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Unable to construct url", e);
        }
    }

    public URL listUsersEndpoint() {
        try {
            logger.info("listUsersEndpoint");
            logger.info(this.baseURL);

            var u =  new URL(this.baseURL, "list-members");

            logger.info(u);

            return u;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Unable to construct url", e);
        }
    }
}
