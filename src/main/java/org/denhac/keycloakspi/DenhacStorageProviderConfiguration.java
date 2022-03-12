package org.denhac.keycloakspi;

import java.net.MalformedURLException;
import java.net.URL;

public class DenhacStorageProviderConfiguration {
    public static final String DEFAULT_API_BASE_URL = "https://denhac.org/wp-json/denhac/v1";
    public final String accessKey;
    public final String accessSecret;
    private final String basePath;

    public DenhacStorageProviderConfiguration(String basePath, String accessKey, String accessSecret) {
        this.basePath = basePath;
        this.accessKey = accessKey;
        this.accessSecret = accessSecret;
    }

    public static DenhacStorageProviderConfiguration createFromEnv() {
        String baseUrlFromEnv = System.getenv("DENHAC_BASE_URL");

        return new DenhacStorageProviderConfiguration(
                System.getenv("DENHAC_ACCESS_KEY"),
                System.getenv("DENHAC_ACCESS_KEY_SECRET"),
                baseUrlFromEnv.isBlank()
                        ? DEFAULT_API_BASE_URL
                        : baseUrlFromEnv
        );
    }

    public URL validateEndpoint() throws MalformedURLException {
        return new URL(this.basePath + "/validate");
    }

    public URL listUsersEndpoint() throws MalformedURLException {
        return new URL(this.basePath + "/list-users");
    }
}
