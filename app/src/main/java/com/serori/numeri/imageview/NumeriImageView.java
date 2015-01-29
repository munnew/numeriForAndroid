package com.serori.numeri.imageview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.serori.numeri.main.Application;
import com.serori.numeri.imageview.cache.IconCache;
import com.serori.numeri.imageview.cache.OnLoadImageCompletedListener;

/**
 * ImageView
 */
public class NumeriImageView extends ImageView implements OnLoadImageCompletedListener {
    private TextView progressText = null;
    private OnLoadCompletedListener onLoadCompletedListener;

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

    public void startLoadImage(TextView progressText, String url) {
        IconCache iconCache = new IconCache();
        iconCache.setOnLoadImageCompletedListener(this);
        this.progressText = progressText;
        iconCache.loadImage(url);
    }


    @Override
    public void onLoadImageCompleted(Bitmap image) {
        ((Activity) Application.getInstance().getMainActivityContext()).runOnUiThread(() -> {
            this.setImageBitmap(image);
            if (progressText != null) {
                progressText.setVisibility(GONE);
            }
            this.setVisibility(VISIBLE);
            if (onLoadCompletedListener != null) {
                onLoadCompletedListener.onLoadCompleted(image);
            }
        });
    }
}
