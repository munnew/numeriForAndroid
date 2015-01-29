package com.serori.numeri.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;

import com.serori.numeri.R;
import com.serori.numeri.config.ConfigurationStorager;

/**
 * Created by seroriKETC on 2015/01/24.
 */
public class NumeriActivity extends ActionBarActivity {
    private AlertDialog currentShowDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ConfigurationStorager.EitherConfigurations.DARK_THEME.isEnabled()) {
            setTheme(R.style.AppTheme_Dark);
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
     * Dialogをセットすると同時に表示します
     *
     * @param dialog
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
        super.onDestroy();
    }

    protected void startActivity(Class activityClass, boolean finish) {
        Intent intent = new Intent(this, activityClass);
        super.startActivity(intent);
        if (finish) {
            finish();
        }
    }
}
