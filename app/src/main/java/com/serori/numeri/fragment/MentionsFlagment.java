package com.serori.numeri.fragment;

import android.os.AsyncTask;

import com.serori.numeri.fragment.manager.FragmentStorager;
import com.serori.numeri.twitter.SimpleTweetStatus;
import com.serori.numeri.stream.event.OnStatusListener;
import com.serori.numeri.util.toast.ToastSender;
import com.serori.numeri.util.twitter.TwitterExceptionDisplay;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.UserMentionEntity;

/**
 * ユーザーへのリプライを表示するFragment
 */
public class MentionsFlagment extends NumeriFragment implements OnStatusListener {

    @Override
    public void setFragmentName(String name) {
        super.name = name + " : " + FragmentStorager.FragmentType.MENTIONS.getId();
    }

    @Override
    protected void initializeLoad() {
        AsyncTask.execute(() -> {
            Twitter twitter = getNumeriUser().getTwitter();
            ResponseList<Status> timeLine = loadMentions(twitter);
            if (timeLine != null) {
                List<SimpleTweetStatus> items = new ArrayList<>();
                for (Status status : timeLine) {
                    items.add(SimpleTweetStatus.build(status, getNumeriUser()));
                }

                getActivity().runOnUiThread(() -> {
                    getAdapter().addAll(items);
                    getNumeriUser().getStreamEvent().addOnStatusListener(this);
                });
            }
        });
    }


    private ResponseList<Status> loadMentions(Twitter twitter) {
        try {
            return twitter.getMentionsTimeline();
        } catch (TwitterException e) {
            ToastSender.sendToast("ツイートを読み込めませんでした。ストリームを開始します");
            getNumeriUser().getStreamEvent().addOnStatusListener(this);
            getTimelineListView().onAttachedBottomCallbackEnabled(true);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onStatus(Status status) {
        for (UserMentionEntity userMentionEntity : status.getUserMentionEntities()) {
            if (userMentionEntity.getId() == getNumeriUser().getAccessToken().getUserId() && !status.isRetweet()) {
                SimpleTweetStatus item = SimpleTweetStatus.build(status, getNumeriUser());
                getTimelineListView().insertItem(item);
            }
        }
    }

    private ResponseList<Status> loadPreviousMentionsTimeLine(Twitter twitter, long statusId) {
        try {
            Paging pages = new Paging();
            pages.setMaxId(statusId);
            pages.count(31);
            ResponseList<Status> responceStatuses = twitter.getMentionsTimeline(pages);
            responceStatuses.remove(0);
            return responceStatuses;
        } catch (TwitterException e) {
            TwitterExceptionDisplay.show(e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onAttachedBottom(SimpleTweetStatus item) {
        AsyncTask.execute(() -> {
            getTimelineListView().onAttachedBottomCallbackEnabled(false);
            ResponseList<Status> previousTimeLine = loadPreviousMentionsTimeLine(getNumeriUser().getTwitter(), item.getStatusId());
            if (previousTimeLine != null) {
                List<SimpleTweetStatus> items = new ArrayList<>();
                for (Status status : previousTimeLine) {
                    items.add(SimpleTweetStatus.build(status, getNumeriUser()));
                }
                getActivity().runOnUiThread(() -> getAdapter().addAll(items));
            }
            getTimelineListView().onAttachedBottomCallbackEnabled(true);
        });
    }
}
