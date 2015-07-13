package com.serori.numeri.fragment;

import android.os.Handler;

import com.serori.numeri.main.manager.FragmentStorager;
import com.serori.numeri.twitter.SimpleTweetStatus;


import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;

/**
 * ホームタイムラインを表示するためのFragment
 */
public class TimeLineFragment extends TwitterStreamFragment {

    public TimeLineFragment() {
    }

    @Override
    public void setFragmentName(String name) {
        super.name = name + " : " + FragmentStorager.FragmentType.TL.getId();
    }


    @Override
    protected void initializeLoad() {
        Handler handler = new Handler();
        new Thread(() -> {
            Paging paging = new Paging();
            paging.setCount(30);
            ResponseList<Status> statuses = getTweets(paging, false);
            if (statuses == null) return;
            handler.post(() -> {
                for (Status status : statuses) {
                    getTimelineListView().getAdapter().add(SimpleTweetStatus.build(status, getNumeriUser()));
                }
                getNumeriUser().getStreamEvent().addOnStatusListener(this);
                getTimelineListView().onAttachedBottomCallbackEnabled(true);
            });
        }).start();
    }

    @Override
    protected void onAttachedBottom() {
        Handler handler = new Handler();
        new Thread(() -> {
            getTimelineListView().onAttachedBottomCallbackEnabled(false);
            Paging paging = new Paging();
            paging.setCount(31);
            paging.setMaxId(getTimelineListView().getAdapter().getItem(getTimelineListView().getAdapter().getCount() - 1).getStatusId());
            ResponseList<Status> statuses = getTweets(paging, true);
            handler.post(() -> {
                for (Status status : statuses) {
                    getTimelineListView().getAdapter().addAll(SimpleTweetStatus.build(status, getNumeriUser()));
                    getTimelineListView().onAttachedBottomCallbackEnabled(true);
                }
            });
        }).start();
    }


    @Override
    protected void onTweetStats(Status status) {
        getTimelineListView().insert(SimpleTweetStatus.build(status, getNumeriUser()));
    }

    @Override
    ResponseList<Status> getResponseList(Paging paging) throws TwitterException {
        return getNumeriUser().getTwitter().getHomeTimeline(paging);
    }


}
