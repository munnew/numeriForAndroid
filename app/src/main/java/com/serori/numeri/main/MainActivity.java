package com.serori.numeri.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.serori.numeri.R;
import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.application.Application;
import com.serori.numeri.color.ColorManagerActivity;
import com.serori.numeri.color.ColorStorager;
import com.serori.numeri.config.ConfigActivity;
import com.serori.numeri.config.ConfigurationStorager;
import com.serori.numeri.fragment.MentionsFlagment;
import com.serori.numeri.fragment.NumeriFragment;
import com.serori.numeri.fragment.SectionsPagerAdapter;
import com.serori.numeri.fragment.TimeLineFragment;
import com.serori.numeri.fragment.manager.FragmentManagerActivity;
import com.serori.numeri.fragment.manager.FragmentStorager;
import com.serori.numeri.oauth.OAuthActivity;
import com.serori.numeri.stream.OnFavoriteListener;
import com.serori.numeri.toast.ToastSender;
import com.serori.numeri.twitter.TweetActivity;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.user.NumeriUserStorager;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public class MainActivity extends NumeriActivity implements OnFavoriteListener {
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private List<NumeriFragment> numeriFragments = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Application.getInstance().setApplicationContext(getApplicationContext());
        Application.getInstance().setMainActivityContext(this);
        ConfigurationStorager.getInstance().loadConfigurations();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ConfigurationStorager.EitherConfigurations.ADD_MENUBUTTON.isEnabled()) {
            addMenuButton();
        }
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(30);
        ImageButton changeTweetActivityButton;
        changeTweetActivityButton = (ImageButton) findViewById(R.id.goTweetButton);
        changeTweetActivityButton.setOnClickListener(v -> startActivity(TweetActivity.class, false));
        if (savedInstanceState == null) {
            Log.v("initLoad", "init");
            List<AccessToken> tokens = new ArrayList<>();
            tokens.addAll(NumeriUserStorager.getInstance().loadNumeriUserTokens());
            if (tokens.isEmpty()) {
                startActivity(OAuthActivity.class, true);
            } else {
                AsyncTask.execute(() -> {
                    Application.getInstance().getNumeriUsers().clear();
                    for (AccessToken token : tokens) {
                        Application.getInstance().getNumeriUsers().addNumeriUser(new NumeriUser(token));
                    }
                    Log.v("MainActivity", "users = " + Application.getInstance().getNumeriUsers().getNumeriUsers().size());
                    for (NumeriUser numeriUser : Application.getInstance().getNumeriUsers().getNumeriUsers()) {
                        numeriUser.getStreamSwitcher().startStream();
                        numeriUser.getStreamEvent().addOwnerOnfavoriteListener(this);
                    }
                    init();
                });

            }
        } else {
            Log.v("restoreLoad", "restore;fragmentsSize" + NumeriFragmentManager.getInstance().getNumeriFragments().size());
            for (NumeriFragment numeriFragment : NumeriFragmentManager.getInstance().getNumeriFragments()) {
                sectionsPagerAdapter.add((Fragment) numeriFragment);
            }
            viewPager.setAdapter(sectionsPagerAdapter);
        }
        Log.v("MainActivity", "numeriUsers : " + Application.getInstance().getNumeriUsers().getNumeriUsers().size());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!numeriFragments.isEmpty()) {
            Log.v("save", "saved");
            NumeriFragmentManager.getInstance().putFragments(numeriFragments);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Log.v("Restore", "onRestoreInstanceState:fragmentsSize" + NumeriFragmentManager.getInstance().getNumeriFragments().size());
        super.onRestoreInstanceState(savedInstanceState);
        if (!NumeriFragmentManager.getInstance().getNumeriFragments().isEmpty()) {
            numeriFragments.addAll(NumeriFragmentManager.getInstance().getNumeriFragments());
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
            case R.id.action_acount:
                startActivity(OAuthActivity.class, false);
                break;
            case R.id.action_fragment_manager:
                startActivity(FragmentManagerActivity.class, false);
                break;
            case R.id.action_color_manager:
                startActivity(ColorManagerActivity.class, false);
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


    private NumeriFragment initNumeriFragment(NumeriUser numeriUser, NumeriFragment numeriFragment) {
        String screenName;

        screenName = numeriUser.getScreenName();
        numeriFragment.setNumeriUser(numeriUser);
        numeriFragment.setFragmentName(screenName);
        return numeriFragment;
    }

    private void init() {

        ColorStorager.getInstance().loadColor();

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
    public void onFavorite(User source, User target, Status favoritedStatus) {
        for (NumeriUser numeriUser : Application.getInstance().getNumeriUsers().getNumeriUsers()) {
            if (target.getId() == numeriUser.getAccessToken().getUserId()) {
               ToastSender.getInstance().sendToast(source.getScreenName() + "さんに" + target.getScreenName() + "のツイートがお気に入り登録されました");
            }
        }
    }

    public void addMenuButton() {
        LinearLayout menuButton = (LinearLayout) findViewById(R.id.menuButton);
        menuButton.setVisibility(View.VISIBLE);
        menuButton.setOnClickListener(v -> openOptionsMenu());
        menuButton.getChildAt(0).setOnClickListener(v -> openOptionsMenu());
    }

    public void removeMenuButton() {
        LinearLayout menuButton = (LinearLayout) findViewById(R.id.menuButton);
        menuButton.setVisibility(View.GONE);
    }
}
