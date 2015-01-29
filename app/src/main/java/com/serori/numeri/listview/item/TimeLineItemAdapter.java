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
import com.serori.numeri.color.ColorStorager;
import com.serori.numeri.config.ConfigurationStorager;
import com.serori.numeri.imageview.NumeriImageView;

import java.util.List;

public class TimeLineItemAdapter extends ArrayAdapter<TimeLineItem> {
    private LayoutInflater layoutInflater;
    private Context context;
    private int resource;
    private List<TimeLineItem> objects;
    private View currentVeiw;

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
        float textSize = ConfigurationStorager.NumericalConfigurations.CHARACTER_SIZE.getNumericValue() + 8;
        TextView waitImageLoadingBar = (TextView) convertView.findViewById(R.id.waitImageLoading);
        NumeriImageView iconImageView = (NumeriImageView) convertView.findViewById(R.id.userIcon);

        TextView screenNameTextView = (TextView) convertView.findViewById(R.id.timeLine_screenName);
        screenNameTextView.setTextColor(Color.parseColor(ColorStorager.Colors.CHARACTER.getColor()));
        screenNameTextView.setTextSize(textSize * (float) 0.8);

        TextView nameTextView = (TextView) convertView.findViewById(R.id.timeLine_name);
        nameTextView.setTextColor(Color.parseColor(ColorStorager.Colors.CHARACTER.getColor()));
        nameTextView.setTextSize(textSize * (float) 0.8);

        TextView mainTextView = (TextView) convertView.findViewById(R.id.timeLine_maintext);
        mainTextView.setTextColor(Color.parseColor(ColorStorager.Colors.CHARACTER.getColor()));
        mainTextView.setTextSize(textSize);

        TextView viaTextView = (TextView) convertView.findViewById(R.id.timeLine_via);
        viaTextView.setTextColor(Color.parseColor(ColorStorager.Colors.CHARACTER.getColor()));
        viaTextView.setTextSize(8);

        TextView createdTime = (TextView) convertView.findViewById(R.id.createdDate);
        createdTime.setTextColor(Color.parseColor(ColorStorager.Colors.CHARACTER.getColor()));
        createdTime.setTextSize(8);

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
            isMyTweetState.setBackgroundColor(Color.parseColor(ColorStorager.Colors.MYTWEET_MARK.getColor()));
        } else {
            isMyTweetState.setBackgroundColor(Color.parseColor("#00000000"));
        }

        if (timeLineItem.isRetweeted()) {
            convertView.setBackgroundColor(Color.parseColor(ColorStorager.Colors.RT_ITEM.getColor()));
            return convertView;
        }

        if (timeLineItem.isMention()) {
            convertView.setBackgroundColor(Color.parseColor(ColorStorager.Colors.MENTION_ITEM.getColor()));
            return convertView;
        }

        convertView.setBackgroundColor(Color.parseColor(ColorStorager.Colors.NORMAL_ITEM.getColor()));

        return convertView;
    }

    public View getCurrentVeiw() {
        return currentVeiw;
    }

    public void setCurrentVeiw(View currentVeiw) {
        this.currentVeiw = currentVeiw;
    }
}
