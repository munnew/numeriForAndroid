package com.serori.numeri.user;

import android.os.AsyncTask;
import android.util.Log;

import com.serori.numeri.hoge.Application;
import com.serori.numeri.R;
import com.serori.numeri.stream.IStreamEvent;
import com.serori.numeri.stream.StreamEvent;
import com.serori.numeri.stream.StreamOwner;

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
    private StreamEvent streamEvent = new StreamEvent();
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

    public String getScreenName() {
        return screenName;
    }
}
