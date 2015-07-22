package com.serori.numeri.fragment;

import android.os.Handler;

import com.serori.numeri.main.manager.FragmentStorager;

/**
 * Created by Lynx on 2015/07/22.
 */
public class DM_TextlistFragment extends NumeriFragment {
    //TODO should attach width of dm_text
    public DM_TextlistFragment() {
    }

    @Override
    public void setFragmentName(String name) {
        super.name = name + " : " + FragmentStorager.FragmentType.FillInMessage.getId();
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
