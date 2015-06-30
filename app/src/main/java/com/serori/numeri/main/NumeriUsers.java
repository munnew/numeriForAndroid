package com.serori.numeri.main;

import com.serori.numeri.user.NumeriUser;

import java.util.ArrayList;
import java.util.List;

/**
 * アプリケーションが生きている間持つユーザーたちを表すクラス
 */
public class NumeriUsers {
    private final List<NumeriUser> numeriUsers = new ArrayList<>();

    private NumeriUsers() {
    }

    static NumeriUsers getInstance() {
        return NumeriUsersHolder.instance;
    }

    public void addNumeriUser(NumeriUser numeriUser) {
        numeriUsers.add(numeriUser);
    }

    public List<NumeriUser> getNumeriUsers() {
        List<NumeriUser> users = new ArrayList<>();
        synchronized (numeriUsers) {
            users.addAll(numeriUsers);
        }
        return users;
    }

    void clear() {
        for (NumeriUser numeriUser : numeriUsers) {
            numeriUser.getStreamSwitcher().closeStream();
        }
        numeriUsers.clear();
    }

    public void removeNumeriUser(NumeriUser numeriUser) {
        new Thread(() -> numeriUser.getStreamSwitcher().closeStream());
        numeriUsers.remove(numeriUser);
    }


    private static class NumeriUsersHolder {
        private static final NumeriUsers instance = new NumeriUsers();
    }

}
