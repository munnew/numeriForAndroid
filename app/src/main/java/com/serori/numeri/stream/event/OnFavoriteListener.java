package com.serori.numeri.stream.event;

import twitter4j.Status;
import twitter4j.User;

/**
 * OnFavoriteListener
 */
public interface OnFavoriteListener {
    void onFavorite(User source, User target, Status favoritedStatus);
}
