package com.serori.numeri.stream.event;

import twitter4j.Status;
import twitter4j.User;

/**
 * OnUnFavoriteListener
 */
public interface OnUnFavoriteListener {
    void onUnfavorite(User source, User target, Status unfavoritedStatus);
}
