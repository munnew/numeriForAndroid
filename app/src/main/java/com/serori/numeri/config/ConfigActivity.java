package com.serori.numeri.config;

import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.serori.numeri.R;
import com.serori.numeri.activity.SubsidiaryActivity;
import com.serori.numeri.license.LicenseActivity;
import com.serori.numeri.listview.action.ActionStorager;
import com.serori.numeri.listview.action.TwitterActions;
import com.serori.numeri.main.Global;
import com.serori.numeri.main.MainActivity;
import com.serori.numeri.util.toast.ToastSender;

import java.util.ArrayList;
import java.util.List;

/**
 * アプリケーションの設定についてのActivity
 */
public class ConfigActivity extends SubsidiaryActivity {
    private boolean previousTheme;
    private String textColor = "#000000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        previousTheme = ConfigurationStorager.EitherConfigurations.DARK_THEME.isEnabled();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        if (savedInstanceState == null) {
            if (ConfigurationStorager.EitherConfigurations.DARK_THEME.isEnabled()) {
                textColor = "#FFFFFF";
            }
            initActionConfComponent();
            initLayoutConfComponent();
            initLicenseComponent();
        }
    }

    private void initActionConfComponent() {
        LinearLayout actionMenu = (LinearLayout) findViewById(R.id.actionMenu);
        Button openMenuButton = (Button) findViewById(R.id.openActionMenuButton);
        TextView tweetAdditionalAcquisitionFlagText = ((TextView) findViewById(R.id.tweetAdditionalAcquisitionFlagText));
        TextView sleeplessText = (TextView) findViewById(R.id.sleeplessText);
        TextView useHighResolutionIconText = (TextView) findViewById(R.id.useHighResolutionIconText);
        TextView fastScrollText = (TextView) findViewById(R.id.fastScrollText);
        TextView displayImageThumbText = (TextView) findViewById(R.id.displayImageThumbText);
        openMenuButton.setTextColor(Color.parseColor(textColor));
        tweetAdditionalAcquisitionFlagText.setTextColor(Color.parseColor(textColor));
        sleeplessText.setTextColor(Color.parseColor(textColor));
        useHighResolutionIconText.setTextColor(Color.parseColor(textColor));
        fastScrollText.setTextColor(Color.parseColor(textColor));
        displayImageThumbText.setTextColor(Color.parseColor(textColor));
        for (int i = 0; i < actionMenu.getChildCount(); i++) {
            ((Button) actionMenu.getChildAt(i)).setTextColor(Color.parseColor(textColor));
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


        rightTapConfButton.setOnClickListener(v -> chooseAction(ActionStorager.RespectTapPositionActions.RIGHT, ((Button) v).getText()));
        centerTapConfButton.setOnClickListener(v -> chooseAction(ActionStorager.RespectTapPositionActions.CENTER, ((Button) v).getText()));
        leftTapConfButton.setOnClickListener(v -> chooseAction(ActionStorager.RespectTapPositionActions.LEFT, ((Button) v).getText()));
        rightLongTapConfButton.setOnClickListener(v -> chooseAction(ActionStorager.RespectTapPositionActions.LONG_RIGHT, ((Button) v).getText()));
        centerLongTapConfButton.setOnClickListener(v -> chooseAction(ActionStorager.RespectTapPositionActions.LONG_CENTER, ((Button) v).getText()));
        leftLongTapConfButton.setOnClickListener(v -> chooseAction(ActionStorager.RespectTapPositionActions.LONG_LEFT, ((Button) v).getText()));

        CheckBox isSleeplessCheckBox = (CheckBox) findViewById(R.id.isSleeplessCheckBox);
        initChooseEitherAction(sleeplessText, isSleeplessCheckBox, ConfigurationStorager.EitherConfigurations.SLEEPLESS);

        CheckBox isConfirmationLessGetTweetCheckBox = (CheckBox) findViewById(R.id.confirmationLessGetTweetCheckBox);
        initChooseEitherAction(tweetAdditionalAcquisitionFlagText, isConfirmationLessGetTweetCheckBox,
                ConfigurationStorager.EitherConfigurations.CONFIRMATION_LESS_GET_TWEET);

        CheckBox useHighResolutionIconCheckBox = (CheckBox) findViewById(R.id.useHighResolutionIconCheckBox);
        initChooseEitherAction(useHighResolutionIconText, useHighResolutionIconCheckBox,
                ConfigurationStorager.EitherConfigurations.USE_HIGH_RESOLUTION_ICON);

        CheckBox fastScrollCheckBox = (CheckBox) findViewById(R.id.fastScrollCheckBox);
        initChooseEitherAction(fastScrollText, fastScrollCheckBox,
                ConfigurationStorager.EitherConfigurations.USE_FAST_SCROLL);

        CheckBox displayImageThumbCheckBox = (CheckBox) findViewById(R.id.displayImageThumbCheckBox);
        initChooseEitherAction(displayImageThumbText, displayImageThumbCheckBox,
                ConfigurationStorager.EitherConfigurations.DISPLAY_IMAGE_THUMB);

    }

    private void initChooseEitherAction(TextView textView, CheckBox checkBox, ConfigurationStorager.EitherConfigurations eitherConfiguration) {
        checkBox.setChecked(eitherConfiguration.isEnabled());
        checkBox.setOnClickListener(v -> chooseEither(eitherConfiguration, (CheckBox) v));
        textView.setOnClickListener(v -> chooseEither(eitherConfiguration, checkBox));
    }

    private void initLayoutConfComponent() {
        Button chooseThemeButton = (Button) findViewById(R.id.chooseThemeButton);
        chooseThemeButton.setTextColor(Color.parseColor(textColor));
        Button chooseTextSizeButton = (Button) findViewById(R.id.chooseCharSizeButton);
        chooseTextSizeButton.setTextColor(Color.parseColor(textColor));
        //onCLick
        chooseThemeButton.setOnClickListener(v -> chooseTheme());
        chooseTextSizeButton.setOnClickListener(v -> chooseTextSize());
    }

    private void initLicenseComponent() {
        Button showTwitter4JLicenseButton = (Button) findViewById(R.id.showTwitter4j_license);
        showTwitter4JLicenseButton.setTextColor(Color.parseColor(textColor));
        Button showOrmLiteLicenseButton = (Button) findViewById(R.id.showOrmLiteL_license);
        showOrmLiteLicenseButton.setTextColor(Color.parseColor(textColor));

        showTwitter4JLicenseButton.setOnClickListener(v -> LicenseActivity.show(this, LicenseActivity.twitter4JLicense));
        showOrmLiteLicenseButton.setOnClickListener(v -> LicenseActivity.show(this, LicenseActivity.ormLiteLicense));

    }

    private void chooseTheme() {
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
                    ConfigurationStorager.getInstance().saveNumericalConfigTable(ConfigurationStorager.NumericalConfigurations.CHARACTER_SIZE);
                    ((AlertDialog) dialog).hide();
                    dialog.dismiss();
                })
                .create();
        setCurrentShowDialog(alertDialog);
    }

    private void chooseAction(ActionStorager.RespectTapPositionActions respectTapPositionActions, CharSequence title) {
        List<CharSequence> actionTexts = new ArrayList<>();
        List<TwitterActions.Actions> actions = new ArrayList<>();
        for (TwitterActions.Actions action : TwitterActions.Actions.values()) {
            if (!action.getName().equals("")) {
                actionTexts.add(action.getName());
                actions.add(action);
            }
        }
        int position = 0;
        for (TwitterActions.Actions action : actions) {
            if (action == respectTapPositionActions.getTwitterAction()) {
                break;
            }
            position++;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title)
                .setSingleChoiceItems(actionTexts.toArray(new CharSequence[actionTexts.size()]),
                        position, (dialog, which) -> {
                            respectTapPositionActions.setTwitterAction(actions.get(which));
                            AsyncTask.execute(() -> ActionStorager.getInstance().saveActions());
                            ((AlertDialog) dialog).hide();
                            dialog.dismiss();
                        }).create();
        setCurrentShowDialog(alertDialog);
    }

    private void chooseEither(ConfigurationStorager.EitherConfigurations eitherConfigurations, CheckBox checkBox) {
        eitherConfigurations.setEnabled(!eitherConfigurations.isEnabled());
        checkBox.setChecked(eitherConfigurations.isEnabled());
        ConfigurationStorager.getInstance().saveEitherConfigTable(eitherConfigurations);
        if (eitherConfigurations.equals(ConfigurationStorager.EitherConfigurations.SLEEPLESS))
            ToastSender.sendToast("この設定は次回の起動から適用されます");
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (previousTheme != ConfigurationStorager.EitherConfigurations.DARK_THEME.isEnabled()) {
                Global.getInstance().destroyMainActivity();
                Toast.makeText(this, "テーマが変更されました。アプリケーションを再起動します。", Toast.LENGTH_SHORT).show();
            }
            if (Global.getInstance().isActiveMainActivity()) {
                startActivity(MainActivity.class, true);
            } else {
                finish();
            }
            return true;
        }
        return false;
    }
}
