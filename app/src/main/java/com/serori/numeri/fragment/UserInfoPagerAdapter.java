package com.serori.numeri.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
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
        if ((fragment instanceof UserListFragment) || ((fragment instanceof NumeriFragment))) {
            fragments.add(fragment);
        } else {
            throw new IllegalArgumentException("fragmentがNumeriFragmentかUserListFragmentのどちらかを継承している必要があります");
        }

    }

    public void addAll(List<Fragment> fragments) {
        for (Fragment fragment : fragments) {
            add(fragment);
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "fragment";
    }


}
