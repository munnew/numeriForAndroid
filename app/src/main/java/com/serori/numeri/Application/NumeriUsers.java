package com.serori.numeri.application;

import com.serori.numeri.user.NumeriUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seroriKETC on 2014/12/20.
 */
public class NumeriUsers {
    private List<NumeriUser> numeriUsers = new ArrayList<>();

    private NumeriUsers() {
    }

    protected static NumeriUsers getInstance() {
        return NumeriUsersHolder.instance;
    }

    public void addNumeriUser(NumeriUser numeriUser) {
        numeriUsers.add(numeriUser);
    }

    public List<NumeriUser> getNumeriUsers() {
        List<NumeriUser> users = new ArrayList<>();
        users.addAll(numeriUsers);
        return users;
    }

    public void clear() {
        for (NumeriUser numeriUser : numeriUsers) {
            numeriUser.getStreamSwicher().closeStream();
        }
        numeriUsers.clear();
    }

    private static class NumeriUsersHolder {
        private static final NumeriUsers instance = new NumeriUsers();
    }

}
