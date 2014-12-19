package com.serori.numeri.OAuth;

import com.serori.numeri.stream.StreamEvent;

import twitter4j.Twitter;
import twitter4j.auth.AccessToken;

/**
 * Created by serioriKETC on 2014/12/19.
 */
public class NumeriUserStorager implements NumeriUser{










    @Override
    public Twitter getTwitter() {
        return null;
    }

    @Override
    public StreamEvent getStreamEvent() {
        return null;
    }

    @Override
    public AccessToken getAccessToken() {
        return null;
    }
}
