package com.serori.numeri.temp.imageview;

import android.graphics.Bitmap;

/**
 * OnLoadCompletedListener
 */
public interface OnLoadCompletedListener {
    /**
     * imageViewへの画像のロードが終了した際のイベントハンドラ
     *
     * @param bitmap ロードした画像
     */
    void onLoadCompleted(Bitmap bitmap);
}
