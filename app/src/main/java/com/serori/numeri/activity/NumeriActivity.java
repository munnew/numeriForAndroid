package com.serori.numeri.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.serori.numeri.R;
import com.serori.numeri.config.ConfigurationStorager;

import java.util.ArrayList;
import java.util.List;

/**
 * Activityが継承すべきクラス
 */
public class NumeriActivity extends AppCompatActivity {
    private AlertDialog currentShowDialog = null;
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


    @Override
    protected void onStop() {
        if (currentShowDialog != null) {
            if (currentShowDialog.isShowing()) {
                currentShowDialog.hide();
            } else {
                currentShowDialog = null;
            }
        }
        super.onStop();
    }

    @Override
    protected void onRestart() {
        if (currentShowDialog != null) {
            currentShowDialog.show();
        }
        super.onRestart();
    }

    /**
     * Dialogをセットすると同時に表示します<br>
     * このメソッドを使用してDialogを表示した場合、そのDialogのライフサイクルはActivityのライフサイクルに準じます
     *
     * @param dialog セットするダイアログ
     */
    public void setCurrentShowDialog(AlertDialog dialog) {
        runOnUiThread(dialog::show);
        currentShowDialog = dialog;
    }

    @Override
    protected void onDestroy() {
        if (currentShowDialog != null) {
            currentShowDialog.dismiss();
        }
        if (ConfigurationStorager.EitherConfigurations.SLEEPLESS.isEnabled()) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        currentShowDialog = null;
        super.onDestroy();
    }

    public void addOnFinishListener(OnFinishListener listener) {
        onFinishListeners.add(listener);
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
}
