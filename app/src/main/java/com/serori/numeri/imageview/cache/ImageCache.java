package com.serori.numeri.imageview.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.serori.numeri.util.async.AsyncTaskUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ImageCache {
    private static final int maxCacheSize = 1 * 1024 * 1024;
    private static int currentCacheSize = 0;

    private AsyncTaskUtil<String, Void> task = new AsyncTaskUtil<>();

    private static volatile Map<String, Bitmap> imageCache = new LinkedHashMap<>();
    private static volatile List<String> urls = new ArrayList<>();

    private void entryRemoved() {
        while (maxCacheSize < currentCacheSize) {
            if (imageCache.isEmpty()) {
                return;
            }
            Bitmap removedImage = imageCache.remove(urls.get(0));
            currentCacheSize -= removedImage.getByteCount() * 1024;
            removedImage.recycle();
            urls.remove(0);
            Log.v(getClass().toString(), "remove");
        }
        Log.v(getClass().toString(), "currentCacheSize: " + (currentCacheSize / 1024.0) + "KB / " + (maxCacheSize / 1024 / 1024.0) + "MB");
    }


    public void loadImage(String url, OnLoadImageCompletedListener listener, OnDownLoadStartListener startListener) {
        Bitmap image;
        image = imageCache.get(url);

        if (image != null && !image.isRecycled()) {
            listener.onLoadImageCompleted(image.copy(image.getConfig(), true));
            return;
        }
        startListener.onDownLoadStart();
        if (task.getPreviousRunAsync() != null) {
            
        }
        task.setBackgroundRunnable(url, url2 -> {

            InputStream inputStream = null;
            try {
                HttpGet httpGet = new HttpGet();
                httpGet.setURI(URI.create(url));
                HttpResponse response = new DefaultHttpClient().execute(httpGet);
                if (response.getStatusLine().getStatusCode() < 400) {
                    Bitmap image1;
                    inputStream = response.getEntity().getContent();
                    image1 = BitmapFactory.decodeStream(inputStream);
                    imageCache.put(url, image1);
                    urls.add(url);
                    currentCacheSize += image1.getByteCount() / 1024;
                    entryRemoved();
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        });
        task.setOnPostUiThreadRunnable(none -> {
            Bitmap image2 = imageCache.get(url);
            if (image2 != null) {
                listener.onLoadImageCompleted(image2.copy(image2.getConfig(), true));
            }
        });
        task.execute();
    }
}
