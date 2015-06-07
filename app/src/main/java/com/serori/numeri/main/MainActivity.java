package com.serori.numeri.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.serori.numeri.notification.NotificationSender;
import com.serori.numeri.R;
import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.color.ColorManagerActivity;
import com.serori.numeri.config.ConfigActivity;
import com.serori.numeri.exceptionreport.ExceptionReportStorager;
import com.serori.numeri.fragment.NumeriFragment;
import com.serori.numeri.fragment.SectionsPagerAdapter;
import com.serori.numeri.main.manager.FragmentManagerActivity;
import com.serori.numeri.main.manager.FragmentStorager;
import com.serori.numeri.oauth.OAuthActivity;
import com.serori.numeri.twitter.SimpleTweetStatus;
import com.serori.numeri.twitter.TweetActivity;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.user.NumeriUserStorager;
import com.serori.numeri.fragment.listview.item.UserListItem;
import com.serori.numeri.util.toast.ToastSender;
import com.serori.numeri.util.twitter.TwitterAPIConfirmer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import twitter4j.RateLimitStatus;
import twitter4j.TwitterException;

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

    @SuppressWarnings("unchecked")
    private void init() {
        Log.v("initLoad", "init");
        List<NumeriUserStorager.NumeriUserTable> tables = new ArrayList<>();
        tables.addAll(NumeriUserStorager.getInstance().loadNumeriUserTables());
        if (tables.isEmpty()) {
            startActivity(OAuthActivity.class, true);
        } else {
            Handler handler = new Handler();
            new Thread(() -> {
                runOnUiThread(() -> {
                    infoTextView.setVisibility(View.VISIBLE);
                    infoTextView.setText("ユーザー情報を取得中...");
                });
                Global.getInstance().getNumeriUsers().clear();
                for (NumeriUserStorager.NumeriUserTable table : NumeriUserStorager.getInstance().loadNumeriUserTables()) {
                    Global.getInstance().getNumeriUsers().addNumeriUser(new NumeriUser(table));
                }
                for (NumeriUser numeriUser : Global.getInstance().getNumeriUsers().getNumeriUsers()) {
                    numeriUser.getStreamSwitcher().startStream();
                    runOnUiThread(() -> infoTextView.setText(numeriUser.getScreenName() + " : startStream"));
                }
                NotificationSender.getInstance().sendStart();
                List<NumeriUser> numeriUsers = NumeriUsers.getInstance().getNumeriUsers();
                List<NumeriFragment> numeriFragments = new ArrayList<>();
                if (!numeriUsers.isEmpty()) {
                    SimpleTweetStatus.startObserveFavorite();
                    //SimpleTweetStatus.startObserveDestroyTweet();
                    UserListItem.startObserveRelation();
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
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
                return true;
            case KeyEvent.KEYCODE_HOME:
                moveTaskToBack(true);
                return true;
            default:
                return false;
        }
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
