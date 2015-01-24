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

    private Actions actions;

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

    @Override
    public ListAdapter getAdapter() {
        return super.getAdapter();
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
        if (getAdapter() == null) {
            throw new NullPointerException("adapterがセットされていません");
        }
        actions = new Actions(context, numeriUser, (TimeLineItemAdapter) getAdapter());
        setOnItemClickListener((parent, view, position, id) -> {
            Log.v("ontTouchItem", "" + getTouchedCoordinates());
            ((TimeLineItemAdapter) getAdapter()).setCurrentVeiw(view);
            switch (getTouchedCoordinates()) {
                case LEFT:
                    actions.onTouchAction(Actions.FAVORITE, position);
                    break;
                case CENTER:
                    actions.onTouchAction(Actions.RT, position);
                    break;
                case RIGHT:
                    actions.onTouchAction(Actions.REPLY, position);
                    break;
                default:
                    break;

            }
        });
        setOnItemLongClickListener((parent, view, position, id) -> {
            switch (getTouchedCoordinates()) {
                case LEFT:
                    break;
                case CENTER:
                    break;
                case RIGHT:
                    actions.onTouchAction(Actions.MENU, position);
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
