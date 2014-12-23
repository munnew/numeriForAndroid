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

import com.serori.numeri.Application.Application;
import com.serori.numeri.R;
import com.serori.numeri.fragment.MentionsFlagment;
import com.serori.numeri.fragment.NumeriFragment;
import com.serori.numeri.fragment.SectionsPagerAdapter;
import com.serori.numeri.fragment.TimeLineFragment;
import com.serori.numeri.oauth.OAuthActivity;
import com.serori.numeri.twitter.TweetActivity;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.user.NumeriUserStorager;

import java.util.ArrayList;
import java.util.List;

import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

public class MainActivity extends ActionBarActivity implements OnToast {
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private List<AccessToken> tokens = new ArrayList<>();
    private List<NumeriFragment> numeriFragments = new ArrayList<>();
    private ImageButton changetweetActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            Application.getInstance().setApplicationContext(getApplicationContext());
            Application.getInstance().setMainActivityContext(this);
            Application.getInstance().setOnToastListener(this);
            sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            viewPager = (ViewPager) findViewById(R.id.pager);
            viewPager.setOffscreenPageLimit(30);
            tokens.addAll(NumeriUserStorager.getInstance().loadNumeriUserTokens());
            if (tokens.isEmpty()) {
                startOauthActivity(true);
            } else {
                for (AccessToken token : tokens) {
                    Application.getInstance().getNumeriUsers().addNumeriUser(new NumeriUser(token));
                }
                Log.v("Users =", "" + Application.getInstance().getNumeriUsers().getNumeriUsers().size());
                for (NumeriUser numeriUser : Application.getInstance().getNumeriUsers().getNumeriUsers()) {
                    numeriUser.getStreamSwicher().startStream();
                }
            }


            changetweetActivityButton = (ImageButton) findViewById(R.id.goTweetButton);
            init();
            changetweetActivityButton.setOnClickListener(v -> startTweetActivity(false));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

    private void init() {
        AsyncTask.execute(() -> {
            for (NumeriUser numeriUser : Application.getInstance().getNumeriUsers().getNumeriUsers()) {
                NumeriFragment numeriTimeLineFragment = new TimeLineFragment();
                NumeriFragment numeriMentionsFragment = new MentionsFlagment();
                numeriTimeLineFragment.setNumeriUser(numeriUser);
                numeriMentionsFragment.setNumeriUser(numeriUser);
                String screenName = null;
                try {
                    screenName = numeriUser.getTwitter().getScreenName();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                numeriTimeLineFragment.setFragmentName(screenName);
                numeriMentionsFragment.setFragmentName(screenName);
                numeriFragments.add(numeriTimeLineFragment);
                numeriFragments.add(numeriMentionsFragment);
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
}
