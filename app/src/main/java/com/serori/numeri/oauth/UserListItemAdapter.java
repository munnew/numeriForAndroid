package com.serori.numeri.oauth;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.serori.numeri.R;

import java.util.List;

/**
 * Created by seroriKETC on 2014/12/19.
 */
public class UserListItemAdapter extends ArrayAdapter<NumeriUserListItem>{
    private LayoutInflater layoutInflater;
    public UserListItemAdapter(Context context, int resource, List<NumeriUserListItem> objects) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NumeriUserListItem item = getItem(position);
        if(null == convertView){
            convertView = layoutInflater.inflate(R.layout.item_user,null);
        }
        TextView screenName = (TextView)convertView.findViewById(R.id.screenName);
        screenName.setText(item.getScreenName());
        Button userDeleteButton = (Button)convertView.findViewById(R.id.userDelete);
        userDeleteButton.setOnClickListener(view -> Log.v("UserListItemAdapter", "onDelete") );
        return convertView;
    }
}
