package com.serori.numeri.userprofile;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by serioriKETC on 2015/02/01.
 */
public class UserInfoPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> fragments = new ArrayList<>();

    public UserInfoPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }


    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    public void add(Fragment fragment) {
        fragments.add(fragment);
    }

    public void addAll(List<Fragment> fragments) {
        fragments.addAll(fragments);
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}
