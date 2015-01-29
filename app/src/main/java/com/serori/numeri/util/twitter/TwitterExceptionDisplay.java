package com.serori.numeri.util.twitter;

import com.serori.numeri.util.toast.ToastSender;

import twitter4j.TwitterException;

/**
 * Created by serioriKETC on 2015/01/29.
 */
public class TwitterExceptionDisplay {
    public static void show(TwitterException e) {
        String info = null;
        if (e.exceededRateLimitation()) {
            info = "exceededRateLimitation";
        } else if (e.isCausedByNetworkIssue()) {
            info = "ネットワークを確認してください";
        }
        if (info != null) {
            ToastSender.sendToast(info);
        }
    }
}
