package com.serori.numeri.fragment;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.serori.numeri.R;
import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.listview.NumeriListView;
import com.serori.numeri.listview.item.TimeLineItem;
import com.serori.numeri.listview.item.TimeLineItemAdapter;
import com.serori.numeri.stream.OnStatusListener;
import com.serori.numeri.toast.ToastSender;
import com.serori.numeri.user.NumeriUser;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;


public class TimeLineFragment extends Fragment implements NumeriFragment, OnStatusListener, AttachedBottomListener {

    public TimeLineFragment() {
    }

    private String fragmentName;
    private NumeriListView timeLineListView;
    private List<TimeLineItem> timeLineItems;
    private TimeLineItemAdapter adapter;
    private NumeriUser numeriUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);
        setRetainInstance(true);
        timeLineListView = (NumeriListView) rootView.findViewById(R.id.timeLineListView);
        timeLineListView.setAttachedBottomListener(this);
        if (savedInstanceState == null) {
            timeLineItems = new ArrayList<>();
            adapter = new TimeLineItemAdapter(rootView.getContext(), 0, timeLineItems);
            timeLineListView.setAdapter(adapter);
            initializeLoad();
        } else {
            timeLineListView.setAdapter(adapter);
            timeLineListView.onAttachedBottomCallbackEnabled(true);
            Log.v("restoredinfo:", fragmentName + numeriUser.getAccessToken().getUserId());
        }

        timeLineListView.onTouchItemEnabled(numeriUser, getActivity());
        return rootView;
    }

    @Override
    public String getFragmentName() {
        return fragmentName;
    }

    @Override
    public void setFragmentName(String name) {
        fragmentName = name + " : TimeLine";
    }

    @Override
    public void onStatus(Status status) {
        getActivity().runOnUiThread(() -> timeLineListView.insertItem(new TimeLineItem(status, numeriUser)));
    }

    @Override
    public void setNumeriUser(NumeriUser numeriUser) {
        this.numeriUser = numeriUser;
    }

    private void initializeLoad() {
        AsyncTask.execute(() -> {
            Twitter twitter = numeriUser.getTwitter();
            ResponseList<Status> timeLine = loadTimeLine(twitter);
            if (timeLine != null) {
                List<TimeLineItem> items = new ArrayList<>();
                for (Status status : timeLine) {
                    items.add(new TimeLineItem(status, numeriUser));
                }
                getActivity().runOnUiThread(() -> {
                    adapter.addAll(items);
                    numeriUser.getStreamEvent().addOwnerOnStatusListener(this);
                    timeLineListView.onAttachedBottomCallbackEnabled(true);
                });
            }
        });
    }

    private ResponseList<Status> loadTimeLine(Twitter twitter) {
        try {
            return twitter.getHomeTimeline();
        } catch (TwitterException e) {
            ToastSender.getInstance().sendToast("ツイートを読み込めませんでした。ストリームを開始します");
            numeriUser.getStreamEvent().addOwnerOnStatusListener(this);
            timeLineListView.onAttachedBottomCallbackEnabled(true);
            e.printStackTrace();
        }
        return null;
    }

    private ResponseList<Status> loadPreviousTimeLine(Twitter twitter, long statusId) {
        try {
            Paging pages = new Paging();
            pages.setMaxId(statusId);
            pages.count(31);
            ResponseList<Status> responceStatuses = twitter.getHomeTimeline(pages);
            responceStatuses.remove(0);
            return responceStatuses;
        } catch (TwitterException e) {
            ToastSender.getInstance().sendToast("ネットワークを確認して下さい");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void AttachedBottom(TimeLineItem item) {
        Log.v("TL", "onAttach");
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage("ツイートを更に読み込みますか？")
                .setNegativeButton("いいえ", (dialog, id) -> {
                })
                .setPositiveButton("はい", (daialog, id) -> {
                    AsyncTask.execute(() -> {
                        timeLineListView.onAttachedBottomCallbackEnabled(false);
                        ResponseList<Status> previousTimeLine = loadPreviousTimeLine(numeriUser.getTwitter(), item.getStatusId());
                        if (previousTimeLine != null) {
                            List<TimeLineItem> items = new ArrayList<>();
                            for (Status status : previousTimeLine) {
                                items.add(new TimeLineItem(status, numeriUser));
                            }
                            getActivity().runOnUiThread(() -> adapter.addAll(items));
                        }
                        timeLineListView.onAttachedBottomCallbackEnabled(true);
                    });
                })
                .create();
        ((NumeriActivity) getActivity()).setCurrentShowDialog(alertDialog);
    }
}
