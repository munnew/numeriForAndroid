package com.serori.numeri.oauth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.serori.numeri.Application.Application;
import com.serori.numeri.R;
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
public class OAuthActivity extends Activity {

    private ListView numeriUserListView;
    private List<NumeriUserListItem> userListItems = new ArrayList<>();
    private UserListItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);
        if (savedInstanceState == null) {
            numeriUserListView = (ListView) findViewById(R.id.numeriUserListView);
            adapter = new UserListItemAdapter(this, 0, userListItems);
            numeriUserListView.setAdapter(adapter);
            init();
        }


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
                    toast("失敗しました");
                }
            }
        };
        task.execute();
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intent.getData().getQueryParameter("oauth_verifier") == null||intent == null || intent.getData() == null || !intent.getData().toString().startsWith(getString(R.string.twitter_callback_url))) {
            toast("認証がキャンセルされました");
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
                    toast(token.getScreenName() + "認証");
                    NumeriUserStorager.NumeriUserTable userTable = new NumeriUserStorager.NumeriUserTable();
                    userTable.setAccessToken(token.getToken());
                    userTable.setAccessTokenSecret(token.getTokenSecret());
                    NumeriUserStorager.getInstance().saveNumeriUser(userTable);
                    addItem(token);
                } else {
                    toast("認証失敗");
                }
            }
        };
        task.execute(oauthVerifier);
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void addItem(AccessToken token) {
        NumeriUserListItem userListItem = new NumeriUserListItem();
        NumeriUser numeriUser = new NumeriUser(token);
        Application.getInstance().getNumeriUsers().addNumeriUser(numeriUser);
        userListItem.setScreenName(numeriUser.getAccessToken().getScreenName());
        adapter.add(userListItem);
        Log.v(token.getToken(), token.getTokenSecret());
        Log.v(token.getScreenName(), "" + token.getUserId());
    }

    private void init() {
        adapter.clear();
        List<NumeriUser> numeriUsers = new ArrayList<>();
        numeriUsers.addAll(Application.getInstance().getNumeriUsers().getNumeriUsers());
        Log.v("numeriUsersSize", "" + numeriUsers.size());
        if (!numeriUsers.isEmpty()) {
            List<NumeriUserListItem> listItems = new ArrayList<>();
            AsyncTask.execute(() -> {
                for (NumeriUser numeriUser : numeriUsers) {
                    try {
                        NumeriUserListItem item = new NumeriUserListItem();
                        item.setScreenName(numeriUser.getTwitter().getScreenName());
                        listItems.add(item);
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(() -> adapter.addAll(listItems));
            });

        }
    }


}


