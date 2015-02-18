package com.serori.numeri.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.view.WindowManager;

import com.serori.numeri.R;
import com.serori.numeri.config.ConfigurationStorager;

/**
 * Activityが継承すべきクラス
 */
public class NumeriActivity extends ActionBarActivity {
    private AlertDialog currentShowDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ConfigurationStorager.EitherConfigurations.DARK_THEME.isEnabled()) {
            setTheme(R.style.AppTheme_Dark);
        }
        if (ConfigurationStorager.EitherConfigurations.SLEEPLESS.isEnabled()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (currentShowDialog != null) {
            if (currentShowDialog.isShowing()) {
                currentShowDialog.hide();
            }
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        if (currentShowDialog != null) {
            currentShowDialog.show();
        }
        super.onRestoreInstanceState(savedInstanceState);
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

    /**
     * 指定したActivityに遷移します
     *
     * @param activityClass 遷移するActivityのクラス
     * @param finish        現在のActivityを終了するか否か<br>true:終了する<br>false:終了しない
     */
    protected void startActivity(Class activityClass, boolean finish) {
        Intent intent = new Intent(this, activityClass);
        super.startActivity(intent);
        if (finish) {
            finish();
        }
    }
}
