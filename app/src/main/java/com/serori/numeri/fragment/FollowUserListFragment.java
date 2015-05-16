package com.serori.numeri.fragment;

import com.serori.numeri.fragment.listview.item.UserListItem;
import com.serori.numeri.util.async.SimpleAsyncTask;
import com.serori.numeri.util.twitter.TwitterExceptionDisplay;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Friendship;
import twitter4j.PagableResponseList;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 */
public class FollowUserListFragment extends UserListFragment {
    @Override
    protected void initializeLoad() {
        getFollows(getCursorHolder().getNextCursor());
    }

    @Override
    void onAttachedBottom() {
        getFollows(getCursorHolder().getNextCursor());
    }

    private void getFollows(long cursor) {
        getUserListView().onAttachedBottomCallbackEnabled(false);
        if (cursor != 0) {
            new SimpleAsyncTask<Long, PagableResponseList<User>>() {
                @Override
                protected PagableResponseList<User> doInBackground(Long cursor) {
                    PagableResponseList<User> followers = null;
                    try {
                        followers = getNumeriUser().getTwitter().getFriendsList(getUserId(), cursor, 50);
                    } catch (TwitterException e) {
                        TwitterExceptionDisplay.show(e);
                    }
                    return followers;
                }

                @Override
                protected void onPostExecute(PagableResponseList<User> users) {
                    if (users != null && !users.isEmpty()) {
                        List<UserListItem> userListItems = new ArrayList<>();
                        for (User user : users) {
                            UserListItem userListItem = new UserListItem(user, getNumeriUser());
                            getAdapter().add(userListItem);
                            userListItems.add(userListItem);
                        }
                        getCursorHolder().setNextCursor(users.getNextCursor());
                        getRelationships(userListItems);
                    }
                    getUserListView().onAttachedBottomCallbackEnabled(true);

                }
            }.execute(cursor);
        }
    }

    private void getRelationships(List<UserListItem> userListItems) {
        long userIds[] = new long[userListItems.size()];
        for (int i = 0; i < userListItems.size(); i++) {
            userIds[i] = userListItems.get(i).getUserId();
        }
        new SimpleAsyncTask<Twitter, ResponseList<Friendship>>() {

            @Override
            protected ResponseList<Friendship> doInBackground(Twitter twitter) {
                ResponseList<Friendship> friendships = null;
                try {
                    friendships = twitter.lookupFriendships(userIds);
                } catch (TwitterException e) {
                    TwitterExceptionDisplay.show(e);
                }
                return friendships;
            }

            @Override
            protected void onPostExecute(ResponseList<Friendship> friendships) {
                if (friendships != null) {
                    for (int i = 0; i < friendships.size(); i++) {
                        userListItems.get(i).setRelationship(friendships.get(i));
                    }
                }
            }
        }.execute(getNumeriUser().getTwitter());
    }
}
