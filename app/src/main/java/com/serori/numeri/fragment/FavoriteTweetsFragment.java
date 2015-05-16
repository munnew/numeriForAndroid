package com.serori.numeri.fragment;

import android.util.Log;

import com.serori.numeri.fragment.NumeriFragment;
import com.serori.numeri.twitter.SimpleTweetStatus;
import com.serori.numeri.util.async.SimpleAsyncTask;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 */
public class FavoriteTweetsFragment extends NumeriFragment {
    private long userId = 0;

    @Override
    protected void initializeLoad() {
        if (userId > 0) {
            Log.v(toString(), "init");
            new SimpleAsyncTask<Twitter, ResponseList<Status>>() {

                @Override
                protected ResponseList<twitter4j.Status> doInBackground(Twitter twitter) {
                    try {
                        Log.v(toString(), "startAsyncTask");
                        return twitter.getFavorites(userId);
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(ResponseList<twitter4j.Status> statuses) {
                    if (statuses != null) {
                        Log.v(toString(), "onPost");
                        for (twitter4j.Status status : statuses) {
                            getAdapter().add(SimpleTweetStatus.build(status, getNumeriUser()));
                        }
                    }
                }
            }.execute(getNumeriUser().getTwitter());

        }
    }

    @Override
    protected void onAttachedBottom() {
        getTimelineListView().onAttachedBottomCallbackEnabled(false);
        new SimpleAsyncTask<Twitter, ResponseList<Status>>() {

            @Override
            protected ResponseList<twitter4j.Status> doInBackground(Twitter twitter) {
                try {
                    Paging paging = new Paging();
                    paging.setCount(21);
                    paging.setMaxId(getAdapter().getItem(getAdapter().getCount() - 1).getStatusId());
                    return twitter.getFavorites(userId, paging);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ResponseList<twitter4j.Status> statuses) {
                if (statuses != null) {
                    if (!statuses.isEmpty())
                        statuses.remove(0);
                    for (twitter4j.Status status : statuses) {
                        getAdapter().add(SimpleTweetStatus.build(status, getNumeriUser()));
                    }
                }
                getTimelineListView().onAttachedBottomCallbackEnabled(true);
            }
        }.execute(getNumeriUser().getTwitter());
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
