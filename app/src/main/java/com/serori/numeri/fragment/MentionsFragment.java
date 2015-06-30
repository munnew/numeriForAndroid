package com.serori.numeri.fragment;

import android.os.Handler;

import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.main.manager.FragmentStorager;
import com.serori.numeri.stream.event.OnStatusListener;
import com.serori.numeri.twitter.SimpleTweetStatus;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.util.twitter.TwitterExceptionDisplay;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.UserMentionEntity;

/**
 * ユーザーへのリプライを表示するFragment
 */
public class MentionsFragment extends NumeriFragment implements OnStatusListener {

    @Override
    public void setFragmentName(String name) {
        super.name = name + " : " + FragmentStorager.FragmentType.MENTIONS.getId();
    }

    @Override
    protected void initializeLoad() {
        Handler handler = new Handler();
        new Thread(() -> {
            Paging paging = new Paging();
            paging.setCount(30);
            ResponseList<Status> statuses = getMentionsTweetsList(paging, false);
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
    public void onStatus(Status status) {
        for (UserMentionEntity userMentionEntity : status.getUserMentionEntities()) {
            if (userMentionEntity.getId() == getNumeriUser().getAccessToken().getUserId() && !status.isRetweet()) {
                getTimelineListView().insert(SimpleTweetStatus.build(status, getNumeriUser()));
            }
        }
    }

    @Override
    protected void onAttachedBottom() {
        Handler handler = new Handler();
        new Thread(() -> {
            getTimelineListView().onAttachedBottomCallbackEnabled(false);
            Paging paging = new Paging();
            paging.setCount(31);
            paging.setMaxId(getTimelineListView().getAdapter().getItem(getTimelineListView().getAdapter().getCount() - 1).getStatusId());
            ResponseList<Status> statuses = getMentionsTweetsList(paging, true);
            handler.post(() -> {
                for (Status status : statuses) {
                    getTimelineListView().getAdapter().addAll(SimpleTweetStatus.build(status, getNumeriUser()));
                    getTimelineListView().onAttachedBottomCallbackEnabled(true);
                }
            });
        }).start();
    }

    private ResponseList<Status> getMentionsTweetsList(Paging paging, boolean isBelowUnder) {
        ResponseList<Status> statuses = null;
        try {
            statuses = getNumeriUser().getTwitter().getMentionsTimeline(paging);
            if (!statuses.isEmpty() && isBelowUnder) {
                statuses.remove(0);
            }
        } catch (TwitterException e) {
            e.printStackTrace();
            TwitterExceptionDisplay.show(e);
        }
        return statuses;
    }

    @Override
    public void onDestroy() {
        NumeriUser numeriUser = getNumeriUser();
        if (numeriUser != null)
            getNumeriUser().getStreamEvent().removeOnStatusListener(this);
        super.onDestroy();
    }
}
