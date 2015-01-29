package com.serori.numeri.config;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.serori.numeri.R;
import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.listview.action.ActionStorager;
import com.serori.numeri.listview.action.TwitterActions;
import com.serori.numeri.main.Application;
import com.serori.numeri.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * config
 */
public class ConfigActivity extends NumeriActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        if (savedInstanceState == null) {
            initActionConfComponent();
            initLayoutConfComponent();
        }
    }

    private void initActionConfComponent() {
        LinearLayout actionMenu = (LinearLayout) findViewById(R.id.actionMenu);
        Button openMenuButton = (Button) findViewById(R.id.openActionMenuButton);
        if (ConfigurationStorager.EitherConfigurations.DARK_THEME.isEnabled()) {
            openMenuButton.setTextColor(Color.parseColor("#FFFFFF"));
            for (int i = 0; i < actionMenu.getChildCount(); i++) {
                ((Button) actionMenu.getChildAt(i)).setTextColor(Color.parseColor("#FFFFFF"));
            }
        }

        Button rightTapConfButton = (Button) findViewById(R.id.rightTapConfButton);
        Button centerTapConfButton = (Button) findViewById(R.id.centerTapConfButton);
        Button leftTapConfButton = (Button) findViewById(R.id.leftTapConfButton);
        Button rightLongTapConfButton = (Button) findViewById(R.id.rightLongTapConfButton);
        Button centerLongTapConfButton = (Button) findViewById(R.id.centerLongTapConfButton);
        Button leftLongTapConfButton = (Button) findViewById(R.id.leftLongTapConfButton);

        openMenuButton.setOnClickListener(v -> {
            if (actionMenu.getVisibility() == View.GONE) {
                actionMenu.setVisibility(View.VISIBLE);
            } else if (actionMenu.getVisibility() == View.VISIBLE) {
                actionMenu.setVisibility(View.GONE);
            }
        });
        rightTapConfButton.setOnClickListener(v -> chooseAction(ActionStorager.RespectTapPositionActions.RIGHT));
        centerTapConfButton.setOnClickListener(v -> chooseAction(ActionStorager.RespectTapPositionActions.CENTER));
        leftTapConfButton.setOnClickListener(v -> chooseAction(ActionStorager.RespectTapPositionActions.LEFT));
        rightLongTapConfButton.setOnClickListener(v -> chooseAction(ActionStorager.RespectTapPositionActions.LONG_RIGHT));
        centerLongTapConfButton.setOnClickListener(v -> chooseAction(ActionStorager.RespectTapPositionActions.LONG_CENTER));
        leftLongTapConfButton.setOnClickListener(v -> chooseAction(ActionStorager.RespectTapPositionActions.LONG_LEFT));
    }

    private void initLayoutConfComponent() {
        String textColor = "#000000";
        if (ConfigurationStorager.EitherConfigurations.DARK_THEME.isEnabled()) {
            textColor = "#FFFFFF";
        }
        Button chooseThemeButton = (Button) findViewById(R.id.chooseThemeButton);
        chooseThemeButton.setTextColor(Color.parseColor(textColor));
        Button chooseTextSizeButton = (Button) findViewById(R.id.chooseCharSizeButton);
        chooseTextSizeButton.setTextColor(Color.parseColor(textColor));
        //onCLick
        chooseThemeButton.setOnClickListener(v -> chooseThema());
        chooseTextSizeButton.setOnClickListener(v -> chooseTextSize());
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
                    dialog.dismiss();
                })
                .create();
        setCurrentShowDialog(alertDialog);
    }

    private void chooseTextSize() {
        CharSequence[] textSizes = new CharSequence[15];
        for (int i = 0; i < textSizes.length; i++) {
            textSizes[i] = "" + (i + 8);
        }
        int checkedItem = ConfigurationStorager.NumericalConfigurations.CHARACTER_SIZE.getNumericValue();
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setSingleChoiceItems(textSizes, checkedItem, (dialog, witch) -> {
                    ConfigurationStorager.NumericalConfigurations.CHARACTER_SIZE.setNumericValue(witch);

                    ConfigurationStorager.getInstance().saveNumercalConfigTable(ConfigurationStorager.NumericalConfigurations.CHARACTER_SIZE);
                    ((AlertDialog) dialog).hide();
                    dialog.dismiss();
                })
                .create();
        setCurrentShowDialog(alertDialog);
    }

    private void chooseAction(ActionStorager.RespectTapPositionActions respectTapPositionActions) {
        List<CharSequence> actionTexts = new ArrayList<>();
        List<TwitterActions.Actions> canSetActions = new ArrayList<>();
        for (TwitterActions.Actions action : TwitterActions.Actions.values()) {
            if (TwitterActions.Actions.OPEN_URI != action) {
                actionTexts.add(action.getName());
                canSetActions.add(action);
            }
        }
        AlertDialog alertDialog = new AlertDialog.Builder(this).setSingleChoiceItems(actionTexts.toArray(new CharSequence[actionTexts.size()]),
                respectTapPositionActions.getTwitterAction().getId(), (dialog, which) -> {
                    respectTapPositionActions.setTwitterAction(canSetActions.get(which));
                    ActionStorager.getInstance().saveActions();
                    ((AlertDialog) dialog).hide();
                    dialog.dismiss();
                }).create();
        setCurrentShowDialog(alertDialog);
    }


    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (Application.getInstance().isDestroyMainActivity()) {
                startActivity(MainActivity.class, true);
            } else {
                finish();
            }
            return true;
        }
        return false;
    }
}
