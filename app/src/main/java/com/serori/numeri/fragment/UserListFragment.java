package com.serori.numeri.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.serori.numeri.R;
import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.fragment.listview.AttachedBottomListener;
import com.serori.numeri.fragment.listview.item.UserListItem;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.fragment.listview.item.UserListItemAdapter;
import com.serori.numeri.fragment.listview.UserListView;

import java.util.ArrayList;
import java.util.List;

/**
 */
public abstract class UserListFragment extends Fragment implements AttachedBottomListener {
    private NumeriUser numeriUser = null;
    private long userId = -1;
    private UserListItemAdapter userListItemAdapter;
    private UserListView userListView;
    private CursorHolder cursorHolder = new CursorHolder();
    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_userlist, container, false);
        if (numeriUser == null)
            return rootView;
        if (!(getActivity() instanceof NumeriActivity))
            throw new IllegalStateException("親のアクティビティがNumeriActivityを継承していません");
        userListView = (UserListView) rootView.findViewById(R.id.userList);
        if (savedInstanceState == null) {
            List<UserListItem> userListItems = new ArrayList<>();
            userListItemAdapter = new UserListItemAdapter(getActivity(), 0, userListItems);
            userListView.setAdapter(userListItemAdapter);
            initializeLoad();
        } else {
            userListView.setAdapter(userListItemAdapter);
        }
        userListView.setAttachedBottomListener(this);
        return rootView;
    }

    public void setNumeriUser(NumeriUser numeriUser) {
        this.numeriUser = numeriUser;
    }

    protected abstract void initializeLoad();

    public NumeriUser getNumeriUser() {
        return numeriUser;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }


    public long getUserId() {
        return userId;
    }

    public UserListItemAdapter getAdapter() {
        return userListItemAdapter;
    }

    abstract void onAttachedBottom();

    protected UserListView getUserListView() {
        return userListView;
    }

    @Override
    public void attachedBottom() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setMessage("さらにユーザーを読み込みますか？")
                .setNegativeButton("いいえ", (dialog, id) -> {
                })
                .setPositiveButton("はい", (dialog, id) -> {
                    onAttachedBottom();
                })
                .create();
        ((NumeriActivity) getActivity()).setCurrentShowDialog(alertDialog);
    }

    protected CursorHolder getCursorHolder() {
        return cursorHolder;
    }


    protected class CursorHolder {
        private long nextCursor = -1;

        private CursorHolder() {

        }

        public long getNextCursor() {
            return nextCursor;
        }

        public void setNextCursor(long nextCursor) {
            this.nextCursor = nextCursor;
        }
    }
}
