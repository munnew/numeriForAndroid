package com.serori.numeri.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.serori.numeri.R;
import com.serori.numeri.application.Application;
import com.serori.numeri.listview.NumeriListView;
import com.serori.numeri.listview.item.TimeLineItem;
import com.serori.numeri.listview.item.TimeLineItemAdapter;
import com.serori.numeri.stream.OnStatusListener;
import com.serori.numeri.user.NumeriUser;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.UserMentionEntity;

public class MentionsFlagment extends Fragment implements NumeriFragment, OnStatusListener, AttachedBottomListener {
    private String name;
    private NumeriUser numeriUser;
    private NumeriListView mentionsListView;
    private TimeLineItemAdapter adapter;
    private List<TimeLineItem> timeLineItems;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);
        Log.v("Mentions", "cleate");
        setRetainInstance(true);
        context = rootView.getContext();
        mentionsListView = (NumeriListView) rootView.findViewById(R.id.timeLineListView);
        mentionsListView.onTouchItemEnabled(numeriUser, context);
        mentionsListView.setAttachedBottomListener(this);
        if (savedInstanceState == null) {
            timeLineItems = new ArrayList<>();
            adapter = new TimeLineItemAdapter(context, 0, timeLineItems);
            mentionsListView.setAdapter(adapter);
            initializeLoad();
        } else {
            mentionsListView.setAdapter(adapter);
            mentionsListView.onAttachedBottomCallbackEnabled(true);
        }

        return rootView;
    }


    @Override
    public String getFragmentName() {
        return name;
    }

    @Override
    public void setFragmentName(String name) {
        this.name = name + " : Mentions";
    }

    @Override
    public void setNumeriUser(NumeriUser numeriUser) {
        this.numeriUser = numeriUser;
    }

    private void initializeLoad() {
        AsyncTask.execute(() -> {
            Twitter twitter = numeriUser.getTwitter();
            ResponseList<Status> timeLine = loadMentions(twitter);
            if (timeLine != null) {
                List<TimeLineItem> items = new ArrayList<>();
                for (Status status : timeLine) {
                    items.add(new TimeLineItem(status, numeriUser));
                }
                getActivity().runOnUiThread(() -> {
                    adapter.addAll(items);
                    numeriUser.getStreamEvent().addOwnerOnStatusListener(this);
                    mentionsListView.onAttachedBottomCallbackEnabled(true);
                });
            }
        });
    }

    private ResponseList<Status> loadMentions(Twitter twitter) {
        try {
            return twitter.getMentionsTimeline();
        } catch (TwitterException e) {
            Application.getInstance().onToast("ツイートを読み込めませんでした。ストリームを開始します", Toast.LENGTH_SHORT);
            numeriUser.getStreamEvent().addOwnerOnStatusListener(this);
            mentionsListView.onAttachedBottomCallbackEnabled(true);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onStatus(Status status) {
        for (UserMentionEntity userMentionEntity : status.getUserMentionEntities()) {
            if (userMentionEntity.getId() == numeriUser.getAccessToken().getUserId() && !status.isRetweet()) {
                TimeLineItem item = new TimeLineItem(status, numeriUser);
                getActivity().runOnUiThread(() -> mentionsListView.insertItem(item));
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
            Application.getInstance().onToast("ネットワークを確認して下さい", Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void AttachedBottom(TimeLineItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("ツイートを更に読み込みますか？")
                .setNegativeButton("いいえ", (dailog, id) -> {
                })
                .setPositiveButton("はい", (dialog, id) -> {
                    AsyncTask.execute(() -> {
                        mentionsListView.onAttachedBottomCallbackEnabled(false);
                        ResponseList<Status> previousTimeLine = loadPreviousMentionsTimeLine(numeriUser.getTwitter(), item.getStatusId());
                        if (previousTimeLine != null) {
                            List<TimeLineItem> items = new ArrayList<>();
                            for (Status status : previousTimeLine) {
                                items.add(new TimeLineItem(status, numeriUser));
                            }
                            getActivity().runOnUiThread(() -> adapter.addAll(items));
                        }
                        mentionsListView.onAttachedBottomCallbackEnabled(true);
                    });
                })
                .create().show();
    }
}
