package com.serori.numeri.fragment.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.serori.numeri.R;

import java.util.List;

/**
 * Created by serioriKETC on 2014/12/27.
 */
public class FragmentManagerItemAdapter extends ArrayAdapter<FragmentManagerItem> {
    private LayoutInflater layoutInflater;

    public FragmentManagerItemAdapter(Context context, int resource, List<FragmentManagerItem> objects) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FragmentManagerItem item = getItem(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_fragments_list, null);
        }
        TextView fragmentName = (TextView) convertView.findViewById(R.id.fragmentName);
        fragmentName.setText(item.getFragmentName());
        return convertView;
    }
}
