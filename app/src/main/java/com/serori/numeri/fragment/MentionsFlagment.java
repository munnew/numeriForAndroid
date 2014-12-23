package com.serori.numeri.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.serori.numeri.R;
import com.serori.numeri.stream.OnStatusListener;
import com.serori.numeri.user.NumeriUser;

import java.util.ArrayList;
import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.UserMentionEntity;

/**
 * Created by seroriKETC on 2014/12/23.
 */
public class MentionsFlagment extends Fragment implements NumeriFragment, OnStatusListener {
    String name;
    NumeriUser numeriUser;
    ListView MentionsListView;
    TimeLineItemAdapter adapter;
    List<TimeLineItem> timeLineItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);
        Log.v("Mentions", "cleate");
        if (savedInstanceState == null) {
            MentionsListView = (ListView) rootView.findViewById(R.id.timeLineListView);
            timeLineItems = new ArrayList<>();
            adapter = new TimeLineItemAdapter(rootView.getContext(), 0, timeLineItems);
            MentionsListView.setAdapter(adapter);
            initializeLoad();
            numeriUser.getStreamEvent().addOwnerOnStatusListener(this);
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
                getActivity().runOnUiThread(() -> {
                    for (Status status : timeLine) {
                        adapter.add(new TimeLineItem(status, numeriUser));
                    }
                });
            }
        });
    }

    private ResponseList<Status> loadMentions(Twitter twitter) {
        try {
            return twitter.getMentionsTimeline();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onStatus(Status status) {
        for (UserMentionEntity userMentionEntity : status.getUserMentionEntities()) {
            if (userMentionEntity.getId() == numeriUser.getAccessToken().getUserId()) {
                getActivity().runOnUiThread(() -> adapter.add(new TimeLineItem(status, numeriUser)));
            }
        }
    }
}
