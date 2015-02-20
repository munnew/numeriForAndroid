package com.serori.numeri.stream;

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
}
