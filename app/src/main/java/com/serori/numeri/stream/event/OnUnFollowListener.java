package com.serori.numeri.stream.event;

import twitter4j.User;

/**
 * OnUnFollowListener
 */
public interface OnUnFollowListener {
    void onUnFollow(User source, User unfollowedUser);
}
