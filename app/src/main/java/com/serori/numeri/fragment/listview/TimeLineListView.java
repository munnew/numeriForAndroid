package com.serori.numeri.fragment.listview;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListAdapter;

import com.serori.numeri.R;
import com.serori.numeri.fragment.listview.action.ActionStorager;
import com.serori.numeri.fragment.listview.action.TwitterActions;
import com.serori.numeri.fragment.listview.item.TimeLineItemAdapter;
import com.serori.numeri.main.Global;
import com.serori.numeri.twitter.SimpleTweetStatus;
import com.serori.numeri.user.NumeriUser;

import java.util.ArrayList;
import java.util.List;


/**
 * タイムラインを表示するためのリストビュー
 */
public class TimeLineListView extends AttachedBottomCallBackListView {
    private float touchedCoordinatesX;
    private boolean insertItemEnable = true;

    private int _firstVisibleItemPosition = 0;
    private int _visibleItemCount = 0;

    private static final int LEFT = 0;
    private static final int CENTER = 1;
    private static final int RIGHT = 2;
    private TwitterActions twitterAction;


    private List<SimpleTweetStatus> storedItems = new ArrayList<>();


    public TimeLineListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnItemScrollListener((view, firstVisibleItemPosition, visibleItemCount, totalItemCount) -> {
            _firstVisibleItemPosition = firstVisibleItemPosition;
            _visibleItemCount = visibleItemCount;
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
        });
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        touchedCoordinatesX = ev.getX();
        return super.onTouchEvent(ev);
    }


    private int getTouchedCoordinates() {
        float windowX = Global.getInstance().getWindowSize().x;
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

    /**
     * ユーザーのお気に入り動作を監視し星をつけたり消したりし始めるためのメソッド
     *
     * @param numeriUser ユーザー
     */
    public void startObserveFavorite(NumeriUser numeriUser) {
        numeriUser.getStreamEvent().addOnFavoriteListener((user1, user2, favoritedStatus) -> {
            if (user1.getId() == numeriUser.getAccessToken().getUserId()) {
                SimpleTweetStatus simpleTweetStatus = SimpleTweetStatus.build(favoritedStatus, numeriUser);
                ((Activity) getContext()).runOnUiThread(() -> setFavoriteStar(simpleTweetStatus, true));
            }

        }).addOnUnFavoriteListener((user1, user2, unFavoritedStatus) -> {
            if (user1.getId() == numeriUser.getAccessToken().getUserId()) {
                SimpleTweetStatus simpleTweetStatus = SimpleTweetStatus.build(unFavoritedStatus, numeriUser);
                ((Activity) getContext()).runOnUiThread(() -> setFavoriteStar(simpleTweetStatus, false));

            }
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
                } else {
                    getChildAt(i).findViewById(R.id.favoriteStar).setVisibility(GONE);
                }
            }
        }
    }

    public void startObserveDestroyTweet(NumeriUser numeriUser) {
        numeriUser.getStreamEvent().addOnDeletionNoticeListener(statusDeletionNotice -> {
            long deletedStatusId = statusDeletionNotice.getStatusId();
            for (int i = 0; i < _visibleItemCount; i++) {
                SimpleTweetStatus simpleTweetStatus = ((SimpleTweetStatus) getAdapter().getItem(_firstVisibleItemPosition + i));
                if (simpleTweetStatus.getStatusId() == deletedStatusId) {
                    removeViewAt(i);
                }
            }
            ((TimeLineItemAdapter) getAdapter()).remove(deletedStatusId);
        });
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (!(adapter instanceof TimeLineItemAdapter))
            throw new IllegalArgumentException("AdapterがTimeLineItemAdapterを継承していません");
        super.setAdapter(adapter);
    }


    public void insertItem(SimpleTweetStatus item) {
        if (getFirstVisiblePosition() == 0 && insertItemEnable) {
            Global.getInstance().runOnUiThread(() -> ((TimeLineItemAdapter) getAdapter()).insert(item, 0));
        } else {
            storedItems.add(item);
        }
    }
}
