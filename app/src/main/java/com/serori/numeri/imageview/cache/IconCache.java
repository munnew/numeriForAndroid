package com.serori.numeri.imageview.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class IconCache {
    private static final int cacheSize = 5 * 1024;//5000MB
    private OnLoadImageCompletedListener onLoadImageCompletedListener;
    private static LruCache<String, Bitmap> iconCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap image) {
            return image.getByteCount() / 1024;
        }
    };


    public void loadImage(String url) {
        AsyncTask.execute(() -> {
            Bitmap image = iconCache.get(url);
            if (image != null) {
                onLoadImageCompletedListener.onLoadImageCompleted(image);
            } else {
                try {
                    InputStream inputStream = new URL(url).openStream();
                    image = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    iconCache.put(url, image);
                    onLoadImageCompletedListener.onLoadImageCompleted(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setOnLoadImageCompletedListener(OnLoadImageCompletedListener listener) {
        onLoadImageCompletedListener = listener;
    }
}
