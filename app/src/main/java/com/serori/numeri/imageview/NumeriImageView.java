package com.serori.numeri.imageview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.serori.numeri.application.Application;
import com.serori.numeri.util.cache.IconCache;
import com.serori.numeri.util.cache.OnLoadImageCompletedListener;

/**
 * ImageView
 */
public class NumeriImageView extends ImageView implements OnLoadImageCompletedListener {
    private IconCache iconCache = new IconCache();
    private ProgressBar progressBar;
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

    public void startLoadImage(ProgressBar progressBar, String url) {
        iconCache.setOnLoadImageCompletedListener(this);
        this.progressBar = progressBar;

        iconCache.loadImage(url);
    }


    @Override
    public void onLoadImageCompleted(Bitmap image) {
        ((Activity) Application.getInstance().getMainActivityContext()).runOnUiThread(() -> {
            this.setImageBitmap(image);
            if (progressBar != null) {
                progressBar.setVisibility(GONE);
            }
            this.setVisibility(VISIBLE);
            if (onLoadCompletedListener != null) {
                onLoadCompletedListener.onLoadCompleted(image);
            }
        });
    }
}
