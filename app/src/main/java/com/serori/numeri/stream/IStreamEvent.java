package com.serori.numeri.stream;

import com.serori.numeri.stream.event.OnDeletionNoticeListener;
import com.serori.numeri.stream.event.OnFavoriteListener;
import com.serori.numeri.stream.event.OnStatusListener;
import com.serori.numeri.stream.event.OnUnFavoriteListener;

/**
 * Streamのイベントを追加していくためのインターフェース
 */
public interface IStreamEvent {
    /**
     * onStatusイベントを追加する
     *
     * @param listener OnStatusListener
     * @return IStreamEvent
     */
    IStreamEvent addOnStatusListener(OnStatusListener listener);

    /**
     * onStatusイベントをリムーブする
     *
     * @param listener OnStatusListener
     */
    void removeOnStatusListener(OnStatusListener listener);

    /**
     * onFavoriteイベントを追加する
     *
     * @param listener OnFavoriteListener
     * @return IStreamEvent
     */
    IStreamEvent addOnFavoriteListener(OnFavoriteListener listener);

    /**
     * onFavoriteイベントをリムーブする
     *
     * @param listener OnFavoriteListener
     */
    void removeOnFavoriteListener(OnFavoriteListener listener);

    /**
     * onUnFavoriteイベントを追加する
     *
     * @param listener OnUnFavoriteListener
     * @return IStreamEvent
     */
    IStreamEvent addOnUnFavoriteListener(OnUnFavoriteListener listener);

    /**
     * onUnFavoriteイベントをリムーブする
     *
     * @param listener OnUnFavoriteListener
     */
    void removeOnUnFavoriteListener(OnUnFavoriteListener listener);

    /**
     * onDeletionNoticeイベントを追加する
     *
     * @param listener OnDeletionNoticeListener
     */
    void addOnDeletionNoticeListener(OnDeletionNoticeListener listener);

    /**
     * onDeletionNoticeイベントをリムーブする
     *
     * @param listener OnDeletionNoticeListener
     */
    void removeOnDeletionNoticeListener(OnDeletionNoticeListener listener);

}
