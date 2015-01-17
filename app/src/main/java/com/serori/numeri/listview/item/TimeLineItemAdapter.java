package com.serori.numeri.listview.item;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.serori.numeri.R;
import com.serori.numeri.color.Colors;
import com.serori.numeri.imageview.NumeriImageView;

import java.util.List;

public class TimeLineItemAdapter extends ArrayAdapter<TimeLineItem> {
    private LayoutInflater layoutInflater;
    private Context context;
    private int resource;
    private List<TimeLineItem> objects;

    public TimeLineItemAdapter(Context context, int resource, List<TimeLineItem> objects) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    public TimeLineItemAdapter clone() {
        return new TimeLineItemAdapter(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TimeLineItem timeLineItem = getItem(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_timeline, null);
        }
        ProgressBar waitImageLoadingBar = (ProgressBar) convertView.findViewById(R.id.waitImageLoading);
        NumeriImageView iconImageView = (NumeriImageView) convertView.findViewById(R.id.userIcon);
        TextView screenNameTextView = (TextView) convertView.findViewById(R.id.timeLine_screenName);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.timeLine_name);
        TextView mainTextView = (TextView) convertView.findViewById(R.id.timeLine_maintext);
        TextView viaTextView = (TextView) convertView.findViewById(R.id.timeLine_via);
        TextView createdTime = (TextView) convertView.findViewById(R.id.createdDate);
        ImageView favoriteStar = (ImageView) convertView.findViewById(R.id.favoriteStar);
        View isMyTweetState = convertView.findViewById(R.id.isMyTweetStateView);

        iconImageView.startLoadImage(waitImageLoadingBar, timeLineItem.getIconImageUrl());
        screenNameTextView.setText(timeLineItem.getScreenName());
        nameTextView.setText(timeLineItem.getName());
        mainTextView.setText(timeLineItem.getMainText());
        viaTextView.setText(timeLineItem.getVia());
        createdTime.setText(timeLineItem.getcreatedTime());
        if (timeLineItem.isFavorite()) {
            favoriteStar.setImageDrawable(getContext().getResources().getDrawable(R.drawable.favorite_star));
        } else {
            favoriteStar.setImageBitmap(null);
        }

        if (timeLineItem.isMyTweet()) {
            isMyTweetState.setBackgroundColor(Color.parseColor(Colors.getInstance().getMyTweetMarkColor()));
        } else {
            isMyTweetState.setBackgroundColor(Color.parseColor("#00000000"));
        }

        if (timeLineItem.isRetweeted()) {
            convertView.setBackgroundColor(Color.parseColor(Colors.getInstance().getRetweetColor()));
            return convertView;
        }

        if (timeLineItem.isMention()) {
            convertView.setBackgroundColor(Color.parseColor(Colors.getInstance().getMentionColor()));
            return convertView;
        }

        convertView.setBackgroundColor(Color.parseColor(Colors.getInstance().getNomalColor()));

        return convertView;
    }

}
