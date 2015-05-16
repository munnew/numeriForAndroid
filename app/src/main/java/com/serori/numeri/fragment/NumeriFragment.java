package com.serori.numeri.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.serori.numeri.R;
import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.config.ConfigurationStorager;
import com.serori.numeri.fragment.listview.AttachedBottomListener;
import com.serori.numeri.fragment.listview.TimeLineListView;
import com.serori.numeri.twitter.SimpleTweetStatus;
import com.serori.numeri.fragment.listview.item.TimeLineItemAdapter;
import com.serori.numeri.user.NumeriUser;

import java.util.ArrayList;
import java.util.List;


/**
 * タイムラインを表示するFragmentが継承すべきクラス<br>
 * 親のActivityはNumeriActivityを継承している必要がある
 */
public abstract class NumeriFragment extends Fragment implements AttachedBottomListener {

    protected String name = "fragment";
    private NumeriUser numeriUser = null;

    private TimeLineListView timelineListView;
    private TimeLineItemAdapter adapter;
    private List<SimpleTweetStatus> timeLineItems;
    protected Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (!(getActivity() instanceof NumeriActivity))
            throw new IllegalStateException("親のアクティビティがNunmeriActivityを継承してません");
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);
        if (numeriUser == null)
            return rootView;//ランタイムエラー回避

        setRetainInstance(true);
        context = rootView.getContext();
        timelineListView = (TimeLineListView) rootView.findViewById(R.id.timeLineListView);
        timelineListView.setAttachedBottomListener(this);
        if (savedInstanceState == null) {
            timeLineItems = new ArrayList<>();
            adapter = new TimeLineItemAdapter(context, 0, timeLineItems);
            timelineListView.setAdapter(adapter);
            initializeLoad();
        } else {
            timelineListView.setAdapter(adapter);
        }
        timelineListView.onTouchItemEnabled(getNumeriUser(), getActivity());
        timelineListView.startObserveFavorite(getNumeriUser());
        return rootView;
    }

    protected TimeLineListView getTimelineListView() {
        return timelineListView;
    }

    protected TimeLineItemAdapter getAdapter() {
        return adapter;
    }

    protected List<SimpleTweetStatus> getTimeLineItems() {
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

    /**
     * タイムラインを取得して表示する処理を実装するメソッド
     */
    protected abstract void initializeLoad();

    /**
     * リストビューの一番下までスクロールした際に発生するイベントハンドラ
     */
    protected abstract void onAttachedBottom();

    @Override
    public void attachedBottom() {
        if (ConfigurationStorager.EitherConfigurations.CONFIRMATION_LESS_GET_TWEET.isEnabled()) {
            onAttachedBottom();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setMessage("ツイートを更に読み込みますか？")
                    .setNegativeButton("いいえ", (dialog, id) -> {
                    })
                    .setPositiveButton("はい", (dialog, id) -> {
                        onAttachedBottom();
                    })
                    .create();
            ((NumeriActivity) getActivity()).setCurrentShowDialog(alertDialog);
        }
    }

    /**
     * 親のNumeriActivityを取得する
     *
     * @return 親のNumeriActivity
     */
    protected NumeriActivity getNumeriActivity() {
        return (NumeriActivity) getActivity();
    }
}
