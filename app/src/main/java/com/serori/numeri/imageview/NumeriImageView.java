package com.serori.numeri.imageview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.serori.numeri.R;
import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.fragment.NumeriFragment;
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
    private Bitmap image = null;
    private String imageName = "";
    private String imageType = "";

    public NumeriImageView(Context context) {
        super(context);
    }

    public NumeriImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumeriImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnLoadCompletedListener(OnLoadCompletedListener listener) {
        onLoadCompletedListener = listener;
    }

    public void startLoadImage(ProgressType type, String url) {
        ImageCache imageCache = new ImageCache();
        imageName = Uri.parse(url).getLastPathSegment();
        imageType = "";
        char[] charArray = url.toCharArray();
        for (int length = charArray.length - 1; length >= 0; length--) {
            if (String.valueOf(charArray[length]).equals(".")) {
                break;
            }
            imageType = String.valueOf(charArray[length]) + imageType;
        }
        imageCache.loadImage(url, image -> {
                    Log.v(getClass().toString(), "loadComplete");
                    if (image == null || image.isRecycled()) {
                        this.startLoadImage(type, url);
                        return;
                    }
                    this.image = image;
                    this.setImageBitmap(this.image);
                    if (onLoadCompletedListener != null) {
                        onLoadCompletedListener.onLoadCompleted(image);
                    }
                },
                () -> {
                    Log.v(getClass().toString(), "startDownload");
                    switch (type) {
                        case LOAD_ICON:
                            setImageDrawable(getContext().getResources().getDrawable(R.drawable.loading));
                            break;
                        case LOAD_MEDIA:
                            setImageDrawable(null);
                            break;
                    }
                });
    }

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
                outputStream = new FileOutputStream(file.getAbsolutePath() + "/" + imageName);
                if (imageType.equals("png") || imageType.equals("PNG")) {
                    image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                } else if (imageType.equals("jpg") || imageType.equals("JPG") || imageType.equals("jpeg") || imageType.equals("JPEG")) {
                    image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
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

    public static enum ProgressType {
        LOAD_ICON,
        LOAD_MEDIA;

        private ProgressType() {

        }
    }
}
