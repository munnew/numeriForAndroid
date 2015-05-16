package com.serori.numeri.fragment.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * 一番下まで見るとそれをコールバックしてくれる抽象クラスなListView
 */
public abstract class AttachedBottomCallBackListView extends ListView {
    private AttachedBottomListener attachedBottomListener;
    private boolean onAttachedBottom = false;
    private boolean onAttachedBottomCallbackEnabled = true;
    private OnItemScrollListener onItemScrollListener;
    private int _visibleItemCount = 0;
    private int _totalItemCount = 0;

    public AttachedBottomCallBackListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItemPosition, int visibleItemCount, int totalItemCount) {
                _visibleItemCount = visibleItemCount;
                _totalItemCount = totalItemCount;
                if (onItemScrollListener != null) {
                    onItemScrollListener.onItemScroll(view, firstVisibleItemPosition, visibleItemCount, totalItemCount);
                }
                if (attachedBottomListener != null && onAttachedBottomCallbackEnabled && !onAttachedBottom &&
                        firstVisibleItemPosition + visibleItemCount == totalItemCount && visibleItemCount < totalItemCount) {
                    int lastItemY = getChildAt(getChildCount() - 1).getHeight();
                    int lastItemPositionY = getChildAt(getChildCount() - 1).getTop();
                    int itemPositionTargetLine = getHeight() - lastItemY;
                    if (lastItemPositionY <= itemPositionTargetLine) {
                        attachedBottomListener.attachedBottom();
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

    protected int getVisibleItemCount() {
        return _visibleItemCount;
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

    public int getTotalItemCount() {
        return _totalItemCount;
    }
}