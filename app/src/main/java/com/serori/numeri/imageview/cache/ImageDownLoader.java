package com.serori.numeri.imageview.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 画像をDLしたりキャッシュしたりするクラス
 * 生成されたインスタンスを使いまわすことは出来ません
 */
public class ImageDownloader {
    private static final int maxCacheSize = 8 * 1024 * 1024;
    private static int currentCacheSize = 0;
    private OnStartDownloadListener onStartDownloadListener = null;
    private static volatile Map<String, ImageData> imageCache = new LinkedHashMap<>();
    private static volatile List<String> urls = new ArrayList<>();
    private static volatile Map<String, List<OnLoadImageCompletedListener>> onLoadImageCompletedListenerMap = new LinkedHashMap<>();
    private boolean loadImageAlreadyCalled = false;


    private void entryRemoved() {
        int index = 0;
        while (maxCacheSize < currentCacheSize) {
            if (imageCache.isEmpty() && urls.size() <= index) {
                return;
            }

            ImageData removeImage = imageCache.get(urls.get(index));
            int removeImageByteSize = removeImage.getImage().getByteCount();

            if (removeImage.delete()) {
                currentCacheSize -= removeImageByteSize;
                imageCache.remove(urls.get(index));
                String url = urls.remove(index);
                Log.v("ImageDownloader", "remove : " + url);
            } else {
                index++;
            }
        }
    }

    /**
     * ダウンロードを開始した際に発生するイベントのリスナをセットします。<br>
     * セットする場合はloadImage()を呼ぶ前にセットする必要があります。
     *
     * @param listener OnDownLoadStartListener
     * @return 自身のインスタンス
     */
    public ImageDownloader setOnStartDownloadListener(OnStartDownloadListener listener) {
        if (loadImageAlreadyCalled)
            throw new IllegalStateException("loadImageが呼び出された後に呼び出されました。");
        onStartDownloadListener = listener;
        return this;
    }


    /**
     * 画像をダウンロードする<br>
     * ダウンロード済みの画像が場合はそれをロードする
     * おなじインスタンスから２度呼ぶことは出来ません
     * 既にダウンロードを開始している画像のurlを指定した場合はそのOnLoadImageCompletedListenerはキューに入り、
     * ダウンロードを完了すると順次OnLoadImageCompletedListener#onLoadImageCompleted(ImageData, String)が呼ばれます。
     *
     * @param url                 ロードしたい画像のurl
     * @param onCompletedListener 画像のロードが終了した際のリスナ
     */
    public void loadImage(String url, OnLoadImageCompletedListener onCompletedListener) {
        if (loadImageAlreadyCalled) {
            throw new IllegalStateException("loadImageが二度呼ばれました");
        }
        loadImageAlreadyCalled = true;
        ImageData imageData = imageCache.get(url);

        if (imageData != null && !imageData.getImage().isRecycled()) {
            onCompletedListener.onLoadImageCompleted(imageData, url);
            return;
        }

        boolean startedDownload = false;
        if (onStartDownloadListener != null)
            onStartDownloadListener.onDownLoadStart(url);
        for (String s : urls) {
            if (s.equals(url)) {
                startedDownload = true;
                if (imageCache.get(url) == null) {
                    List<OnLoadImageCompletedListener> listeners = new ArrayList<>();
                    listeners.add(onCompletedListener);
                    List<OnLoadImageCompletedListener> previousListeners = onLoadImageCompletedListenerMap.put(url, listeners);
                    if (previousListeners != null) {
                        onLoadImageCompletedListenerMap.get(url).addAll(previousListeners);
                    }
                }
                break;
            }
        }

        if (!startedDownload) {
            urls.add(url);
            Handler handler = new Handler();
            new Thread(() -> {
                Bitmap image = downloadImage(url);
                if (image != null) {
                    ImageData imageData1 = new ImageData(image, url);
                    imageCache.put(url, imageData1);
                    currentCacheSize += image.getByteCount();
                    entryRemoved();
                    handler.post(() -> {
                        onCompletedListener.onLoadImageCompleted(imageData1, url);
                        List<OnLoadImageCompletedListener> listeners = onLoadImageCompletedListenerMap.get(url);
                        if (listeners != null) {
                            for (OnLoadImageCompletedListener onLoadImageCompletedListener : listeners) {
                                onLoadImageCompletedListener.onLoadImageCompleted(imageData1, url);
                            }
                            onLoadImageCompletedListenerMap.remove(url);
                        }
                    });
                } else {
                    urls.remove(url);
                }
            }).start();
        }
    }

    /**
     * /**
     * 画像をダウンロードする<br>
     * おなじインスタンスから２度呼ぶことは出来ません
     *
     * @param cache               キャッシュするか否か true:キャシュする false : キャッシュしない<br>
     *                            falseを指定した場合は既にダウンロードを開始した画像のurlを指定しても
     *                            複数のonCompletedListenerはキューに入れられることはなく、
     *                            またキャッシュされた画像から読み込むこともありません。
     * @param url                 ダウンロードする画像のurl
     * @param onCompletedListener OnLoadImageCompletedListener
     */
    public void loadImage(boolean cache, String url, OnLoadImageCompletedListener onCompletedListener) {
        if (cache) {
            loadImage(url, onCompletedListener);
        } else {
            if (loadImageAlreadyCalled) {
                throw new IllegalStateException("loadImageが二度呼ばれました");
            }
            loadImageAlreadyCalled = true;
            Handler handler = new Handler();
            new Thread(() -> {
                Bitmap image = downloadImage(url);
                if (image != null) {
                    ImageData imageData = new ImageData(image, url);
                    handler.post(() -> onCompletedListener.onLoadImageCompleted(imageData, url));
                }
            }).start();
        }
    }

    private Bitmap downloadImage(String urlString) {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        Bitmap image = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setAllowUserInteraction(false);
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
                image = BitmapFactory.decodeStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (httpURLConnection != null) httpURLConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return image;
    }

    /**
     * 画像を表すクラス
     */
    public class ImageData {
        //TODO かなりダサい
        private Bitmap image;
        private int usedQuantity = 0;
        private String key = "";

        ImageData(Bitmap image, String key) {
            this.image = image;
            this.key = key;
        }

        /**
         * Bitmapが使われていない場合それを開放します
         *
         * @return true : 開放した false : 開放出来なかった
         */
        boolean delete() {
            if (usedQuantity == 0) {
                image.recycle();
                image = null;
                return true;
            }
            return false;
        }

        /**
         * Bitmapがキャッシュされていない場合それを開放します
         *
         * @return true : 開放した false : 開放出来なかった
         */
        public boolean recycle() {
            if (imageCache.get(key) == null && image != null) {
                Log.v(toString(), "recycle");
                image.recycle();
                image = null;
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
         *
         * @param practicableDelete true : 使われている false : 使われなくなった。
         */
        public void setQuantity(boolean practicableDelete) {
            if (practicableDelete) {
                usedQuantity++;
            } else {
                usedQuantity--;
                if (usedQuantity < 0) {
                    usedQuantity = 0;
                }
            }
        }

    }
}
