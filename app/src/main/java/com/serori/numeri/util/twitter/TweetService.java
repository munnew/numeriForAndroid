package com.serori.numeri.util.twitter;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.serori.numeri.activity.NumeriActivity;

/**
 */
public class TweetService extends IntentService {
    private static final String NAME = "Tweet";
    private static TweetBuilder tweetBuilder;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public TweetService(String name) {
        super(name);
    }

    public TweetService() {
        super(NAME);
    }

    public static void sendTweet(Context context, TweetBuilder tweetBuilder) {
        if (!(context instanceof NumeriActivity)) return;
        TweetService.tweetBuilder = tweetBuilder;
        Intent intent = new Intent(context, TweetService.class);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(toString(), "onHandleIntent");
        tweetBuilder.tweet();
    }
}
