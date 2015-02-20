package com.serori.numeri.fragment;

import com.serori.numeri.twitter.SimpleTweetStatus;

/**
 * AttachedBottomListener
 */
public interface AttachedBottomListener {
    /**
     * リストビューの一番下までスクロールした際のイベントハンドラ
     *
     * @param item 一番下のアイテム
     */
    void attachedBottom(SimpleTweetStatus item);
}
