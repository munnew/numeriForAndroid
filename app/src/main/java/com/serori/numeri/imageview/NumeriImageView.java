package com.serori.numeri.imageview;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.imageview.cache.ImageCache;
import com.serori.numeri.util.toast.ToastSender;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * ImageView
 */
public class NumeriImageView extends ImageView {
    private OnLoadCompletedListener onLoadCompletedListener;
    private ImageCache.ImageData imageData = null;
    private String imageName = "";
    private String imageExtension = "";
    private String imageKey = "";

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
     * 画像のロードが終了した際のリスナをセットする
     *
     * @param listener 画像のロードが終了した際のリスナ
     */
    public void setOnLoadCompletedListener(OnLoadCompletedListener listener) {
        onLoadCompletedListener = listener;
    }

    /**
     * 画像のロードをスタートする
     *
     * @param type その画像の種類
     * @param url  ロードしたい画像のurl
     */
    public void startLoadImage(ProgressType type, String url) {
        ImageCache imageCache = new ImageCache();
        imageName = Uri.parse(url).getLastPathSegment();
        imageKey = url;
        imageExtension = "";
        char[] charArray = url.toCharArray();
        for (int length = charArray.length - 1; length >= 0; length--) {
            if (String.valueOf(charArray[length]).equals(".")) {
                break;
            }
            imageExtension = String.valueOf(charArray[length]) + imageExtension;
        }

        imageCache.setOnStartDownLoadListener(key -> {
            if (key.equals(imageKey)) {
                switch (type) {
                    case LOAD_ICON:
                        setImageDrawable(null);
                        break;
                    case LOAD_MEDIA:
                        setImageDrawable(null);
                        break;
                }
            }
        }).loadImage(url, (imageData, key) -> {
            if ((imageData != null && !imageData.getImage().isRecycled()) && imageKey.equals(key) && !imageData.equals(this.imageData)) {
                ImageCache.ImageData previousImageData = this.imageData;
                if (previousImageData != null) previousImageData.setQuantity(false);
                this.imageData = imageData;
                imageData.setQuantity(true);
                this.setImageBitmap(this.imageData.getImage());
                if (onLoadCompletedListener != null) {
                    onLoadCompletedListener.onLoadCompleted(imageData.getImage());
                }
            }
        });
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
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setMessage("画像を保存しますか？")
                        .setPositiveButton("はい", (dialog, which) -> {
                            boolean success = saveImage();
                            if (success) {
                                ToastSender.sendToast(imageName + "を保存しました");
                            } else {
                                ToastSender.sendToast("保存に失敗しました");
                            }
                        })
                        .setNegativeButton("いいえ", (dialog, which) -> {
                        })
                        .create();

                activity.setCurrentShowDialog(alertDialog);

                return true;
            });
        } else {
            setOnLongClickListener(v -> false);
        }
    }

    private boolean saveImage() {
        FileOutputStream outputStream;
        try {
            File file = new File(Environment.getExternalStorageDirectory()
                    .getPath() + "/numeri/");
            boolean existence = false;
            if (file.exists() || file.mkdir()) {
                existence = true;
            }
            if (existence) {
                //ToDO ダサい
                outputStream = new FileOutputStream(file.getAbsolutePath() + "/" + imageName);
                if (imageExtension.equals("png") || imageExtension.equals("PNG")) {
                    imageData.getImage().compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                } else if (imageExtension.equals("jpg") || imageExtension.equals("JPG") || imageExtension.equals("jpeg") || imageExtension.equals("JPEG")) {
                    imageData.getImage().compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                }
                outputStream.flush();
                outputStream.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (imageData != null) imageData.setQuantity(false);
        super.onDetachedFromWindow();
    }

    public static enum ProgressType {
        LOAD_ICON,
        LOAD_MEDIA;

        private ProgressType() {

        }
    }
}
