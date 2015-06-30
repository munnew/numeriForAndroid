package com.serori.numeri.oauth;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;

import com.serori.numeri.R;
import com.serori.numeri.activity.SubsidiaryActivity;
import com.serori.numeri.main.Global;
import com.serori.numeri.util.toast.ToastSender;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.user.NumeriUserStorager;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationContext;

/**
 * 認証用の画面
 */
public class OAuthActivity extends SubsidiaryActivity implements OnUserDeleteListener {
    private UserListItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);
        if (savedInstanceState == null) {
            List<NumeriUser> userListItems = new ArrayList<>();
            ListView numeriUserListView;
            numeriUserListView = (ListView) findViewById(R.id.numeriUserListView);
            adapter = new UserListItemAdapter(this, 0, userListItems);
            numeriUserListView.setAdapter(adapter);
            init();
            Log.v("Oauth", "create");
        }
        UserDeleteObserver.getInstance().setOnUserDeleteListener(this);
        Button addUserButton = (Button) findViewById(R.id.addUser);
        addUserButton.setOnClickListener(v -> oauthStart());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_oauth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_oauth) {
            Log.v("OAuthActivity", "oauthStart");
            oauthStart();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private OAuthAuthorization oauth;
    private RequestToken resultToken;

    private void oauthStart() {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    Configuration configuration = ConfigurationContext.getInstance();

                    oauth = new OAuthAuthorization(configuration);
                    oauth.setOAuthConsumer(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret));
                    resultToken = oauth.getOAuthRequestToken(getString(R.string.twitter_callback_url));
                    return resultToken.getAuthorizationURL();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String url) {
                if (url != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    ToastSender.sendToast("失敗しました");
                }
            }
        };
        task.execute();
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intent.getData().getQueryParameter("oauth_verifier") == null || intent.getData() == null || !intent.getData().toString().startsWith(getString(R.string.twitter_callback_url))) {
            ToastSender.sendToast("認証がキャンセルされました");
            return;
        }

        String oauthVerifier = intent.getData().getQueryParameter("oauth_verifier");
        AsyncTask<String, Void, AccessToken> task = new AsyncTask<String, Void, AccessToken>() {
            @Override
            protected AccessToken doInBackground(String... oauthVerifier) {
                try {
                    AccessToken token = oauth.getOAuthAccessToken(resultToken, oauthVerifier[0]);
                    Twitter twitter = new TwitterFactory().getInstance();
                    twitter.setOAuthConsumer(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret));
                    AccessToken accessToken = new AccessToken(token.getToken(), token.getTokenSecret());
                    twitter.setOAuthAccessToken(accessToken);
                    return token;
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(AccessToken token) {
                if (resultToken != null) {
                    Log.v("screenName", token.getScreenName());
                    ToastSender.sendToast(token.getScreenName() + "認証");
                    NumeriUserStorager.getInstance().addOnAddedNumeriUserListener(adapter::add);
                    NumeriUserStorager.NumeriUserTable userTable = new NumeriUserStorager.NumeriUserTable(token.getToken(), token.getTokenSecret(), token.getScreenName());
                    NumeriUserStorager.getInstance().addNumeriUserTable(userTable);
                } else {
                    ToastSender.sendToast("認証失敗");
                }
            }
        };
        task.execute(oauthVerifier);
    }


    private void init() {
        AsyncTask.execute(() -> {
            adapter.clear();
            List<NumeriUser> numeriUsers;
            numeriUsers = Global.getInstance().getNumeriUsers().getNumeriUsers();
            Log.v("numeriUsersSize", "" + numeriUsers.size());
            if (!numeriUsers.isEmpty()) {
                runOnUiThread(() -> adapter.addAll(numeriUsers));
            }
        });
    }


    @Override
    public void onUserDelete(int position) {
        NumeriUser numeriUser = Global.getInstance().getNumeriUsers().getNumeriUsers().get(position);
        Global.getInstance().getNumeriUsers().removeNumeriUser(numeriUser);
        adapter.remove(adapter.getItem(position));
        Global.getInstance().destroyMainActivity();
    }
}


