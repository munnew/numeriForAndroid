package com.serori.numeri.temp.imageview.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

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
 * 表示されている画像がmaxCacheSizeを超えた場合も表示している分はキャッシュに保持されます。
 */
public final class ImageDownloader {
    private static final int maxCacheSize = 15 * 1024 * 1024;
    private static int currentCacheSize = 0;
    private OnStartDownloadListener onStartDownloadListener = null;
    private static volatile Map<String, ImageData> imageCache = new LinkedHashMap<>();
    private static volatile List<String> urls = new ArrayList<>();
    private static volatile Map<String, List<OnLoadImageCompletedListener>> onLoadImageCompletedListenerMap = new LinkedHashMap<>();
    private boolean loadImageAlreadyCalled = false;


    private void entryRemoved() {
        int index = 0;
        while (maxCacheSize < currentCacheSize) {
            if (imageCache.isEmpty() || urls.size() <= index) {
                break;
            }

            ImageData removeImage = imageCache.get(urls.get(index));

            if (removeImage != null && removeImage.delete()) {
                currentCacheSize -= removeImage.getByteCount();
                imageCache.remove(urls.get(index));
                urls.remove(index);
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
        List<String> urls = new ArrayList<>();
        urls.addAll(ImageDownloader.urls);
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
            Handler handler = new Handler();
            new Thread(() -> {
                ImageDownloader.urls.add(url);
                Bitmap image = downloadImage(url);
                if (image != null) {
                    ImageData imageData1 = new ImageData(image, url);
                    imageCache.put(url, imageData1);
                    currentCacheSize += image.getByteCount();
                    handler.post(() -> {
                        onCompletedListener.onLoadImageCompleted(imageData1, url);
                        List<OnLoadImageCompletedListener> listeners = onLoadImageCompletedListenerMap.get(url);
                        entryRemoved();
                        if (listeners != null) {
                            for (OnLoadImageCompletedListener onLoadImageCompletedListener : listeners) {
                                onLoadImageCompletedListener.onLoadImageCompleted(imageData1, url);
                            }
                            onLoadImageCompletedListenerMap.remove(url);
                        }

                    });
                } else {
                    ImageDownloader.urls.remove(url);
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
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
                image = BitmapFactory.decodeStream(inputStream);
            } else if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                String redirectUrl = httpURLConnection.getHeaderField("Location");
                if (redirectUrl != null) {
                    return downloadImage(redirectUrl);
                }
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
    public final class ImageData {
        //TODO ダサい
        private Bitmap image;
        private int usedQuantity = 0;
        private String key = "";
        private final int byteCount;

        ImageData(Bitmap image, String key) {
            if (image == null) {
                throw new NullPointerException("nullが渡されました");
            }
            this.image = image;
            this.key = key;
            byteCount = image.getByteCount();
        }

        /**
         * Bitmapが使われていない場合それを開放します
         *
         * @return true : 開放した false : 開放出来なかった
         */
        boolean delete() {
            if (usedQuantity == 0 && image != null) {
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

        public int getByteCount() {
            return byteCount;
        }
    }
}
