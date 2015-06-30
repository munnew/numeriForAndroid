package com.serori.numeri.listview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.serori.numeri.config.ConfigurationStorager;

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
    private boolean enabledFastScroll;

    public AttachedBottomCallBackListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        enabledFastScroll = ConfigurationStorager.EitherConfigurations.USE_FAST_SCROLL.isEnabled();
        setFastScrollEnabled(enabledFastScroll);
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
                if (enabledFastScroll != ConfigurationStorager.EitherConfigurations.USE_FAST_SCROLL.isEnabled()) {
                    enabledFastScroll = ConfigurationStorager.EitherConfigurations.USE_FAST_SCROLL.isEnabled();
                    setFastScrollEnabled(enabledFastScroll);
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

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int i = _visibleItemCount + 1; i >= 0; i--) {
                    View item = getChildAt(i);
                    if (item != null && y >= item.getY()) {
                        onTouchItem(item);
                        break;
                    }
                }
                break;
            default:
                for (int i = _visibleItemCount + 1; i >= 0; i--) {
                    View item = getChildAt(i);
                    if (item != null)
                        onDeTouch(item);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    protected void onTouchItem(View touchedItem) {

    }

    protected void onDeTouch(View visibleItem) {

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