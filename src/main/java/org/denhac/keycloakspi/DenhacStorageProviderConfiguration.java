package org.denhac.keycloakspi;

import java.net.MalformedURLException;
import java.net.URL;

public class DenhacStorageProviderConfiguration {

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
    }

    public URL validateEndpoint() {
        try {
            return new URL(this.baseURL, "/validate-user");
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Unable to construct url", e);
        }
    }

    public URL getUserEndpoint() {
        try {
            return new URL(this.baseURL, "/get-member");
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Unable to construct url", e);
        }
    }

    public URL listUsersEndpoint() {
        try {
            return new URL(this.baseURL, "/list-members");
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Unable to construct url", e);
        }
    }
}
