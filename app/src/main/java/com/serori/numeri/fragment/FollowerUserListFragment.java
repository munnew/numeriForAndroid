package com.serori.numeri.fragment;

import android.os.Handler;

import com.serori.numeri.fragment.listview.item.UserListItem;
import com.serori.numeri.util.twitter.TwitterExceptionDisplay;

import java.util.ArrayList;
import java.util.List;

import twitter4j.PagableResponseList;
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


    private PagableResponseList<User> getPagableUserList(long cursor) {
        PagableResponseList<User> followers = null;
        try {
            followers = getNumeriUser().getTwitter().getFollowersList(getUserId(), cursor, LOAD_USER_NUM);
        } catch (TwitterException e) {
            TwitterExceptionDisplay.show(e);
        }
        return followers;
    }


}
