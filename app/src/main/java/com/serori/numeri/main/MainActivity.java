package com.serori.numeri.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.serori.numeri.R;
import com.serori.numeri.application.Application;
import com.serori.numeri.color.ColorManagerActivity;
import com.serori.numeri.color.ColorStorager;
import com.serori.numeri.color.Colors;
import com.serori.numeri.fragment.MentionsFlagment;
import com.serori.numeri.fragment.NumeriFragment;
import com.serori.numeri.fragment.SectionsPagerAdapter;
import com.serori.numeri.fragment.TimeLineFragment;
import com.serori.numeri.fragment.manager.FragmentManagerActivity;
import com.serori.numeri.fragment.manager.FragmentStorager;
import com.serori.numeri.oauth.OAuthActivity;
import com.serori.numeri.stream.OnFavoriteListener;
import com.serori.numeri.twitter.TweetActivity;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.user.NumeriUserStorager;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public class MainActivity extends ActionBarActivity implements OnToast, OnFavoriteListener {
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private List<NumeriFragment> numeriFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(30);
        ImageButton changeTweetActivityButton;
        changeTweetActivityButton = (ImageButton) findViewById(R.id.goTweetButton);
        changeTweetActivityButton.setOnClickListener(v -> startTweetActivity(false));
        if (savedInstanceState == null) {
            Log.v("initLoad", "init");
            Application.getInstance().setApplicationContext(getApplicationContext());
            Application.getInstance().setMainActivityContext(this);
            Application.getInstance().setOnToastListener(this);

            List<AccessToken> tokens = new ArrayList<>();
            tokens.addAll(NumeriUserStorager.getInstance().loadNumeriUserTokens());
            if (tokens.isEmpty()) {
                startOauthActivity(true);
            } else {
                AsyncTask.execute(() -> {
                    Application.getInstance().getNumeriUsers().clear();
                    for (AccessToken token : tokens) {
                        Application.getInstance().getNumeriUsers().addNumeriUser(new NumeriUser(token));
                    }
                    Log.v("MainActivity", "users = " + Application.getInstance().getNumeriUsers().getNumeriUsers().size());
                    for (NumeriUser numeriUser : Application.getInstance().getNumeriUsers().getNumeriUsers()) {
                        numeriUser.getStreamSwicher().startStream();
                        numeriUser.getStreamEvent().addOwnerOnfavoriteListener(this);
                    }
                    init();
                });

            }
        } else {
            Log.v("restoreLoad", "restore;fragmentsSize" + Application.getInstance().getNumeriFragmentManager().getNumeriFragments().size());
            for (NumeriFragment numeriFragment : Application.getInstance().getNumeriFragmentManager().getNumeriFragments()) {
                sectionsPagerAdapter.add((Fragment) numeriFragment);
            }
            viewPager.setAdapter(sectionsPagerAdapter);
        }
        Log.v("MainActivity", "numeriUsers : " + Application.getInstance().getNumeriUsers().getNumeriUsers().size());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!numeriFragments.isEmpty()) {
            Log.v("save", "saved");
            Application.getInstance().getNumeriFragmentManager().putFragments(numeriFragments);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.v("Restore", "onRestoreInstanceState:fragmentsSize" + Application.getInstance().getNumeriFragmentManager().getNumeriFragments().size());
        super.onRestoreInstanceState(savedInstanceState);
        if (!Application.getInstance().getNumeriFragmentManager().getNumeriFragments().isEmpty()) {
            numeriFragments.addAll(Application.getInstance().getNumeriFragmentManager().getNumeriFragments());
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
                break;
            case R.id.action_acount:
                startOauthActivity(false);
                break;
            case R.id.action_fragment_manager:
                startFragmentManagerActivity(false);
                break;
            case R.id.action_color_manager:
                startColorManager(false);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return false;
    }

    private void startOauthActivity(boolean isFinish) {
        Intent intent = new Intent(this, OAuthActivity.class);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    private void startTweetActivity(boolean isFinish) {
        Intent intent = new Intent(this, TweetActivity.class);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    private void startColorManager(boolean isFinish) {
        Intent intent = new Intent(this, ColorManagerActivity.class);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    private void startFragmentManagerActivity(boolean isFinish) {
        Application.getInstance().getNumeriFragmentManager().clear();
        Intent intent = new Intent(this, FragmentManagerActivity.class);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    private NumeriFragment initNumeriFragment(NumeriUser numeriUser, NumeriFragment numeriFragment) {
        String screenName;

        screenName = numeriUser.getScreenName();
        numeriFragment.setNumeriUser(numeriUser);
        numeriFragment.setFragmentName(screenName);
        return numeriFragment;
    }

    private void init() {

        Colors.getInstance().setRetweetColor(ColorStorager.getInstance().loadColorForId(ColorStorager.RT_ITEM));
        Colors.getInstance().setMentionColor(ColorStorager.getInstance().loadColorForId(ColorStorager.MENTION_ITEM));
        Colors.getInstance().setNomalColor(ColorStorager.getInstance().loadColorForId(ColorStorager.NOMAL_ITEM));
        Colors.getInstance().setMyTweetMarkColor(ColorStorager.getInstance().loadColorForId(ColorStorager.MYTWEET_MARK));

        AsyncTask.execute(() -> {
            List<NumeriUser> numeriUsers = new ArrayList<>();
            numeriUsers.addAll(Application.getInstance().getNumeriUsers().getNumeriUsers());
            for (FragmentStorager.FragmentsTable table : FragmentStorager.getInstance().getFragmentsData()) {
                if (table.getFragmentType().equals(FragmentStorager.TL)) {
                    for (NumeriUser numeriUser : numeriUsers) {
                        if (numeriUser.getAccessToken().getToken().equals(table.getUserToken())) {
                            numeriFragments.add(initNumeriFragment(numeriUser, new TimeLineFragment()));
                        }
                    }
                } else if (table.getFragmentType().equals(FragmentStorager.MENTIONS)) {
                    for (NumeriUser numeriUser : numeriUsers) {
                        if (numeriUser.getAccessToken().getToken().equals(table.getUserToken())) {
                            numeriFragments.add(initNumeriFragment(numeriUser, new MentionsFlagment()));
                        }
                    }
                }
            }
            runOnUiThread(() -> {
                for (NumeriFragment numeriFragment : numeriFragments) {
                    sectionsPagerAdapter.add((Fragment) numeriFragment);
                }
                viewPager.setAdapter(sectionsPagerAdapter);
            });
        });
    }

    @Override
    public void onToast(String text, int length) {
        runOnUiThread(() -> Toast.makeText(this, text, length).show());
    }

    @Override
    public void onFavorite(User source, User target, Status favoritedStatus) {
        for (NumeriUser numeriUser : Application.getInstance().getNumeriUsers().getNumeriUsers()) {
            if (target.getId() == numeriUser.getAccessToken().getUserId()) {
                runOnUiThread(() -> onToast(source.getScreenName() + "さんに" + target.getScreenName() + "のツイートがお気に入り登録されました", Toast.LENGTH_SHORT));
            }
        }
    }
}
