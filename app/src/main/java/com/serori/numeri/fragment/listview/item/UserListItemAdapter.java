package com.serori.numeri.fragment.listview.item;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.serori.numeri.R;
import com.serori.numeri.config.ConfigurationStorager;
import com.serori.numeri.imageview.NumeriImageView;
import com.serori.numeri.userprofile.UserInformationActivity;

import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;


/**
 */
public class UserListItemAdapter extends ArrayAdapter<UserListItem> {
    private LayoutInflater layoutInflater;

    public UserListItemAdapter(Context context, int resource, List<UserListItem> objects) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserListItem userListItem = getItem(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_userlist, null);
        }
        float textSize = ConfigurationStorager.NumericalConfigurations.CHARACTER_SIZE.getNumericValue() + 8;
        NumeriImageView iconImageView = (NumeriImageView) convertView.findViewById(R.id.userIcon);
        TextView userScreenNameTextView = (TextView) convertView.findViewById(R.id.userScreenName);
        userScreenNameTextView.setTextSize(textSize * (float) 0.8);
        TextView userNameTextView = (TextView) convertView.findViewById(R.id.userName);
        userNameTextView.setTextSize(textSize * (float) 0.8);
        TextView bioTextView = (TextView) convertView.findViewById(R.id.bio);
        bioTextView.setTextSize(textSize);
        Button followButton = (Button) convertView.findViewById(R.id.followButton);
        followButton.setOnClickListener(null);
        followButton.setText("");
        followButton.setBackgroundColor(getContext().getResources().getColor(R.color.not_selected_color));
        TextView relationIndicator = (TextView) convertView.findViewById(R.id.relationIndicator);
        relationIndicator.setText("");
        boolean useHighResolution = ConfigurationStorager.EitherConfigurations.USE_HIGH_RESOLUTION_ICON.isEnabled();
        String iconUrl = useHighResolution ? userListItem.getBiggerIconImageUrl() : userListItem.getIconImageUrl();
        iconImageView.startLoadImage(true, NumeriImageView.ProgressType.LOAD_ICON, iconUrl);
        userScreenNameTextView.setText(userListItem.getUserScreenName());
        userNameTextView.setText(userListItem.getUserName());
        bioTextView.setText(userListItem.getBio());


        if (userListItem.isMe()) {
            followButton.setText("");
            followButton.setBackgroundColor(getContext().getResources().getColor(R.color.not_selected_color));
            relationIndicator.setText("自分");
            return convertView;
        }
        convertView.setOnTouchListener((v1, event) -> onTouchEvent(v1, event, userListItem));
        if (userListItem.isShowedRelation()) {
            Log.v(toString(), "setRelation");
            if (userListItem.isFollow()) {
                followButton.setText("フォロー解除");
                followButton.setBackgroundColor(getContext().getResources().getColor(R.color.un_follow_color));
            } else {
                followButton.setText("フォローする");
                followButton.setBackgroundColor(getContext().getResources().getColor(R.color.follow_color));
            }
            relationIndicator.setText(userListItem.getRelationship());
            followButton.setOnClickListener(v -> updateFriendship(userListItem));
        }
        return convertView;
    }


    private boolean onTouchEvent(View view, MotionEvent event, UserListItem userListItem) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                view.findViewById(R.id.overlay).setBackgroundColor(getContext().getResources().getColor(R.color.touched));
                break;
            case MotionEvent.ACTION_UP:
                view.findViewById(R.id.overlay).setBackgroundColor(getContext().getResources().getColor(R.color.transparency));
                UserInformationActivity.show(getContext(), userListItem.getUserId(), userListItem.getNumeriUser());
            default:
                view.findViewById(R.id.overlay).setBackgroundColor(getContext().getResources().getColor(R.color.transparency));
        }
        return true;
    }

    public void updateFriendship(UserListItem userListItem) {
        Twitter twitter = userListItem.getNumeriUser().getTwitter();
        new Thread(() -> {
            try {
                if (userListItem.isFollow()) {
                    twitter.destroyFriendship(userListItem.getUserId());
                } else {
                    twitter.createFriendship(userListItem.getUserId());
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }).start();

    }
}