package com.serori.numeri.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

import com.serori.numeri.fragment.AttachedBottomListener;
import com.serori.numeri.twitter.SimpleTweetStatus;

/**
 * 一番下まで見るとそれをコールバックしてくれる抽象クラスなListView
 */
public abstract class AttachedBottomCallBackListView extends ListView {
    private AttachedBottomListener attachedBottomListener;
    private boolean onAttachedBottom = false;
    private boolean onAttachedBottomCallbackEnabled = true;
    private OnItemScrollListener onItemScrollListener;


    public AttachedBottomCallBackListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItemPosition, int visibleItemCount, int totalItemCount) {
                if (onItemScrollListener != null) {
                    onItemScrollListener.onItemScroll(view, firstVisibleItemPosition, visibleItemCount, totalItemCount);
                }
                if (attachedBottomListener != null && onAttachedBottomCallbackEnabled && !onAttachedBottom &&
                        firstVisibleItemPosition + visibleItemCount == totalItemCount && visibleItemCount < totalItemCount) {
                    int lastItemY = getChildAt(getChildCount() - 1).getHeight();
                    int lastItemPositionY = getChildAt(getChildCount() - 1).getTop();
                    int itemPositionTargetLine = getHeight() - lastItemY;
                    if (lastItemPositionY <= itemPositionTargetLine) {
                        attachedBottomListener.attachedBottom((SimpleTweetStatus) getAdapter().getItem(totalItemCount - 1));
                        onAttachedBottom = true;
                    }
                } else if (firstVisibleItemPosition + visibleItemCount <= totalItemCount - 1) {
                    onAttachedBottom = false;
                }
            }
        });
    }

    public void setAttachedBottomListener(AttachedBottomListener listener) {
        attachedBottomListener = listener;
    }


    public void setOnItemScrollListener(OnItemScrollListener onItemScrollListener) {
        this.onItemScrollListener = onItemScrollListener;
    }

    /**
     * コールバックするか否かを切り替える
     *
     * @param onAttachedBottomCallbackEnabled true : コールバックする false : コールバックしない
     */
    public void onAttachedBottomCallbackEnabled(boolean onAttachedBottomCallbackEnabled) {
        this.onAttachedBottomCallbackEnabled = onAttachedBottomCallbackEnabled;
    }
}