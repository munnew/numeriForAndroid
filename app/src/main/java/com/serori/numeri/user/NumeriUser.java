package com.serori.numeri.user;

import android.os.AsyncTask;
import android.util.Log;

import com.serori.numeri.R;
import com.serori.numeri.main.Application;
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
 * このアプリケーションを使用するユーザーを表す
 */
public class NumeriUser {
    private Twitter twitter;
    private StreamEvent streamEvent;
    private String screenName = null;
    private NumeriUserStorager.NumeriUserTable table;

    /**
     * このコンストラクタは非同期タスクで実行する必要があります
     *
     * @param table NumeriUserTable
     */
    public NumeriUser(NumeriUserStorager.NumeriUserTable table) {
        this.table = table;
        auth();
    }


    private void auth() {
        ConfigurationBuilder builder = new ConfigurationBuilder()
                .setOAuthConsumerKey(Application.getInstance().getApplicationContext().getString(R.string.twitter_consumer_key))
                .setOAuthConsumerSecret(Application.getInstance().getApplicationContext().getString(R.string.twitter_consumer_secret))
                .setOAuthAccessToken(table.getAccessToken()).setOAuthAccessTokenSecret(table.getAccessTokenSecret());
        twitter = new TwitterFactory(builder.build()).getInstance();
        try {
            screenName = twitter.getScreenName();
            if (!table.getScreenName().equals(screenName)) {
                AsyncTask.execute(() -> {
                    table.setScreenName(screenName);
                    NumeriUserStorager.getInstance().saveNumeriUser(table);
                });
            }
        } catch (TwitterException e) {
            screenName = table.getScreenName();
        }
        Log.v("user", "getScreenName");
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.setOAuthConsumer(Application.getInstance().getApplicationContext().getString(R.string.twitter_consumer_key),
                Application.getInstance().getApplicationContext().getString(R.string.twitter_consumer_secret));
        twitterStream.setOAuthAccessToken(new AccessToken(table.getAccessToken(), table.getAccessTokenSecret()));
        streamEvent = new StreamEvent(twitterStream);
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public AccessToken getAccessToken() {
        return new AccessToken(table.getAccessToken(), table.getAccessTokenSecret());
    }

    /**
     * @return IStreamEvent
     */
    public IStreamEvent getStreamEvent() {
        return streamEvent;
    }

    /**
     * @return StreamSwitcher
     */
    public StreamSwitcher getStreamSwitcher() {
        return streamEvent;
    }

    /**
     * TweetBuilderを取得します
     *
     * @return TweetBuilder
     */
    public TweetBuilder getTweetBuilder() {
        return new TweetBuilder(this);
    }

    public String getScreenName() {
        return screenName;
    }
}
