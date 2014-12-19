package com.serori.numeri.stream;

import twitter4j.Status;
import twitter4j.User;

/**
 * Created by serioriKETC on 2014/12/19.
 */
public interface onFavoriteListener {
    public  void onFavorite(User source, User target, Status favoritedStatus);
}
