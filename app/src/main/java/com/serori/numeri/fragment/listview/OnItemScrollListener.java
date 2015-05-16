package com.serori.numeri.fragment.listview;

import android.widget.AbsListView;

/**
 *
 */
public interface OnItemScrollListener {
    void onItemScroll(AbsListView view, int firstVisibleItemPosition, int visibleItemCount, int totalItemCount);
}
