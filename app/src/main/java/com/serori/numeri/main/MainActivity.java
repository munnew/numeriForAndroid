package com.serori.numeri.main;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.serori.numeri.R;
import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.color.ColorManagerActivity;
import com.serori.numeri.config.ConfigActivity;
import com.serori.numeri.config.ConfigurationStorager;
import com.serori.numeri.exceptionreport.ExceptionReportStorager;
import com.serori.numeri.fragment.NumeriFragment;
import com.serori.numeri.fragment.SectionsPagerAdapter;
import com.serori.numeri.main.manager.FragmentManagerActivity;
import com.serori.numeri.main.manager.FragmentStorager;
import com.serori.numeri.oauth.OAuthActivity;
import com.serori.numeri.twitter.TweetActivity;
import com.serori.numeri.twitter.TweetSearchActivity;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.user.NumeriUserStorager;
import com.serori.numeri.util.toast.ToastSender;
import com.serori.numeri.util.twitter.TwitterAPIConfirmer;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity
 */
public class MainActivity extends NumeriActivity {
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private TextView infoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Global.getInstance().setMainActivityContext(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ExceptionReportStorager.sendReport(this);
        infoTextView = (TextView) findViewById(R.id.infoText);
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(20);
        ImageButton startTweetActivityButton = (ImageButton) findViewById(R.id.goTweetButton);
        startTweetActivityButton.setOnClickListener(v -> {
            if (!NumeriUsers.getInstance().getNumeriUsers().isEmpty())
                startActivity(TweetActivity.class, false);
        });
        if (savedInstanceState == null) {
            init();
        } else {
            if (!Global.getInstance().getNumeriUsers().getNumeriUsers().isEmpty()) {
                viewPager.setAdapter(sectionsPagerAdapter);
            } else if (!NumeriUserStorager.getInstance().loadNumeriUserTables().isEmpty()) {
                startActivity(getClass(), true);
            } else {
                startActivity(OAuthActivity.class, true);
            }
        }
    }

    private void init() {
        Log.v("initLoad", "init");
        infoTextView.setVisibility(View.VISIBLE);
        List<NumeriUserStorager.NumeriUserTable> tables = new ArrayList<>();
        tables.addAll(NumeriUserStorager.getInstance().loadNumeriUserTables());
        if (tables.isEmpty()) {
            startActivity(OAuthActivity.class, false);
            infoTextView.setText("メニュー->ユーザー管理 からユーザーを追加してください");
            NumeriUserStorager.getInstance().addOnAddedNumeriUserListener(numeriUser -> {
                FragmentStorager.FragmentsTable tlFragmentTable
                        = new FragmentStorager.FragmentsTable(FragmentStorager.FragmentType.TL, numeriUser.getScreenName(), numeriUser.getAccessToken().getToken());
                FragmentStorager.FragmentsTable mentionsFragmentTable
                        = new FragmentStorager.FragmentsTable(FragmentStorager.FragmentType.MENTIONS, numeriUser.getScreenName(), numeriUser.getAccessToken().getToken());
                FragmentStorager fragmentStorager = FragmentStorager.getInstance();
                fragmentStorager.saveFragmentData(tlFragmentTable);
                fragmentStorager.saveFragmentData(mentionsFragmentTable);
                initNumeriFragments();
            });
        } else {
            initNumeriFragments();
        }
    }

    private void initNumeriFragments() {
        Handler handler = new Handler();
        boolean createdNumeriUser = !NumeriUsers.getInstance().getNumeriUsers().isEmpty();

        new Thread(() -> {
            if (!createdNumeriUser) {
                runOnUiThread(() -> infoTextView.setText("ユーザー情報を取得中..."));
                Global.getInstance().getNumeriUsers().clear();
                for (NumeriUserStorager.NumeriUserTable table : NumeriUserStorager.getInstance().loadNumeriUserTables()) {
                    NumeriUser numeriUser = new NumeriUser(table);
                    numeriUser.getStreamSwitcher().startStream();
                    Global.getInstance().getNumeriUsers().addNumeriUser(numeriUser);
                    runOnUiThread(() -> infoTextView.setText(numeriUser.getScreenName() + " : startStream"));
                }
            }
            List<NumeriUser> numeriUsers = NumeriUsers.getInstance().getNumeriUsers();
            List<NumeriFragment> numeriFragments = new ArrayList<>();
            if (!numeriUsers.isEmpty()) {
                numeriFragments.addAll(FragmentStorager.getInstance().getFragments(numeriUsers));
            }
            handler.post(() -> {
                if (!numeriFragments.isEmpty()) {
                    infoTextView.setText("フラグメントを生成しています...");
                    for (NumeriFragment numeriFragment : numeriFragments) {
                        sectionsPagerAdapter.add(numeriFragment);
                    }
                    viewPager.setAdapter(sectionsPagerAdapter);
                    infoTextView.setVisibility(View.GONE);
                } else {
                    infoTextView.setText("メニュー -> フラグメント管理 からフラグメントを追加してください。");
                }
            });
        }).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (ConfigurationStorager.EitherConfigurations.DARK_THEME.isEnabled())
            menu.findItem(R.id.action_dm).setIcon(R.drawable.ic_dm_light);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                startActivity(TweetSearchActivity.class, false);
                break;
            case R.id.action_dm:
                break;
            case R.id.action_settings:
                startActivity(ConfigActivity.class, false);
                break;
            case R.id.action_account:
                if (!NumeriUsers.getInstance().getNumeriUsers().isEmpty())
                    startActivity(OAuthActivity.class, false);
                break;
            case R.id.action_fragment_manager:
                if (!NumeriUsers.getInstance().getNumeriUsers().isEmpty())
                    startActivity(FragmentManagerActivity.class, false);
                break;
            case R.id.action_color_manager:
                startActivity(ColorManagerActivity.class, false);
                break;
            case R.id.action_api_confirmation:
                if (!NumeriUsers.getInstance().getNumeriUsers().isEmpty())
                    useApiFrequencyConfirmation();
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                moveTaskToBack(true);
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void useApiFrequencyConfirmation() {
        List<NumeriUser> numeriUsers = Global.getInstance().getNumeriUsers().getNumeriUsers();
        CharSequence[] names = new CharSequence[numeriUsers.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = numeriUsers.get(i).getScreenName();
        }
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("ユーザーを選択").setItems(names, (dialog, witch) -> {
            numeriUsers.get(witch).getTwitterAPIConfirmer().acquireTwitterAPIRemaining(apiRemainingInfo -> {
                String apiConfirmation = "get home_timeline API: remaining: " + apiRemainingInfo.get(TwitterAPIConfirmer.TwitterAPI.HOME_TIMELINE) + "\n";
                apiConfirmation += "get mentions_timeline API: remaining: " + apiRemainingInfo.get(TwitterAPIConfirmer.TwitterAPI.MENTIONS_TIMELINE);
                ToastSender.sendToast(apiConfirmation);
            });
        }).create();
        setCurrentShowDialog(alertDialog);
    }

    @Override
    public void finish() {
        Global.getInstance().restartMainActivityCallBack();
        super.finish();
    }
}
