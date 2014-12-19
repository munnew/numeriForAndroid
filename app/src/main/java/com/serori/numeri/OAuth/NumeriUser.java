package com.serori.numeri.OAuth;

import com.serori.numeri.stream.StreamEvent;

import twitter4j.*;
import twitter4j.auth.AccessToken;

/**
 * Created by serioriKETC on 2014/12/19.
 */
public interface NumeriUser {

    Twitter getTwitter();

    StreamEvent getStreamEvent();

    AccessToken getAccessToken();

}
