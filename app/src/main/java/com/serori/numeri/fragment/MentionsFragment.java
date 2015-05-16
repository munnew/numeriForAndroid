package com.serori.numeri.fragment;

import com.serori.numeri.main.manager.FragmentStorager;
import com.serori.numeri.stream.event.OnStatusListener;
import com.serori.numeri.twitter.SimpleTweetStatus;
import com.serori.numeri.util.async.SimpleAsyncTask;
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
public class MentionsFragment extends NumeriFragment implements OnStatusListener {

    @Override
    public void setFragmentName(String name) {
        super.name = name + " : " + FragmentStorager.FragmentType.MENTIONS.getId();
    }

    @Override
    protected void initializeLoad() {
        SimpleAsyncTask.backgroundExecute(() -> {
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
            ResponseList<Status> responseStatuses = twitter.getMentionsTimeline(pages);
            if (!responseStatuses.isEmpty())
                responseStatuses.remove(0);
            return responseStatuses;
        } catch (TwitterException e) {
            TwitterExceptionDisplay.show(e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onAttachedBottom() {
        getTimelineListView().onAttachedBottomCallbackEnabled(false);
        SimpleAsyncTask.backgroundExecute(() -> {
            ResponseList<Status> previousTimeLine = loadPreviousMentionsTimeLine(getNumeriUser().getTwitter(), getAdapter().getItem(getAdapter().getCount() - 1).getStatusId());
            if (previousTimeLine != null) {
                List<SimpleTweetStatus> items = new ArrayList<>();
                for (Status status : previousTimeLine) {
                    items.add(SimpleTweetStatus.build(status, getNumeriUser()));
                }
                getActivity().runOnUiThread(() -> {
                    getAdapter().addAll(items);
                    getTimelineListView().onAttachedBottomCallbackEnabled(true);
                });
            } else {
                getTimelineListView().onAttachedBottomCallbackEnabled(true);
            }

        });
    }
}
