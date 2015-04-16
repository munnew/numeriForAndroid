package com.serori.numeri.main;

import com.serori.numeri.user.NumeriUser;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * FragmentOwnerRegister
 */
public class FragmentOwnerRegister {

    private Map<String, NumeriUser> FragmentOwnerMap = new LinkedHashMap<>();

    FragmentOwnerRegister() {
    }

    /**
     * Fragmentの持ち主を記憶する
     *
     * @param fragmentName    fragmentの名前
     * @param ownerScreenName Fragmentの持ち主の名前
     * @return 直前にputされたNumeriUser 直前のputがない場合はnullを返す
     */
    public NumeriUser put(String fragmentName, String ownerScreenName) {
        NumeriUser numeriUser = null;
        for (NumeriUser user : NumeriUsers.getInstance().getNumeriUsers()) {
            if (ownerScreenName.equals(user.getScreenName())) {
                numeriUser = user;
                break;
            }
        }
        if (numeriUser == null) throw new IllegalArgumentException("与えられたScreenNameのユーザーは存在しません");
        return FragmentOwnerMap.put(fragmentName, numeriUser);
    }

    /**
     * Fragmentの持ち主を取得する
     *
     * @param fragmentName fragmentの持ち主の名前
     * @return 持ち主であるNumeriUser
     */
    public NumeriUser get(String fragmentName) {
        return FragmentOwnerMap.get(fragmentName);
    }

}
