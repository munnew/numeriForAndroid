package com.serori.numeri.main;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.serori.numeri.R;
import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.color.ColorManagerActivity;
import com.serori.numeri.color.ColorStorager;
import com.serori.numeri.config.ConfigActivity;
import com.serori.numeri.config.ConfigurationStorager;
import com.serori.numeri.fragment.NumeriFragment;
import com.serori.numeri.fragment.SectionsPagerAdapter;
import com.serori.numeri.listview.action.ActionStorager;
import com.serori.numeri.main.manager.FragmentManagerActivity;
import com.serori.numeri.main.manager.FragmentStorager;
import com.serori.numeri.oauth.OAuthActivity;
import com.serori.numeri.stream.event.OnFavoriteListener;
import com.serori.numeri.twitter.SimpleTweetStatus;
import com.serori.numeri.twitter.TweetActivity;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.user.NumeriUserStorager;
import com.serori.numeri.util.async.SimpleAsyncTask;
import com.serori.numeri.util.toast.ToastSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * MainActivity
 */
public class MainActivity extends NumeriActivity implements OnFavoriteListener {
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private List<NumeriFragment> numeriFragments = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Application.getInstance().setApplicationContext(getApplicationContext());
        Application.getInstance().setMainActivityContext(this);

        loadConfigrations();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addMenuButton();
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
            for (NumeriFragment numeriFragment : NumeriFragmentManager.getInstance().getNumeriFragments()) {
                sectionsPagerAdapter.add(numeriFragment);
            }
            viewPager.setAdapter(sectionsPagerAdapter);
        }
    }

    private void loadConfigrations() {
        ConfigurationStorager.getInstance().loadConfigurations();
        ActionStorager.getInstance().initializeActions();
        ColorStorager.getInstance().loadColor();
    }


    private void init() {
        Log.v("initLoad", "init");
        List<NumeriUserStorager.NumeriUserTable> tables = new ArrayList<>();
        tables.addAll(NumeriUserStorager.getInstance().loadNumeriUserTables());
        if (tables.isEmpty()) {
            startActivity(OAuthActivity.class, true);
        } else {
            SimpleAsyncTask.backgroundExecute(() -> {
                Application.getInstance().getNumeriUsers().clear();
                for (NumeriUserStorager.NumeriUserTable table : tables) {
                    Application.getInstance().getNumeriUsers().addNumeriUser(new NumeriUser(table));
                }
                for (NumeriUser numeriUser : Application.getInstance().getNumeriUsers().getNumeriUsers()) {
                    numeriUser.getStreamSwitcher().startStream();
                    numeriUser.getStreamEvent().addOnFavoriteListener(this);
                }
                numeriFragments.clear();
                List<NumeriUser> numeriUsers = NumeriUsers.getInstance().getNumeriUsers();
                if (!numeriUsers.isEmpty()) {
                    SimpleTweetStatus.startObserveFavorite();
                    numeriFragments.addAll(FragmentStorager.getInstance().getFragments(numeriUsers));
                    runOnUiThread(() -> {
                        for (NumeriFragment numeriFragment : numeriFragments) {
                            sectionsPagerAdapter.add(numeriFragment);
                        }
                        viewPager.setAdapter(sectionsPagerAdapter);
                    });
                }
            });
        }
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!numeriFragments.isEmpty()) {
            NumeriFragmentManager.getInstance().putFragments(numeriFragments);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return false;
    }

    @Override
    public void onFavorite(User source, User target, Status favoritedStatus) {
        for (NumeriUser numeriUser : Application.getInstance().getNumeriUsers().getNumeriUsers()) {
            if (target.getId() == numeriUser.getAccessToken().getUserId()) {
                ToastSender.sendToast(source.getScreenName() + "さんに" + target.getScreenName() + "のツイートがお気に入り登録されました");
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

    private void useApiFrequencyConfirmation() {

        CharSequence[] names = new CharSequence[Application.getInstance().getNumeriUsers().getNumeriUsers().size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = Application.getInstance().getNumeriUsers().getNumeriUsers().get(i).getScreenName();
        }
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("ユーザーを選択").setItems(names, (dialog, witch) -> {
            SimpleAsyncTask.backgroundExecute(() -> {
                try {
                    Map<String, RateLimitStatus> rateLimitStatus = Application.getInstance().getNumeriUsers().getNumeriUsers().get(witch)
                            .getTwitter().getRateLimitStatus("statuses");
                    String apiConfirmation = "get home_timeline API: remaining: " + rateLimitStatus.get("/statuses/home_timeline").getRemaining() + "\n";
                    apiConfirmation += "get mentions_timeline API: remaining: " + rateLimitStatus.get("/statuses/mentions_timeline").getRemaining() + "\n";
                    ToastSender.sendToast(apiConfirmation, Toast.LENGTH_LONG);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            });
        }).create();
        setCurrentShowDialog(alertDialog);
    }

    @Override
    public void finish() {
        Application.getInstance().finishMainActivity();
        super.finish();
    }
}
