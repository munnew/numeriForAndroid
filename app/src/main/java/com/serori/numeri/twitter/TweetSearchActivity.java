package com.serori.numeri.twitter;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.serori.numeri.R;
import com.serori.numeri.temp.activity.SubsidiaryActivity;
import com.serori.numeri.config.ConfigurationStorager;
import com.serori.numeri.listview.TimeLineListView;
import com.serori.numeri.listview.item.TimeLineItemAdapter;
import com.serori.numeri.main.Global;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.util.toast.ToastSender;
import com.serori.numeri.util.twitter.TwitterExceptionDisplay;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;

/**
 */
public class TweetSearchActivity extends SubsidiaryActivity implements SearchView.OnQueryTextListener {

    private static int selectedSearcherIndex = 0;
    private TimeLineItemAdapter searchResultAdapter;
    private TimeLineListView searchResultListView;
    private List<SimpleTweetStatus> searchResults = new ArrayList<>();
    private String queries[];
    private boolean enableAttachedBottom = true;
    private SearchView tweetSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.in_right, R.anim.out_left);
        setContentView(R.layout.activity_tweet_search);
        searchResultListView = (TimeLineListView) findViewById(R.id.list_search_result);
        searchResultAdapter = new TimeLineItemAdapter(this, 0, searchResults);
        searchResultListView.setAdapter(searchResultAdapter);
        NumeriUser searcher = Global.getInstance().getNumeriUsers().getNumeriUsers().get(selectedSearcherIndex);
        searchResultListView.setNumeriUser(searcher);

        searchResultListView.setAttachedBottomListener(this::onAttachedBottom);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tweet_search, menu);
        MenuItem item = menu.findItem(R.id.action_tweet_search);
        tweetSearchView = (SearchView) item.getActionView();
        initTweetSearchView(tweetSearchView);
        initOptionsMenu(menu);
        return true;
    }

    private void initOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.select_user);
        Menu subMenu = menuItem.getSubMenu();
        List<NumeriUser> numeriUsers = Global.getInstance().getNumeriUsers().getNumeriUsers();
        for (int i = 0; i < numeriUsers.size(); i++) {
            subMenu.add(0, i, i, numeriUsers.get(i).getScreenName());
            subMenu.getItem(i).setCheckable(true);
            int j = i;
            subMenu.getItem(i).setOnMenuItemClickListener(item -> {
                item.setChecked(true);
                if (selectedSearcherIndex != j) {
                    searchResultAdapter.clear();
                    selectedSearcherIndex = j;
                    NumeriUser searcher = Global.getInstance().getNumeriUsers().getNumeriUsers().get(j);
                    searchResultListView.setNumeriUser(searcher);
                }
                return true;
            });
        }
        subMenu.setGroupCheckable(0, true, true);
        subMenu.getItem(selectedSearcherIndex).setChecked(true);
    }

    private void initTweetSearchView(SearchView searchView) {
        searchView.setSubmitButtonEnabled(false);
        searchView.setIconifiedByDefault(true);
        searchView.setQuery("", false);
        searchView.setQueryHint(getResources().getString(R.string.tweet_search_query_hint));
        searchView.setOnQueryTextListener(this);
    }

    private void searchExecute(String[] queries, boolean isBelow) {
        Query query = new Query();
        query.setCount(30);
        for (String s : queries) {
            query.setQuery(s);
        }
        if (isBelow) {
            int count = searchResultAdapter.getCount();
            if (count > 0)
                query.setMaxId(searchResultAdapter.getItem(count - 1).getStatusId());
        } else {
            searchResultAdapter.clear();
        }
        NumeriUser searcher = Global.getInstance().getNumeriUsers().getNumeriUsers().get(selectedSearcherIndex);
        Handler handler = new Handler();
        new Thread(() -> {
            try {

                QueryResult queryResult = searcher.getTwitter().search(query);
                handler.post(() -> {
                    List<Status> statuses = queryResult.getTweets();
                    if (statuses.isEmpty() && !isBelow) {
                        ToastSender.sendToast("以上の条件で検索しても見つかりませんでした");
                    } else if (!statuses.isEmpty() && isBelow) {
                        statuses.remove(0);
                    }
                    for (Status status : queryResult.getTweets()) {
                        searchResultAdapter.add(SimpleTweetStatus.build(status, searcher));
                    }
                });
            } catch (TwitterException e) {
                TwitterExceptionDisplay.show(e);
            }
        }).start();

    }

    private void onAttachedBottom() {
        if (!ConfigurationStorager.EitherConfigurations.CONFIRMATION_LESS_GET_TWEET.isEnabled()) {
            if (enableAttachedBottom) {
                enableAttachedBottom = false;
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setMessage("更にツイートを読み込みますか？")
                        .setPositiveButton("はい", (dialog, which) -> {
                            enableAttachedBottom = true;
                            searchExecute(queries, true);
                        }).setNegativeButton("キャンセル", (dialog, which) -> {
                            enableAttachedBottom = true;
                        })
                        .setOnDismissListener(dialog -> enableAttachedBottom = true)
                        .create();
                setCurrentShowDialog(alertDialog);
            }
        } else {
            searchExecute(queries, true);
        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        queries = query.split("[ 　]+");
        searchExecute(queries, false);
        tweetSearchView.clearFocus();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //none
        return false;
    }
}
