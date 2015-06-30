package com.serori.numeri.util.twitter;

import com.serori.numeri.util.toast.ToastSender;


import twitter4j.TwitterException;

/**
 *
 */
public class TwitterExceptionDisplay {
    public static void show(TwitterException e) {
        String info = null;
        if (e.exceededRateLimitation()) {
            info = "exceeded rate limitation" + "\n revival: " + (e.getRateLimitStatus().getSecondsUntilReset() / 60) + "m"
                    + (e.getRateLimitStatus().getSecondsUntilReset() % 60) + "s\n";
            info += " limit: " + e.getRateLimitStatus().getRemaining() + "/" + e.getRateLimitStatus().getLimit();
        } else if (e.isCausedByNetworkIssue()) {
            info = "ネットワークを確認してください";
        }
        if (info != null) {
            ToastSender.sendToast(info);
        } else {
            info = e.getErrorMessage();
            if (info != null) {
                ToastSender.sendToast(info);
            }
        }
    }
}
