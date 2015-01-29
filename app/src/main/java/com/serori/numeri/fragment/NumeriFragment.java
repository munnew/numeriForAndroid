package com.serori.numeri.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.serori.numeri.main.Application;
import com.serori.numeri.user.NumeriUser;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment
 */
public abstract class NumeriFragment extends Fragment implements AttachedBottomListener {

    protected String name = "fragment";
    private NumeriUser numeriUser = null;

    private NumeriListView timelineListView;
    private TimeLineItemAdapter adapter;
    private List<TimeLineItem> timeLineItems;
    protected Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (numeriUser == null) {
            throw new NullPointerException("フラグメントにユーザーがセットされていません");
        }
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);
        Log.v(name, "crate");
        setRetainInstance(true);
        context = rootView.getContext();
        timelineListView = (NumeriListView) rootView.findViewById(R.id.timeLineListView);
        timelineListView.setAttachedBottomListener(this);
        if (savedInstanceState == null) {
            timeLineItems = new ArrayList<>();
            adapter = new TimeLineItemAdapter(context, 0, timeLineItems);
            timelineListView.setAdapter(adapter);
            initializeLoad();
        } else {
            timelineListView.setAdapter(adapter);
            Log.v("restoredInfo:", name + getNumeriUser().getAccessToken().getUserId());
        }
        timelineListView.onTouchItemEnabled(getNumeriUser(), getActivity());
        return rootView;
    }

    protected NumeriListView getTimelineListView() {
        return timelineListView;
    }

    protected TimeLineItemAdapter getAdapter() {
        return adapter;
    }

    protected List<TimeLineItem> getTimeLineItems() {
        return timeLineItems;
    }

    public String getFragmentName() {
        return name;
    }

    public void setFragmentName(String name) {
        this.name = name;
    }

    public void setNumeriUser(NumeriUser numeriUser) {
        this.numeriUser = numeriUser;
    }

    protected NumeriUser getNumeriUser() {
        return numeriUser;
    }

    protected abstract void initializeLoad();

    protected abstract void onAttachedBottom(TimeLineItem item);

    @Override
    public void attachedBottom(TimeLineItem item) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setMessage("ツイートを更に読み込みますか？")
                .setNegativeButton("いいえ", (dailog, id) -> {
                })
                .setPositiveButton("はい", (dialog, id) -> {
                    this.onAttachedBottom(item);
                })
                .create();
        ((NumeriActivity) getActivity()).setCurrentShowDialog(alertDialog);
    }

    protected Activity getMainActivity() {
        return ((Activity) Application.getInstance().getMainActivityContext());
    }
}
