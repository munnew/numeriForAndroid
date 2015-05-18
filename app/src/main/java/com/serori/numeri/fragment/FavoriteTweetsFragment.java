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
public class FavoriteTweetsFragment extends NumeriFragment {
    private long userId = 0;

    @Override
    protected void initializeLoad() {
        if (userId > 0) {
            Paging paging = new Paging();
            paging.setCount(30);
            getFavoriteTweets(paging, false);
        }
    }

    @Override
    protected void onAttachedBottom() {
        Paging paging = new Paging();
        paging.setCount(30);
        paging.setMaxId(getAdapter().getItem(getAdapter().getCount() - 1).getStatusId());
        getFavoriteTweets(paging, true);
    }

    private void getFavoriteTweets(Paging paging, boolean isBelowUnder) {
        Handler handler = new Handler();
        new Thread(() -> {
            getTimelineListView().onAttachedBottomCallbackEnabled(false);
            ResponseList<Status> favoriteStatuses = getFavoriteResponseList(paging);
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

    private ResponseList<Status> getFavoriteResponseList(Paging paging) {
        Twitter twitter = getNumeriUser().getTwitter();
        ResponseList<Status> responseList = null;
        try {
            responseList = twitter.getFavorites(userId, paging);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return responseList;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
