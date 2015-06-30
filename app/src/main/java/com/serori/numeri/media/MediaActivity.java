package com.serori.numeri.media;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.serori.numeri.R;
import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.activity.SubsidiaryActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * MediaActivity
 */
public class MediaActivity extends SubsidiaryActivity {
    private static List<String> mediaUris = new ArrayList<>();
    private static int location = 0;

    public static void show(Context activityContext, List<String> uris) {
        if (!mediaUris.isEmpty()) {
            mediaUris.clear();
        }
        mediaUris.addAll(uris);
        ((NumeriActivity) activityContext).startActivity(MediaActivity.class, false);
    }

    public static void show(Context context, List<String> uris, int location) {
        MediaActivity.location = location;
        show(context, uris);
    }

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
            }
        } else {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                mediaPagerAdapter.add(fragment);
            }
        }
        pager.setAdapter(mediaPagerAdapter);
        pager.setCurrentItem(location);
        location = 0;
    }

}
