package com.serori.numeri.stream;

import com.serori.numeri.stream.event.OnDeletionNoticeListener;
import com.serori.numeri.stream.event.OnFavoriteListener;
import com.serori.numeri.stream.event.OnFollowListener;
import com.serori.numeri.stream.event.OnStatusListener;
import com.serori.numeri.stream.event.OnUnFavoriteListener;
import com.serori.numeri.stream.event.OnUnFollowListener;

import java.io.InputStream;

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
     * onFavoriteイベントを追加する
     *
     * @param listener OnFavoriteListener
     * @return IStreamEvent
     */
    IStreamEvent addOnFavoriteListener(OnFavoriteListener listener);

    /**
     * onUnFavoriteイベントを追加する
     *
     * @param listener OnUnFavoriteListener
     * @return IStreamEvent
     */
    IStreamEvent addOnUnFavoriteListener(OnUnFavoriteListener listener);

    /**
     * onDeletionNoticeイベントを追加する
     *
     * @param listener OnDeletionNoticeListener
     * @return IStreamEvent
     */
    IStreamEvent addOnDeletionNoticeListener(OnDeletionNoticeListener listener);

    /**
     * onFollowイベントを追加する
     *
     * @param listener OnFollowListener
     * @return IStreamEvent
     */
    IStreamEvent addOnFollowListener(OnFollowListener listener);

    /**
     * onUnFollowイベントを追加する
     *
     * @param listener OnUnFollowListener
     * @return IStreamEvent
     */
    IStreamEvent addOnUnFollowListener(OnUnFollowListener listener);

    /**
     * onStatusイベントをリムーブする
     *
     * @param listener OnStatusListener
     */
    void removeOnStatusListener(OnStatusListener listener);

    /**
     * onFavoriteイベントをリムーブする
     *
     * @param listener OnFavoriteListener
     */
    void removeOnFavoriteListener(OnFavoriteListener listener);

    /**
     * onUnFavoriteイベントをリムーブする
     *
     * @param listener OnUnFavoriteListener
     */
    void removeOnUnFavoriteListener(OnUnFavoriteListener listener);

    /**
     * onDeletionNoticeイベントをリムーブする
     *
     * @param listener OnDeletionNoticeListener
     */
    void removeOnDeletionNoticeListener(OnDeletionNoticeListener listener);

    /**
     * onFollowイベントをリムーブする
     *
     * @param listener OnFollowListener
     */
    void removeOnFollowListener(OnFollowListener listener);

    /**
     * onUnFollowイベントをリムーブする
     *
     * @param listener OnUnFollowListener
     */
    void removeOnUnFollowListener(OnUnFollowListener listener);
}
