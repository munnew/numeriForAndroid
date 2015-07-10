package com.serori.numeri.temp.imageview;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.serori.numeri.temp.activity.NumeriActivity;
import com.serori.numeri.temp.imageview.cache.ImageDownloader;
import com.serori.numeri.util.toast.ToastSender;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * ImageView
 */
public class NumeriImageView extends ImageView {
    protected static final String PNG_EXTENSION = "[pP][nN][gG]";
    protected static final String JPEG_EXTENSION = "[jJ][pP][eE]?[gG]";
    private static final String URL = "^(https?|ftp)(://[-_.!~*'()a-zA-Z0-9;/?:@&=+\\$,%#]+)$";
    private OnLoadCompletedListener onLoadCompletedListener;
    private ImageDownloader.ImageData imageData = null;
    private String imageName = "";
    private String imageExtension = "";
    private String imageUrl = "";
    private boolean imageReleased = false;
    private static String savePath = "numeri";

    public NumeriImageView(Context context) {
        super(context);
    }

    public NumeriImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumeriImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 画像を保存するパスをセットします。
     * デフォルトでは"numeri"です。
     *
     * @param savePath ExternalStorageDirectoryをルートとした相対パス
     */
    public static void setSavePath(String savePath) {
        NumeriImageView.savePath = savePath;
    }

    @Override
    protected void onAttachedToWindow() {
        Context context = getContext();
        if (context instanceof NumeriActivity)
            ((NumeriActivity) context).addOnFinishListener(this::releaseImage);
        super.onAttachedToWindow();
    }

    /**
     * 画像のロードが終了した際のリスナをセットする
     *
     * @param listener 画像のロードが終了した際のリスナ
     */
    public final void setOnLoadCompletedListener(OnLoadCompletedListener listener) {
        onLoadCompletedListener = listener;
    }

    /**
     * 画像をurlからセットします。
     * <br>キャッシュはされません。
     *
     * @param url セットする画像のurl
     */
    public final void setImage(String url) {
        setImage(false, ProgressType.NONE, url);
    }

    /**
     * @param cache falseを指定するとキャッシュされません、またキャッシュから読み込むこともありません
     * @param type  ProgressType
     * @param url   ロードする画像のurl
     */
    public final void setImage(boolean cache, ProgressType type, String url) {
        if (url == null || url.equals("")) {
            imageUrl = "";
            setSaveImageFunctionEnabled(false, null);
            setImageDrawable(null);
            imageExtension = "";
            imageName = "";
            if (imageData != null) imageData.setQuantity(false);
            imageData = null;
            return;
        }
        if (!judgeUrl(url)) throw new IllegalArgumentException("urlでない文字列が渡されました。");
        ImageDownloader imageDownloader = new ImageDownloader();
        imageName = Uri.parse(url).getLastPathSegment();
        imageUrl = url;
        imageExtension = "";
        char[] charArray = url.toCharArray();
        for (int length = charArray.length - 1; length >= 0; length--) {
            if (String.valueOf(charArray[length]).equals(".")) {
                break;
            }
            imageExtension = String.valueOf(charArray[length]) + imageExtension;
        }

        imageDownloader.setOnStartDownloadListener(key -> {
            if (key.equals(imageUrl)) {
                switch (type) {
                    case LOAD_ICON:
                        setImageDrawable(null);
                        break;
                    case LOAD_MEDIA:
                        setImageDrawable(null);
                        break;
                    case NONE:
                        break;
                }
            }
        }).loadImage(cache, url, (imageData, key) -> {
            if ((imageData != null && imageData.getImage() != null && !imageData.getImage().isRecycled()) && imageUrl.equals(key)) {
                if (this.imageData != null) this.imageData.setQuantity(false);
                this.imageData = imageData;
                imageData.setQuantity(true);
                this.setImageBitmap(imageData.getImage());
                if (onLoadCompletedListener != null) {
                    onLoadCompletedListener.onLoadCompleted(imageData.getImage());
                }
            }
        });
    }

    private boolean judgeUrl(String s) {
        return s.matches(URL);
    }


    /**
     * 長押しでの画像の保存の機能が有効か否かを切り替える
     * 必ず保存するか否かのDialogを表示する
     *
     * @param enabled  true : 有効 false : 無効
     * @param activity Activity Dialogを表示するActivity
     */
    public void setSaveImageFunctionEnabled(boolean enabled, NumeriActivity activity) {
        if (enabled) {
            setOnLongClickListener(v -> {
                if (imageData != null) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setMessage("画像を保存しますか？")
                            .setPositiveButton("はい", (dialog, which) -> {
                                new Thread(() -> {
                                    if (executeSaveImage()) {
                                        ToastSender.sendToast(imageName + "を保存しました");
                                    } else {
                                        ToastSender.sendToast("保存に失敗しました");
                                    }
                                }).start();
                            })
                            .setNegativeButton("キャンセル", (dialog, which) -> {
                            })
                            .create();
                    activity.setCurrentShowDialog(alertDialog);
                    return true;
                }
                return false;
            });
        } else {
            setOnLongClickListener(v -> false);
        }
    }

    /**
     * 画像の保存を実行
     *
     * @return 成功したか否か true : 成功 , false : 失敗
     */
    private boolean executeSaveImage() {
        boolean success = false;
        FileOutputStream outputStream = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory().getPath()
                    + "/" + savePath);
            if ((file.exists() || file.mkdir()) && getImageData() != null) {
                //ToDO ダサい
                outputStream = new FileOutputStream(file.getAbsolutePath() + "/" + imageName);
                saveImage(outputStream);
                success = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }


    /**
     * @param outputStream 画像を保存するファイルのFileOutputStream
     */
    protected void saveImage(FileOutputStream outputStream) {
        if (getImageExtension().matches(PNG_EXTENSION)) {
            getImageData().getImage().compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } else if (getImageExtension().matches(JPEG_EXTENSION)) {
            getImageData().getImage().compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        }
    }

    /**
     * ImageDataを取得
     *
     * @return 持っているImageData<br> nullを返す可能性があります。
     */
    protected final ImageDownloader.ImageData getImageData() {
        return imageData;
    }

    /**
     * 表示されている画像の拡張子を取得
     *
     * @return 拡張子
     */
    protected final String getImageExtension() {
        return imageExtension;
    }


    @Override
    protected void onDetachedFromWindow() {
        releaseImage();
        super.onDetachedFromWindow();
    }

    private void releaseImage() {
        if (imageData != null && !imageReleased) {
            imageData.setQuantity(false);
            imageData.recycle();
            imageReleased = true;
        }
    }


    public enum ProgressType {
        LOAD_ICON,
        LOAD_MEDIA,
        NONE;

        ProgressType() {

        }
    }
}
