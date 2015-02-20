package com.serori.numeri.listview;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.serori.numeri.R;
import com.serori.numeri.fragment.AttachedBottomListener;
import com.serori.numeri.listview.action.ActionStorager;
import com.serori.numeri.listview.action.TwitterActions;
import com.serori.numeri.listview.item.TimeLineItemAdapter;
import com.serori.numeri.main.Application;
import com.serori.numeri.twitter.SimpleTweetStatus;
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

    private int _firstVisibleItemPosition = 0;
    private int _visibleItemCount = 0;
    private int _totalItemCount = 0;

    private int currentY = 0;
    private static final int LEFT = 0;
    private static final int CENTER = 1;
    private static final int RIGHT = 2;
    private TwitterActions twitterAction;


    private List<SimpleTweetStatus> storedItems = new ArrayList<>();

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
                _firstVisibleItemPosition = firstVisibleItemPosition;
                _visibleItemCount = visibleItemCount;
                _totalItemCount = totalItemCount;
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
                        attachedBottomListener.attachedBottom((SimpleTweetStatus) getAdapter().getItem(totalItemCount - 1));
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

    public void startObserveFavorite(NumeriUser numeriUser) {
        numeriUser.getStreamEvent().addOnFavoriteListener((user1, user2, favoritedStatus) -> {

            SimpleTweetStatus simpleTweetStatus = SimpleTweetStatus.build(favoritedStatus, numeriUser);
            if (user1.getId() == numeriUser.getAccessToken().getUserId())
                ((Activity) getContext()).runOnUiThread(() -> setFavoriteStar(simpleTweetStatus, true));

        }).addOnUnFavoriteListener((user1, user2, unFavoritedStatus) -> {

            SimpleTweetStatus simpleTweetStatus = SimpleTweetStatus.build(unFavoritedStatus, numeriUser);
            if (user1.getId() == numeriUser.getAccessToken().getUserId())
                ((Activity) getContext()).runOnUiThread(() -> setFavoriteStar(simpleTweetStatus, false));

        });
    }

    /**
     * お気に入りの星の表示、非表示を切り替える
     *
     * @param simpleTweetStatus 切り替えるSimpleTweetStatus
     * @param enabled           true : 表示 : false 非表示
     */
    private void setFavoriteStar(SimpleTweetStatus simpleTweetStatus, boolean enabled) {
        for (int i = 0; i < _visibleItemCount; i++) {
            SimpleTweetStatus simpleTweetStatus1 = ((SimpleTweetStatus) getAdapter().getItem(_firstVisibleItemPosition + i));
            if (simpleTweetStatus.equals(simpleTweetStatus1)) {
                if (enabled) {
                    getChildAt(i).findViewById(R.id.favoriteStar).setVisibility(VISIBLE);
                    Log.v(getClass().toString(), "favorite");
                } else {
                    getChildAt(i).findViewById(R.id.favoriteStar).setVisibility(GONE);
                    Log.v(getClass().toString(), "unFavorite");
                }
            }
        }
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

    public void insertItem(SimpleTweetStatus item) {
        if (getFirstVisiblePosition() == 0 && insertItemEnable) {
            Application.getInstance().runOnUiThread(() -> ((TimeLineItemAdapter) getAdapter()).insert(item, 0));
        } else {
            storedItems.add(item);
        }
    }
}
