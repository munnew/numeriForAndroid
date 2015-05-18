package com.serori.numeri.fragment;


import android.os.Handler;

import com.serori.numeri.twitter.SimpleTweetStatus;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 */
public class UserPublicTimeLineFragment extends NumeriFragment {
    private long userId = 0;

    @Override
    protected void initializeLoad() {
        if (userId > 0) {
            Paging paging = new Paging();
            paging.setCount(30);
            getTweets(paging, false);
        }
    }

    @Override
    protected void onAttachedBottom() {
        Paging paging = new Paging();
        paging.setCount(31);
        paging.setMaxId(getAdapter().getItem(getAdapter().getCount() - 1).getStatusId());
        getTweets(paging, true);
    }

    private void getTweets(Paging paging, boolean isBelowUnder) {
        Handler handler = new Handler();
        new Thread(() -> {
            getTimelineListView().onAttachedBottomCallbackEnabled(false);
            ResponseList<Status> favoriteStatuses = getResponseList(paging);
            handler.post(() -> {
                if (favoriteStatuses != null) {
                    if (!favoriteStatuses.isEmpty() && isBelowUnder)
                        favoriteStatuses.remove(0);
                    for (Status favoriteStatus : favoriteStatuses) {
                        getAdapter().add(SimpleTweetStatus.build(favoriteStatus, getNumeriUser()));
                    }
                }
                getTimelineListView().onAttachedBottomCallbackEnabled(true);
            });
        }).start();
    }

    private ResponseList<Status> getResponseList(Paging paging) {
        Twitter twitter = getNumeriUser().getTwitter();
        ResponseList<Status> responseList = null;
        try {
            responseList = twitter.getUserTimeline(userId, paging);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return responseList;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
