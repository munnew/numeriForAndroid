package com.serori.numeri.fragment;

import com.serori.numeri.listview.item.TimeLineItem;

/**
 * AttachedBottomListener
 */
public interface AttachedBottomListener {
    /**
     * リストビューの一番下までスクロールした際のイベントハンドラ
     *
     * @param item 一番下のアイテム
     */
    void attachedBottom(TimeLineItem item);
}
