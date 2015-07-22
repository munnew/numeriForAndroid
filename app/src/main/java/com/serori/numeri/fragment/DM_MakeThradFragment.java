package com.serori.numeri.fragment;

import android.os.Handler;

import com.serori.numeri.main.manager.FragmentStorager;

/**
 * Created by Lynx on 2015/07/22.
 */
public class DM_MakeThradFragment extends NumeriFragment {
    public DM_MakeThradFragment() {
    }

    @Override
    public void setFragmentName(String name) {
        super.name = name + " : " + FragmentStorager.FragmentType.LIST.getId();
    }


    @Override
    protected void initializeLoad() {
        Handler handler = new Handler();
        new Thread(() -> {

        }).start();
    }

    @Override
    protected void onAttachedBottom() {
        Handler handler = new Handler();
        new Thread(() -> {

        }).start();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
