package com.serori.numeri.temp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.serori.numeri.R;
import com.serori.numeri.config.ConfigurationStorager;

import java.util.ArrayList;
import java.util.List;

/**
 * Activityが継承すべきクラス
 */
public class NumeriActivity extends DialogActivity {
    private List<OnFinishListener> onFinishListeners = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        if (ConfigurationStorager.EitherConfigurations.DARK_THEME.isEnabled()) {
            setTheme(R.style.AppTheme_Dark);
        }
        if (ConfigurationStorager.EitherConfigurations.SLEEPLESS.isEnabled()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        super.onCreate(savedInstanceState);
    }


    public void addOnFinishListener(OnFinishListener listener) {
        onFinishListeners.add(listener);
    }

    @Override
    protected void onDestroy() {
        if (ConfigurationStorager.EitherConfigurations.SLEEPLESS.isEnabled()) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        super.onDestroy();
    }

    @Override
    public void finish() {
        for (OnFinishListener onFinishListener : onFinishListeners) {
            onFinishListener.onFinish();
        }
        super.finish();
    }

    /**
     * 指定したActivityに遷移します
     *
     * @param activityClass 遷移するActivityのクラス
     * @param finish        現在のActivityを終了するか否か<br>true:終了する<br>false:終了しない
     */
    public void startActivity(Class activityClass, boolean finish) {
        Intent intent = new Intent(this, activityClass);
        super.startActivity(intent);
        if (finish) {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                moveTaskToBack(true);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
