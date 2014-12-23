package com.serori.numeri.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seroriKETC on 2014/12/20.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private  List<Fragment> fragments = new ArrayList<>();

    public SectionsPagerAdapter(FragmentManager manager) {
        super(manager);
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
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ((NumeriFragment) fragments.get(position)).getFragmentName();
    }

    public void add(Fragment fragment) {
        if (!(fragment instanceof NumeriFragment)) {
            throw new IllegalArgumentException("追加されるフラグメントは\"NumeriFragmentParameter\"を" +
                    "継承していなければなりません");
        }
        fragments.add(fragment);
    }

}