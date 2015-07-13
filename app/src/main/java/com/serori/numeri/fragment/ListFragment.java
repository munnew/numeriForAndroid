package com.serori.numeri.fragment;


import android.os.Handler;

import com.serori.numeri.twitter.SimpleTweetStatus;
import com.serori.numeri.util.twitter.TwitterExceptionDisplay;

import java.util.ArrayList;
import java.util.List;

import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * ユーザーリストを表示するFragment
 */
public class ListFragment extends TwitterStreamFragment {

    private long listId;
    private List<Long> listUserIds = new ArrayList<>();

    public void setListId(long id) {
        listId = id;
    }

    @Override
    public String getFragmentName() {
        return super.name;
    }

    @Override
    protected void initializeLoad() {
        Handler handler = new Handler();
        new Thread(() -> {
            try {
                long nextCursor = -1;
                while (nextCursor != 0) {
                    PagableResponseList<User> users = getNumeriUser().getTwitter().getUserListMembers(listId, nextCursor);
                    for (User user : users) {
                        listUserIds.add(user.getId());
                    }
                    nextCursor = users.getNextCursor();
                }
                Paging paging = new Paging();
                paging.setCount(30);
                ResponseList<Status> statuses = getTweets(paging, false);
                if (statuses == null) return;
                handler.post(() -> {
                    for (Status status : statuses) {
                        getTimelineListView().getAdapter().add(SimpleTweetStatus.build(status, getNumeriUser()));
                    }
                    getNumeriUser().getStreamEvent().addOnStatusListener(this);
                });
            } catch (TwitterException e) {
                TwitterExceptionDisplay.show(e);
            }
        }).start();
    }


    @Override
    protected void onAttachedBottom() {
        Handler handler = new Handler();
        new Thread(() -> {
            getTimelineListView().onAttachedBottomCallbackEnabled(false);
            Paging paging = new Paging();
            paging.setMaxId(getTimelineListView().getAdapter().getItem(getTimelineListView().getAdapter().getCount() - 1).getStatusId());
            paging.count(31);
            ResponseList<Status> statuses = getTweets(paging, true);
            if (statuses == null) return;
            handler.post(() -> {
                for (Status status : statuses) {
                    getTimelineListView().getAdapter().add(SimpleTweetStatus.build(status, getNumeriUser()));
                }
                getTimelineListView().onAttachedBottomCallbackEnabled(true);
            });
        }).start();
    }

    @Override
    protected void onTweetStats(Status status) {
        for (Long listUserId : listUserIds) {
            if (status.getUser().getId() == listUserId && status.getUserMentionEntities().length == 0) {
                getTimelineListView().insert(SimpleTweetStatus.build(status, getNumeriUser()));
                break;
            }
        }
    }

    @Override
    ResponseList<Status> getResponseList(Paging paging) throws TwitterException {
        return getNumeriUser().getTwitter().getUserListStatuses(listId, paging);
    }
}
