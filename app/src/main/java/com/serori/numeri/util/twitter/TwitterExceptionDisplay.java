package com.serori.numeri.util.twitter;

import com.serori.numeri.util.toast.ToastSender;

import java.text.SimpleDateFormat;

import twitter4j.TwitterException;

/**
 *
 */
public class TwitterExceptionDisplay {
    public static void show(TwitterException e) {
        String info = null;
        if (e.exceededRateLimitation()) {
            info = "exceeded rate limitation" + "\n remaining: " + (e.getRateLimitStatus().getSecondsUntilReset() / 60) + "minutes";
        } else if (e.isCausedByNetworkIssue()) {
            info = "ネットワークを確認してください";
        }
        if (info != null) {
            ToastSender.sendToast(info);
        } else {
            ToastSender.sendToast(e.getErrorMessage());
        }
    }
}
