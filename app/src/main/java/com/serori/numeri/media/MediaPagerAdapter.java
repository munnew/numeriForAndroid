package com.serori.numeri.media;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.serori.numeri.fragment.NumeriFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * MediaPagerAdapter
 */
public class MediaPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments = new ArrayList<>();

    public MediaPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "image" + position;
    }

    public void add(Fragment fragment) {
        fragments.add(fragment);
    }
}
