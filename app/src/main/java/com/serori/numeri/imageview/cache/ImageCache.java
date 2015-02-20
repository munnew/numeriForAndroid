package com.serori.numeri.imageview.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.serori.numeri.util.async.SimpleAsyncTask;

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

/**
 * 画像をキャッシュするクラス
 * 生成されたインスタンスを使いまわすことは出来ません
 */
public class ImageCache {
    private static final int maxCacheSize = 1 * 1024 * 1024 / 10;
    private static int currentCacheSize = 0;
    private OnStartDownLoadListener onStartDownLoadListener = null;
    private static volatile Map<String, ImageData> imageCache = new LinkedHashMap<>();
    private static final List<String> urls = new ArrayList<>();
    private static volatile Map<String, List<OnLoadImageCompletedListener>> onLoadImageCompletedListeners = new LinkedHashMap<>();
    private boolean loadImageAlreadyCalled = false;

    private void entryRemoved() {
        int index = 0;
        while (maxCacheSize < currentCacheSize) {
            if (imageCache.isEmpty() && urls.size() <= index) {
                return;
            }
            ImageData removeImage = imageCache.get(urls.get(index));
            if (removeImage.delete()) {
                currentCacheSize -= removeImage.getImage().getByteCount() / 1024;
                imageCache.remove(urls.get(index));
                urls.remove(index);
            } else {
                index++;
            }
            Log.v(getClass().toString(), "remove");
        }
        Log.v(getClass().toString(), "onDownload-currentCacheSize: " + (currentCacheSize / 1024.0) + "KB / " + (maxCacheSize / 1024 / 1024.0) + "MB");
    }

    /**
     * ダウンロードを開始した際に発生するイベントのリスナをセットします。<br>
     * セットする場合はloadImage()を呼ぶ前にセットする必要があります。
     *
     * @param listener OnDownLoadStartListener
     * @return 自身のインスタンス
     */
    public ImageCache setOnStartDownLoadListener(OnStartDownLoadListener listener) {
        if (loadImageAlreadyCalled) {
            throw new IllegalStateException("loadImageが呼び出された後に呼び出されました。");
        }
        onStartDownLoadListener = listener;
        return this;
    }


    /**
     * 画像をダウンロードドする<br>
     * ダウンロード済みの画像が場合はそれをロードする
     * おなじインスタンスから２度呼ぶことは出来ません
     *
     * @param url                 ロードしたい画像のurl
     * @param onCompletedListener 画像のロードが終了した際のリスナ
     */
    public void loadImage(String url, OnLoadImageCompletedListener onCompletedListener) {
        if (loadImageAlreadyCalled) {
            throw new IllegalStateException("loadImageが二度呼ばれました");
        }
        loadImageAlreadyCalled = true;
        ImageData image;
        image = imageCache.get(url);
        if (image != null && !image.getImage().isRecycled()) {
            onCompletedListener.onLoadImageCompleted(image, url);
            return;
        }

        boolean startedDownload = false;
        for (String s : urls) {
            if (s.equals(url)) {
                startedDownload = true;
                if (imageCache.get(url) == null) {
                    if (onStartDownLoadListener != null)
                        onStartDownLoadListener.onDownLoadStart(url);
                    List<OnLoadImageCompletedListener> listeners = new ArrayList<>();
                    listeners.add(onCompletedListener);
                    List<OnLoadImageCompletedListener> previousListeners = onLoadImageCompletedListeners.put(url, listeners);
                    if (previousListeners != null) {
                        onLoadImageCompletedListeners.get(url).addAll(previousListeners);
                    }
                }
                break;
            }
        }

        if (!startedDownload) {
            if (onStartDownLoadListener != null) onStartDownLoadListener.onDownLoadStart(url);
            urls.add(url);
            new SimpleAsyncTask<String, ImageData>() {
                @Override
                protected ImageData doInBackground(String s) {
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
                                ImageData imageData = new ImageData(image);
                                imageCache.put(url, imageData);
                                currentCacheSize += image.getByteCount() / 1024;
                                entryRemoved();
                                return imageData;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(ImageData imageData) {
                    if (imageData != null) {
                        onCompletedListener.onLoadImageCompleted(imageData, url);
                        List<OnLoadImageCompletedListener> listeners = onLoadImageCompletedListeners.get(url);
                        if (listeners != null) {
                            for (OnLoadImageCompletedListener onLoadImageCompletedListener : listeners) {
                                onLoadImageCompletedListener.onLoadImageCompleted(imageData, url);
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

    /**
     * キャッシュされた画像を表すクラス
     * 必ずsetQuantityを使用
     */
    public class ImageData {
        //TODO かなりダサい
        private Bitmap image;
        private int usedQuantity = 0;

        ImageData(Bitmap image) {
            this.image = image;
        }

        /**
         * Bitmapが使われていない場合それを開放します
         *
         * @return 開放したか否か true : 開放した false : 開放出来なかった
         */
        boolean delete() {
            if (usedQuantity == 0) {
                image.recycle();
                return true;
            }
            return false;
        }


        /**
         * Bitmapを取得します
         *
         * @return Bitmap
         */
        public Bitmap getImage() {
            return image;
        }

        /**
         * このデータをViewが使用しているか否かを設定します。<br>
         * 使用している場合はtrueを、しなくなった場合はfalseを必ずセットしてください<b>
         * 不適切に使用された場合はIllegalStateExceptionを投げます。
         *
         * @param practicableDelete true : 使われている false : 使われなくなった。
         */
        public void setQuantity(boolean practicableDelete) {
            if (practicableDelete) {
                usedQuantity++;
            } else {
                usedQuantity--;
                if (usedQuantity < 0) {
                    throw new IllegalStateException("一度もtrueを与えられずにfalseを与えられました。");
                }
            }
            // Log.v(getClass().toString(), image.toString() + " usedQuantity = " + usedQuantity);
        }
    }
}
