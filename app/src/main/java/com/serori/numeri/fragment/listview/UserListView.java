package com.serori.numeri.fragment.listview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

import com.serori.numeri.R;
import com.serori.numeri.fragment.listview.item.OnUpdateRelationshipListener;
import com.serori.numeri.fragment.listview.item.UserListItem;
import com.serori.numeri.util.async.SimpleAsyncTask;

import twitter4j.Twitter;
import twitter4j.TwitterException;


/**
 */
public class UserListView extends AttachedBottomCallBackListView implements OnUpdateRelationshipListener {


    public UserListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        UserListItem.addOnUpdateRelationshipListener(this);
    }


    @Override
    public void onUpdateRelationship(long userId, boolean isFollow, String relationship) {
        ((Activity) getContext()).runOnUiThread(() -> {
            int visibleItemCount = getVisibleItemCount();
            int firstVisibleItemPosition = getFirstVisiblePosition();
            for (int i = 0; i < visibleItemCount; i++) {
                int index = firstVisibleItemPosition + i;
                UserListItem item = (UserListItem) getAdapter().getItem(index);
                if (item.getUserId() == userId) {
                    Button followButton = (Button) getChildAt(i).findViewById(R.id.followButton);
                    TextView relationshipIndicator = (TextView) getChildAt(i).findViewById(R.id.relationIndicator);
                    if (isFollow) {
                        followButton.setBackgroundColor(getResources().getColor(R.color.un_follow_color));
                        followButton.setText("フォロー解除");
                    } else {
                        followButton.setBackgroundColor(getResources().getColor(R.color.follow_color));
                        followButton.setText("フォローする");
                    }
                    relationshipIndicator.setText(relationship);
                    followButton.setOnClickListener(v -> updateFriendship(item));
                }
            }
        });
    }

    public void updateFriendship(UserListItem userListItem) {
        Twitter twitter = userListItem.getNumeriUser().getTwitter();
        SimpleAsyncTask.execute(() -> {
            try {
                if (userListItem.isFollow()) {
                    twitter.destroyFriendship(userListItem.getUserId());
                } else {
                    twitter.createFriendship(userListItem.getUserId());
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        UserListItem.removeOnUpdateRelationshipListener(this);
        super.onDetachedFromWindow();
    }
}
