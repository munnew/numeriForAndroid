package com.serori.numeri.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.serori.numeri.R;
import com.serori.numeri.temp.activity.NumeriActivity;
import com.serori.numeri.config.ConfigurationStorager;
import com.serori.numeri.listview.AttachedBottomListener;
import com.serori.numeri.listview.TimeLineListView;
import com.serori.numeri.twitter.SimpleTweetStatus;
import com.serori.numeri.listview.item.TimeLineItemAdapter;
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
    private boolean enableAttachedBottomDialog = true;
    private TimeLineListView timelineListView;
    private TimeLineItemAdapter adapter;
    protected Context context;

    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (!(getActivity() instanceof NumeriActivity))
            throw new IllegalStateException("親のアクティビティがNunmeriActivityを継承してません");
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);
        if (numeriUser == null)
            return rootView;

        setRetainInstance(true);
        context = rootView.getContext();
        timelineListView = (TimeLineListView) rootView.findViewById(R.id.timeLineListView);
        timelineListView.setAttachedBottomListener(this);
        if (savedInstanceState == null) {
            List<SimpleTweetStatus> timeLineItems = new ArrayList<>();
            adapter = new TimeLineItemAdapter(context, 0, timeLineItems);
            timelineListView.setAdapter(adapter);
            initializeLoad();
        } else {
            timelineListView.setAdapter(adapter);
        }
        timelineListView.setNumeriUser(numeriUser);
        return rootView;
    }

    /**
     * TimeLineItemAdapterをセットされたListView
     *
     * @return TimeLineListView
     */
    protected final TimeLineListView getTimelineListView() {
        return timelineListView;
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

    public final NumeriUser getNumeriUser() {
        return this.numeriUser;
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
            if (enableAttachedBottomDialog) {
                enableAttachedBottomDialog = false;
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setMessage("ツイートを更に読み込みますか？")
                        .setNegativeButton("キャンセル", (dialog, id) -> {
                            enableAttachedBottomDialog = true;
                        })
                        .setPositiveButton("はい", (dialog, id) -> {
                            onAttachedBottom();
                            enableAttachedBottomDialog = true;
                        }).setOnDismissListener(dialog -> enableAttachedBottomDialog = true)
                        .create();
                ((NumeriActivity) getActivity()).setCurrentShowDialog(alertDialog);
            }
        }
    }

}
