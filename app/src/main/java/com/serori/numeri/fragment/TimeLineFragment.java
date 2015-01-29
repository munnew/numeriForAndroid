package com.serori.numeri.fragment;

import android.os.AsyncTask;

import com.serori.numeri.fragment.manager.FragmentStorager;
import com.serori.numeri.listview.item.TimeLineItem;
import com.serori.numeri.stream.OnStatusListener;
import com.serori.numeri.util.toast.ToastSender;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;


public class TimeLineFragment extends NumeriFragment implements OnStatusListener {

    public TimeLineFragment() {
    }

    @Override
    public void setFragmentName(String name) {
        super.name = name + " : " + FragmentStorager.FragmentType.TL.getId();
    }

    @Override
    public void onStatus(Status status) {
        getMainActivity().runOnUiThread(() -> getTimelineListView().insertItem(new TimeLineItem(status, getNumeriUser())));
    }

    @Override
    protected void initializeLoad() {
        AsyncTask.execute(() -> {
            Twitter twitter = getNumeriUser().getTwitter();
            ResponseList<Status> timeLine = loadTimeLine(twitter);
            if (timeLine != null) {
                List<TimeLineItem> items = new ArrayList<>();
                for (Status status : timeLine) {
                    items.add(new TimeLineItem(status, getNumeriUser()));
                }
                getMainActivity().runOnUiThread(() -> {
                    getAdapter().addAll(items);
                    getNumeriUser().getStreamEvent().addOwnerOnStatusListener(this);
                });
            }
        });
    }

    private ResponseList<Status> loadTimeLine(Twitter twitter) {
        try {
            return twitter.getHomeTimeline();
        } catch (TwitterException e) {
            ToastSender.sendToast("ツイートを読み込めませんでした。ストリームを開始します");
            getNumeriUser().getStreamEvent().addOwnerOnStatusListener(this);
            e.printStackTrace();
        }
        return null;
    }

    private ResponseList<Status> loadPreviousTimeLine(Twitter twitter, long statusId) {
        try {
            Paging pages = new Paging();
            pages.setMaxId(statusId);
            pages.count(31);
            ResponseList<Status> responseStatuses = twitter.getHomeTimeline(pages);
            responseStatuses.remove(0);
            return responseStatuses;
        } catch (TwitterException e) {
            e.printStackTrace();
            if (e.exceededRateLimitation()) {
                ToastSender.sendToast("exceededRateLimitation");
            } else {
                ToastSender.sendToast("ネットワークを確認して下さい");
            }
        }
        return null;
    }

    @Override
    protected void onAttachedBottom(TimeLineItem item) {
        AsyncTask.execute(() -> {
            getTimelineListView().onAttachedBottomCallbackEnabled(false);
            ResponseList<Status> previousTimeLine = loadPreviousTimeLine(getNumeriUser().getTwitter(), item.getStatusId());
            if (previousTimeLine != null) {
                List<TimeLineItem> items = new ArrayList<>();
                for (Status status : previousTimeLine) {
                    items.add(new TimeLineItem(status, getNumeriUser()));
                }
                getMainActivity().runOnUiThread(() -> getAdapter().addAll(items));
            }
            getTimelineListView().onAttachedBottomCallbackEnabled(true);
        });
    }
}
