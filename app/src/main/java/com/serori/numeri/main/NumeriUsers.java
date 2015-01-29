package com.serori.numeri.main;

import com.serori.numeri.user.NumeriUser;

import java.util.ArrayList;
import java.util.List;

/**
 * アプリケーションが生きている間持つユーザーを表す
 */
public class NumeriUsers {
    private List<NumeriUser> numeriUsers = new ArrayList<>();

    private NumeriUsers() {
    }

    static NumeriUsers getInstance() {
        return NumeriUsersHolder.instance;
    }

    void addNumeriUser(NumeriUser numeriUser) {
        numeriUsers.add(numeriUser);
    }

    public List<NumeriUser> getNumeriUsers() {
        List<NumeriUser> users = new ArrayList<>();
        users.addAll(numeriUsers);
        return users;
    }

    void clear() {
        for (NumeriUser numeriUser : numeriUsers) {
            numeriUser.getStreamSwitcher().closeStream();
        }
        numeriUsers.clear();
    }

    private static class NumeriUsersHolder {
        private static final NumeriUsers instance = new NumeriUsers();
    }

}
