package com.serori.numeri.imageview.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.serori.numeri.util.SimpleAsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ImageCache {
    private static final int maxCacheSize = 1 * 1024 * 1024 / 10;
    private static int currentCacheSize = 0;

    private static volatile Map<String, Bitmap> imageCache = new LinkedHashMap<>();
    private static final List<String> urls = new ArrayList<>();
    private static volatile Map<String, List<OnLoadImageCompletedListener>> onLoadImageCompletedListeners = new LinkedHashMap<>();

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
        Log.v(getClass().toString(), "onDownload-currentCacheSize: " + (currentCacheSize / 1024.0) + "KB / " + (maxCacheSize / 1024 / 1024.0) + "MB");
    }


    public void loadImage(String url, OnLoadImageCompletedListener listener, OnDownLoadStartListener startListener) {
        Bitmap image;
        image = imageCache.get(url);
        if (image != null && !image.isRecycled()) {
            listener.onLoadImageCompleted(image, url);
            return;
        }

        boolean startedDownload = false;
        for (String s : urls) {
            if (s.equals(url)) {
                startedDownload = true;
                if (imageCache.get(url) == null) {
                    startListener.onDownLoadStart();
                    List<OnLoadImageCompletedListener> listeners = new ArrayList<>();
                    listeners.add(listener);
                    List<OnLoadImageCompletedListener> previousListeners = onLoadImageCompletedListeners.put(url, listeners);
                    if (previousListeners != null) {
                        onLoadImageCompletedListeners.get(url).addAll(previousListeners);
                    }
                }
            }
        }

        if (!startedDownload) {
            startListener.onDownLoadStart();
            urls.add(url);
            new SimpleAsyncTask<String, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String s) {
                    try {
                        HttpGet httpGet = new HttpGet();
                        httpGet.setURI(URI.create(url));
                        HttpResponse response = new DefaultHttpClient().execute(httpGet);
                        if (response.getStatusLine().getStatusCode() < 400) {
                            Bitmap image;
                            InputStream inputStream = response.getEntity().getContent();
                            image = BitmapFactory.decodeStream(inputStream);
                            inputStream.close();
                            if (image != null) {
                                imageCache.put(url, image);
                                currentCacheSize += image.getByteCount() / 1024;
                                entryRemoved();
                                return image;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Bitmap image) {
                    if (image != null) {
                        listener.onLoadImageCompleted(image, url);
                        List<OnLoadImageCompletedListener> listeners = onLoadImageCompletedListeners.get(url);
                        if (listeners != null) {
                            for (OnLoadImageCompletedListener onLoadImageCompletedListener : listeners) {
                                onLoadImageCompletedListener.onLoadImageCompleted(image, url);
                            }
                            onLoadImageCompletedListeners.remove(url);
                        }
                    } else {
                        urls.remove(url);
                    }
                }
            }.execute(url);
        }

    }
}
