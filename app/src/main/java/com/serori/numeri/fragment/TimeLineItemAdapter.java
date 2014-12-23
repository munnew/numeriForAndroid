package com.serori.numeri.fragment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.serori.numeri.R;

import java.util.List;

/**
 * Created by seroriKETC on 2014/12/21.
 */
public class TimeLineItemAdapter extends ArrayAdapter<TimeLineItem> {
    private LayoutInflater layoutInflater;

    public TimeLineItemAdapter(Context context, int resource, List<TimeLineItem> objects) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TimeLineItem timeLineItem = getItem(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_timeline, null);
            Log.v("TimeLineItemAdapter", "convert");

        }
        TextView screenNameTextView = (TextView)convertView.findViewById(R.id.timeLine_screenName);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.timeLine_name);
        TextView mainTextView = (TextView) convertView.findViewById(R.id.timeLine_maintext);
        TextView viaTextView = (TextView) convertView.findViewById(R.id.timeLine_via);
        TextView createdTime = (TextView) convertView.findViewById(R.id.createdDate);
        screenNameTextView.setText(timeLineItem.getScreenName());
        nameTextView.setText(timeLineItem.getName());
        mainTextView.setText(timeLineItem.getMainText());
        viaTextView.setText(timeLineItem.getVia());
        createdTime.setText(timeLineItem.getcreatedTime());
        return convertView;
    }

}
