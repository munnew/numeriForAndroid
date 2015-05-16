package com.serori.numeri.stream.event;

import twitter4j.User;

/**
 * OnFollowListener
 */
public interface OnFollowListener {
    void onFollow(User source, User followedUser);
}
