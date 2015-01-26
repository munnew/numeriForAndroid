package com.serori.numeri.user;

import android.util.Log;

import com.serori.numeri.R;
import com.serori.numeri.application.Application;
import com.serori.numeri.stream.IStreamEvent;
import com.serori.numeri.stream.StreamEvent;
import com.serori.numeri.stream.StreamSwitcher;
import com.serori.numeri.util.twitter.TweetBuilder;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * need Create on Async
 */
public class NumeriUser {
    private AccessToken token;
    private Twitter twitter;
    private StreamEvent streamEvent;
    private String screenName = "user";


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
        try {
            screenName = twitter.getScreenName();
        } catch (TwitterException e) {
            e.printStackTrace();
            screenName = null;
        }
        Log.v("user", "getScreenName");
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.setOAuthConsumer(Application.getInstance().getApplicationContext().getString(R.string.twitter_consumer_key),
                Application.getInstance().getApplicationContext().getString(R.string.twitter_consumer_secret));
        twitterStream.setOAuthAccessToken(token);
        streamEvent = new StreamEvent(twitterStream);
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public AccessToken getAccessToken() {
        return token;
    }


    public IStreamEvent getStreamEvent() {
        return streamEvent;
    }

    public StreamSwitcher getStreamSwitcher() {
        return streamEvent;
    }

    public TweetBuilder getTweetBuilder() {
        return new TweetBuilder(this);
    }

    public String getScreenName() {
        return screenName;
    }
}
