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


/**
 * Created by seroriKETC on 2014/12/20.
 */
public class TimeLineFragment extends Fragment implements NumeriFragment, OnStatusListener {

    public TimeLineFragment() {
    }

    private String fragmentName;
    private ListView timeLineListView;
    private List<TimeLineItem> timeLineItems;
    private TimeLineItemAdapter adapter;
    private NumeriUser numeriUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);
        Log.v("Timeline", "cleate");
        if (savedInstanceState == null) {
            timeLineListView = (ListView) rootView.findViewById(R.id.timeLineListView);
            timeLineItems = new ArrayList<>();
            adapter = new TimeLineItemAdapter(rootView.getContext(), 0, timeLineItems);
            timeLineListView.setAdapter(adapter);
            initializeLoad();
            numeriUser.getStreamEvent().addOwnerOnStatusListener(this);
        }

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
        Log.v(getFragmentName(), "" + status.getText());
        getActivity().runOnUiThread(() -> adapter.insert(new TimeLineItem(status, numeriUser), 0));

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

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
                getActivity().runOnUiThread(() -> {
                    for (Status status : timeLine) {
                        adapter.add(new TimeLineItem(status, numeriUser));
                    }
                });
            }
        });
    }

    private ResponseList<Status> loadTimeLine(Twitter twitter) {
        try {
            return twitter.getHomeTimeline();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
