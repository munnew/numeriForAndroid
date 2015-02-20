package com.serori.numeri.oauth;


/**
 * OAuthActivityに表示するリストビューのセルのモデルクラス
 */
public class NumeriUserListItem {

    private String screenName;
    private String token;

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
