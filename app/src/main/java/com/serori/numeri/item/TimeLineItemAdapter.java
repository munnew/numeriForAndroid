package com.serori.numeri.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.serori.numeri.R;
import com.serori.numeri.util.cache.IconCache;

import java.util.ArrayList;
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
        return convertView;
    }

}
