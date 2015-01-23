package com.serori.numeri.listview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.serori.numeri.listview.action.Actions;
import com.serori.numeri.application.Application;
import com.serori.numeri.fragment.AttachedBottomListener;
import com.serori.numeri.listview.item.TimeLineItem;
import com.serori.numeri.listview.item.TimeLineItemAdapter;
import com.serori.numeri.user.NumeriUser;

import java.util.ArrayList;
import java.util.List;


/**
 * NumeriListView
 */
public class NumeriListView extends ListView {
    private float touchedCoordinatesX;
    private AttachedBottomListener attachedBottomListener;
    private boolean onAttachedBottom = false;
    private boolean OnAttachedBottomCallbackEnabled = false;

    private static final int LEFT = 0;
    private static final int CENTER = 1;
    private static final int RIGHT = 2;

    private List<TimeLineItem> storeedItems = new ArrayList<>();

    public NumeriListView(Context context) {
        super(context);
    }

    public NumeriListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumeriListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setAttachedBottomListener(AttachedBottomListener listener) {
        attachedBottomListener = listener;


        setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItemPosition, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItemPosition == 0 && !storeedItems.isEmpty()) {
                    int y = getChildAt(0).getTop();
                    for (TimeLineItem storeedItem : storeedItems) {
                        ((TimeLineItemAdapter) getAdapter()).insert(storeedItem, 0);
                    }
                    setSelectionFromTop(storeedItems.size(), y);

                    storeedItems.clear();
                }
                if (attachedBottomListener != null && OnAttachedBottomCallbackEnabled && !onAttachedBottom &&
                        firstVisibleItemPosition + visibleItemCount == totalItemCount && visibleItemCount < totalItemCount) {
                    attachedBottomListener.AttachedBottom((TimeLineItem) getAdapter().getItem(totalItemCount - 1));
                    onAttachedBottom = true;
                } else if (firstVisibleItemPosition + visibleItemCount <= totalItemCount - 1) {
                    onAttachedBottom = false;
                }
            }
        });
    }


    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        touchedCoordinatesX = ev.getX();
        return super.onTouchEvent(ev);
    }

    private int getTouchedCoordinates() {
        float windowX = Application.getInstance().getWindowSize().x;
        if (windowX / 3 > touchedCoordinatesX)
            return LEFT;
        if (windowX / 3 * 2 > touchedCoordinatesX)
            return CENTER;
        if (windowX > touchedCoordinatesX)
            return RIGHT;

        throw new InternalError("何らかの原因で返される値が正常ではありません");
    }


    public void onTouchItemEnabled(NumeriUser numeriUser, Context context) {
        if (numeriUser == null) {
            throw new NullPointerException("numeriUserがセットされていません");
        }
        if (context == null) {
            throw new NullPointerException("contextがセットされていません");
        }
        setOnItemClickListener((parent, view, position, id) -> {
            TimeLineItem item = (TimeLineItem) getAdapter().getItem(position);
            Log.v("ontTouchItem", "" + getTouchedCoordinates());

            switch (getTouchedCoordinates()) {
                case LEFT:
                    Actions.getInstance().onTouchAction(context, Actions.FAVORITE, item, numeriUser, view);
                    break;
                case CENTER:
                    Actions.getInstance().onTouchAction(context, Actions.RT, item, numeriUser, view);
                    break;
                case RIGHT:
                    Actions.getInstance().onTouchAction(context, Actions.REPLY, item, numeriUser, view);
                    break;
                default:
                    break;

            }
        });
        setOnItemLongClickListener((parent, view, position, id) -> {
            TimeLineItem item = (TimeLineItem) getAdapter().getItem(position);
            switch (getTouchedCoordinates()) {
                case LEFT:
                    break;
                case CENTER:
                    break;
                case RIGHT:
                    Actions.getInstance().onTouchAction(context, Actions.MENU, item, numeriUser, view);
                    break;
                default:
                    break;
            }
            return true;
        });
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (!(adapter instanceof TimeLineItemAdapter))
            throw new IllegalArgumentException("AdapterがTimeLineItemAdapterを継承していません");
        super.setAdapter(adapter);
    }

    public void onAttachedBottomCallbackEnabled(boolean onAttachedBottomCallbackEnabled) {
        OnAttachedBottomCallbackEnabled = onAttachedBottomCallbackEnabled;
    }

    public void insertItem(TimeLineItem item) {
        if (getFirstVisiblePosition() == 0) {
            ((TimeLineItemAdapter) getAdapter()).insert(item, 0);
        } else {
            storeedItems.add(item);
        }
    }

}
