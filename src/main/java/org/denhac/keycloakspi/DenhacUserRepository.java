package org.denhac.keycloakspi;

import java.util.ArrayList;
import java.util.HashMap;

public class DenhacUserRepository {
    private HashMap<String, DenhacUser> userHashMap;

    public DenhacUserRepository() {
        this.userHashMap = new HashMap<>();
    }

    public DenhacUserRepository(ArrayList<DenhacUser> users) {
        this.userHashMap = new HashMap<>();
        users.forEach(denhacUser -> this.userHashMap.put(denhacUser.getId(), denhacUser));
    }

    public DenhacUser getUserById(String id) {
        return this.userHashMap.get(id);
    }
}
