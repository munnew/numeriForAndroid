package com.serori.numeri.listview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.serori.numeri.fragment.AttachedBottomListener;
import com.serori.numeri.listview.action.ActionStorager;
import com.serori.numeri.listview.action.TwitterActions;
import com.serori.numeri.listview.item.TimeLineItem;
import com.serori.numeri.listview.item.TimeLineItemAdapter;
import com.serori.numeri.main.Application;
import com.serori.numeri.user.NumeriUser;

import java.util.ArrayList;
import java.util.List;


/**
 * タイムラインを表示するためのリストビュー
 */
public class NumeriListView extends ListView {
    private float touchedCoordinatesX;
    private AttachedBottomListener attachedBottomListener;
    private boolean onAttachedBottom = false;
    private boolean onAttachedBottomCallbackEnabled = true;
    private boolean insertItemEnable = true;

    private static final int LEFT = 0;
    private static final int CENTER = 1;
    private static final int RIGHT = 2;

    private TwitterActions twitterAction;


    private List<TimeLineItem> storedItems = new ArrayList<>();

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

                if (firstVisibleItemPosition == 0 && !storedItems.isEmpty()) {
                    Log.v("ListView", "InsertEnable:" + insertItemEnable);
                    insertItemEnable = false;
                    while (!storedItems.isEmpty()) {
                        ((TimeLineItemAdapter) getAdapter()).insert(storedItems.get(0), 0);
                        storedItems.remove(0);
                    }
                    setSelection(storedItems.size());
                    insertItemEnable = true;
                }

                if (attachedBottomListener != null && onAttachedBottomCallbackEnabled && !onAttachedBottom &&
                        firstVisibleItemPosition + visibleItemCount == totalItemCount && visibleItemCount < totalItemCount) {
                    int lastItemY = getChildAt(getChildCount() - 1).getHeight();
                    int lastItemPositionY = getChildAt(getChildCount() - 1).getTop();
                    int itemPositionTargetLine = getHeight() - lastItemY;
                    if (lastItemPositionY <= itemPositionTargetLine) {
                        attachedBottomListener.attachedBottom((TimeLineItem) getAdapter().getItem(totalItemCount - 1));
                        onAttachedBottom = true;
                    }
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

    /**
     * タッチによるアクションの実行を有効にする
     *
     * @param numeriUser アクションを実行するユーザー
     * @param context    Context
     */
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

        twitterAction = new TwitterActions(context, numeriUser, (TimeLineItemAdapter) getAdapter());

        setOnItemClickListener((parent, view, position, id) -> {
            Log.v("ontTouchItem", "" + getTouchedCoordinates());
            ((TimeLineItemAdapter) getAdapter()).setCurrentView(view);
            switch (getTouchedCoordinates()) {
                case LEFT:
                    twitterAction.onTouchAction(ActionStorager.RespectTapPositionActions.LEFT, position);
                    break;
                case CENTER:
                    twitterAction.onTouchAction(ActionStorager.RespectTapPositionActions.CENTER, position);
                    break;
                case RIGHT:
                    twitterAction.onTouchAction(ActionStorager.RespectTapPositionActions.RIGHT, position);
                    break;
                default:
                    break;

            }
        });
        setOnItemLongClickListener((parent, view, position, id) -> {
            switch (getTouchedCoordinates()) {
                case LEFT:
                    twitterAction.onTouchAction(ActionStorager.RespectTapPositionActions.LONG_LEFT, position);
                    break;
                case CENTER:
                    twitterAction.onTouchAction(ActionStorager.RespectTapPositionActions.LONG_CENTER, position);
                    break;
                case RIGHT:
                    twitterAction.onTouchAction(ActionStorager.RespectTapPositionActions.LONG_RIGHT, position);
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
        this.onAttachedBottomCallbackEnabled = onAttachedBottomCallbackEnabled;
    }

    public void insertItem(TimeLineItem item) {
        if (getFirstVisiblePosition() == 0 && insertItemEnable) {
            Application.getInstance().runOnUiThread(() -> ((TimeLineItemAdapter) getAdapter()).insert(item, 0));
        } else {
            storedItems.add(item);
        }
    }
}
