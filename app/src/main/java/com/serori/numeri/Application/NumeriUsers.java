package com.serori.numeri.Application;

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

    private static class NumeriUsersHolder {
        private static final NumeriUsers instance = new NumeriUsers();
    }

}
