package com.serori.numeri.media;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;

import com.serori.numeri.R;
import com.serori.numeri.activity.NumeriActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * MediaActivity
 */
public class MediaActivity extends NumeriActivity {
    private static List<String> mediaUris = new ArrayList<>();
    private static List<MediaFragment> mediaFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        MediaPagerAdapter mediaPagerAdapter = new MediaPagerAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.mediaPager);
        pager.setOffscreenPageLimit(10);
        if (savedInstanceState == null) {
            for (int i = 0; i < mediaUris.size(); i++) {
                MediaFragment mediaFragment = new MediaFragment();
                mediaFragment.setMediaUri(mediaUris.get(i));
                mediaPagerAdapter.add(mediaFragment);
                mediaFragments.add(mediaFragment);
            }
        } else {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                mediaPagerAdapter.add(fragment);
                mediaFragments.add((MediaFragment) fragment);
            }
        }
        pager.setAdapter(mediaPagerAdapter);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        for (int i = 0; i < mediaFragments.size(); i++) {
            if (mediaFragments.get(i).isAdded()) {
                getSupportFragmentManager().putFragment(outState, "Fragment" + i, mediaFragments.get(i));
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public static void setMediaUris(List<String> uris) {
        if (!mediaUris.isEmpty()) {
            mediaUris.clear();
        }
        mediaUris.addAll(uris);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mediaFragments.clear();
            finish();
            return true;
        }
        return false;
    }

}
