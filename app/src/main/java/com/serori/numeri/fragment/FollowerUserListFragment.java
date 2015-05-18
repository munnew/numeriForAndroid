package com.serori.numeri.fragment;

import android.os.Handler;

import com.serori.numeri.fragment.listview.item.UserListItem;
import com.serori.numeri.util.twitter.TwitterExceptionDisplay;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Friendship;
import twitter4j.PagableResponseList;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 */
public class FollowerUserListFragment extends UserListFragment {

    @Override
    protected void initializeLoad() {
        getFollowers(getCursorHolder().getNextCursor());
    }

    @Override
    void onAttachedBottom() {
        getFollowers(getCursorHolder().getNextCursor());
    }

    private void getFollowers(long cursor) {

        getUserListView().onAttachedBottomCallbackEnabled(false);
        if (cursor != 0) {
            Handler handler = new Handler();
            new Thread(() -> {
                PagableResponseList<User> users = getPagableUserList(cursor);
                handler.post(() -> {
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
                });
            }).start();
        }
    }

    private void getRelationships(List<UserListItem> userListItems) {
        Handler handler = new Handler();
        new Thread(() -> {
            ResponseList<Friendship> friendships = getFriendships(userListItems);
            handler.post(() -> {
                if (friendships != null) {
                    for (int i = 0; i < friendships.size(); i++) {
                        if (!userListItems.get(i).isShowedRelation())
                            userListItems.get(i).setRelationship(friendships.get(i));
                    }
                }
            });
        }).start();
    }

    private PagableResponseList<User> getPagableUserList(long cursor) {
        PagableResponseList<User> followers = null;
        try {
            followers = getNumeriUser().getTwitter().getFollowersList(getUserId(), cursor, 100);
        } catch (TwitterException e) {
            TwitterExceptionDisplay.show(e);
        }
        return followers;
    }

    private ResponseList<Friendship> getFriendships(List<UserListItem> userListItems) {
        long userIds[] = new long[userListItems.size()];
        for (int i = 0; i < userListItems.size(); i++) {
            userIds[i] = userListItems.get(i).getUserId();
        }
        ResponseList<Friendship> friendships = null;
        try {
            friendships = getNumeriUser().getTwitter().lookupFriendships(userIds);
        } catch (TwitterException e) {
            TwitterExceptionDisplay.show(e);
        }
        return friendships;
    }

}
