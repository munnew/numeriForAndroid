package com.serori.numeri.util.twitter;

import android.os.Handler;

import com.serori.numeri.user.NumeriUser;

import java.util.LinkedHashMap;
import java.util.Map;

import twitter4j.RateLimitStatus;
import twitter4j.TwitterException;

/**
 */
public class TwitterAPIConfirmer {
    private NumeriUser numeriUser;

    public TwitterAPIConfirmer(NumeriUser numeriUser) {
        this.numeriUser = numeriUser;
    }

    public void acquireTwitterAPIRemaining(OnAcquiredAPIUseCountListener listener) {
        Handler handler = new Handler();
        new Thread(() -> {
            try {
                Map<String, RateLimitStatus> rateLimitStatus = numeriUser.getTwitter().getRateLimitStatus("statuses");
                Map<TwitterAPI, Integer> apiRemainingInfoMap = new LinkedHashMap<>();
                apiRemainingInfoMap.put(TwitterAPI.HOME_TIMELINE, rateLimitStatus.get(TwitterAPI.HOME_TIMELINE.getEndPoint()).getRemaining());
                apiRemainingInfoMap.put(TwitterAPI.MENTIONS_TIMELINE, rateLimitStatus.get(TwitterAPI.MENTIONS_TIMELINE.getEndPoint()).getRemaining());
                handler.post(() -> listener.onAcquiredAPIUseCount(apiRemainingInfoMap));
            } catch (TwitterException e) {
                TwitterExceptionDisplay.show(e);
            }
        }).start();
    }

    public enum TwitterAPI {
        HOME_TIMELINE("/statuses/home_timeline"),
        MENTIONS_TIMELINE("/statuses/mentions_timeline");

        private String endPoint;

        TwitterAPI(String endPoint) {
            this.endPoint = endPoint;
        }

        public String getEndPoint() {
            return endPoint;
        }
    }

}
