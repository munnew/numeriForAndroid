package com.serori.numeri.config;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.widget.Button;

import com.serori.numeri.R;
import com.serori.numeri.application.Application;
import com.serori.numeri.main.MainActivity;

/**
 * config
 */
public class ConfigActivity extends ActionBarActivity {
    private Button chooseThemaButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ConfigurationStorager.EitherConfigurations.DARK_THEME.isEnabled()) {
            setTheme(R.style.Base_ThemeOverlay_AppCompat_Dark_ActionBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        if (savedInstanceState == null) {
            init();
        }
    }

    private void init() {
        chooseThemaButton = (Button) findViewById(R.id.chooseThemeButton);

        chooseThemaButton.setOnClickListener(v -> chooseThema());
    }

    private void chooseThema() {
        CharSequence[] themes = {"ライトテーマ", "ダークテーマ"};
        boolean theme = ConfigurationStorager.EitherConfigurations.DARK_THEME.isEnabled();
        int checkedItem = theme ? 1 : 0;
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setSingleChoiceItems(themes, checkedItem, (dialog, witch) -> {
                    switch (witch) {
                        case 0:
                            ConfigurationStorager.EitherConfigurations.DARK_THEME.setEnabled(false);
                            break;
                        case 1:
                            ConfigurationStorager.EitherConfigurations.DARK_THEME.setEnabled(true);
                            break;
                        default:
                            ConfigurationStorager.EitherConfigurations.DARK_THEME.setEnabled(false);
                    }
                    ConfigurationStorager.getInstance().saveEitherConfigTable(ConfigurationStorager.EitherConfigurations.DARK_THEME);
                    Application.getInstance().destroyMainActivity();
                    ((AlertDialog) dialog).hide();
                })
                .create();
        alertDialog.show();

    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (Application.getInstance().isDestroyMainActivity()) {
                startMainActivity(true);
            } else {
                finish();
            }
            return true;
        }
        return false;
    }

    private void startMainActivity(boolean isFinish) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

}
