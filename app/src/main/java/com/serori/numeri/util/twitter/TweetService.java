package com.serori.numeri.util.twitter;


import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.main.Global;

/**
 * TweetService
 */
public class TweetService extends IntentService {
    private static final String NAME = "Tweet";
    private static Tweet tweet;

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

    public static void sendTweet(Context context, Tweet tweet) {
        if (!(context instanceof NumeriActivity)) return;
        NotificationManager notificationManager = (NotificationManager) Global.getInstance()
                .getMainActivityContext()
                .getSystemService(NOTIFICATION_SERVICE);

        tweet.setNotificationManager(notificationManager);
        TweetService.tweet = tweet;
        Intent intent = new Intent(context, TweetService.class);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(toString(), "onHandleIntent");
        tweet.tweet();
    }
}
