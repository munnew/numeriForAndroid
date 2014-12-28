package com.serori.numeri.user;

import com.serori.numeri.application.Application;
import com.serori.numeri.R;
import com.serori.numeri.stream.IStreamEvent;
import com.serori.numeri.stream.StreamEvent;
import com.serori.numeri.stream.StreamOwner;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by seroriKETC on 2014/12/19.
 */
public class NumeriUser {
    private AccessToken token;
    private Twitter twitter;
    private StreamEvent streamEvent = new StreamEvent();

    public NumeriUser(AccessToken token) {
        this.token = token;
        auth();
    }


    private void auth() {
        ConfigurationBuilder builder = new ConfigurationBuilder()
                .setOAuthConsumerKey(Application.getInstance().getApplicationContext().getString(R.string.twitter_consumer_key))
                .setOAuthConsumerSecret(Application.getInstance().getApplicationContext().getString(R.string.twitter_consumer_secret))
                .setOAuthAccessToken(token.getToken()).setOAuthAccessTokenSecret(token.getTokenSecret());
        twitter = new TwitterFactory(builder.build()).getInstance();
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.setOAuthConsumer(Application.getInstance().getApplicationContext().getString(R.string.twitter_consumer_key),
                Application.getInstance().getApplicationContext().getString(R.string.twitter_consumer_secret));
        twitterStream.setOAuthAccessToken(token);
        streamEvent.setUserStream(twitterStream);
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public AccessToken getAccessToken() {
        return token;
    }


    public StreamOwner getStreamEvent() {
        return streamEvent;
    }

    public IStreamEvent getStreamSwicher() {
        return streamEvent;
    }
}
