package com.serori.numeri.imageview.cache;

import android.graphics.Bitmap;

/**
 * OnLoadImageCompletedListener
 */
public interface OnLoadImageCompletedListener {
    /**
     * 画像のロードが終了した際のイベントハンドラ
     *
     * @param image ロードした画像
     * @param key   ロードした画像のurl
     */
    void onLoadImageCompleted(Bitmap image, String key);


}
