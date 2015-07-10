package com.serori.numeri.activity;

import android.view.KeyEvent;

import com.serori.numeri.main.Global;
import com.serori.numeri.main.MainActivity;

/**
 */
public class SubsidiaryActivity extends NumeriActivity {
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (Global.getInstance().isActiveMainActivity()) {
                startActivity(MainActivity.class, true);
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
