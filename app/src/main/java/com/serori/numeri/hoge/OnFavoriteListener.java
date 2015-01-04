package com.serori.numeri.hoge;

import twitter4j.Status;
import twitter4j.User;

/**
 * Created by serioriKETC on 2014/12/19.
 */
public interface OnFavoriteListener {
    public  void onFavorite(User source, User target, Status favoritedStatus);
}
