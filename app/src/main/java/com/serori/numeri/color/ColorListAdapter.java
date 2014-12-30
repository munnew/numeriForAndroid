package com.serori.numeri.color;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import com.serori.numeri.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by serioriKETC on 2014/12/30.
 */
public class ColorListAdapter extends ArrayAdapter<ColorManagerItem> {
    private LayoutInflater layoutInflater;

    public ColorListAdapter(Context context, int resource, List<ColorManagerItem> objects) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ColorManagerItem item = getItem(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_color_manager, null);
        }

        TextView colorId = (TextView) convertView.findViewById(R.id.colorId);
        colorId.setText(item.getColorId());
        SeekBar redSeekBar = (SeekBar) convertView.findViewById(R.id.redSeekBar);
        SeekBar greenSeekBar = (SeekBar) convertView.findViewById(R.id.greenSeekBar);
        SeekBar blueSeekBar = (SeekBar) convertView.findViewById(R.id.blueSeekBar);

        View colorView = convertView.findViewById(R.id.colorView);


        redSeekBar.setMax(255);
        greenSeekBar.setMax(255);
        blueSeekBar.setMax(255);

        List<Integer> rgbValue = purseToDecRgbVakue(item.getColor());
        redSeekBar.setProgress(rgbValue.get(0));
        greenSeekBar.setProgress(rgbValue.get(1));
        blueSeekBar.setProgress(rgbValue.get(2));
        Log.v("rgb", "" + rgbValue.get(0) + " " + rgbValue.get(1) + " " + rgbValue.get(2));
        colorView.setBackgroundColor(Color.argb(255, rgbValue.get(0), rgbValue.get(1), rgbValue.get(2)));
        redSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                colorView.setBackgroundColor(Color.argb(255, progress, greenSeekBar.getProgress(), blueSeekBar.getProgress()));
                item.setColor(purseToHexRgbValue(progress, greenSeekBar.getProgress(), blueSeekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        greenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                colorView.setBackgroundColor(Color.argb(255, redSeekBar.getProgress(), progress, blueSeekBar.getProgress()));
                item.setColor(purseToHexRgbValue(redSeekBar.getProgress(), progress, blueSeekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        blueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                colorView.setBackgroundColor(Color.argb(255, redSeekBar.getProgress(), greenSeekBar.getProgress(), progress));
                item.setColor(purseToHexRgbValue(redSeekBar.getProgress(), greenSeekBar.getProgress(), progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        return convertView;
    }

    private String purseToHexRgbValue(int r, int g, int b) {
        List<String> hex = new ArrayList<>();
        hex.add(Integer.toHexString(r));
        hex.add(Integer.toHexString(g));
        hex.add(Integer.toHexString(b));
        String hexRgbValue = "#";


        for (String s : hex) {
            if (s.length() == 1) {
                hexRgbValue += "0" + s;
            } else {
                hexRgbValue += s;
            }
        }

        return hexRgbValue;
    }

    private List<Integer> purseToDecRgbVakue(String hexRgbValue) {
        List<Integer> rgbValue = new ArrayList<>();
        rgbValue.add(Integer.parseInt(hexRgbValue.substring(1, 3), 16));
        rgbValue.add(Integer.parseInt(hexRgbValue.substring(3, 5), 16));
        rgbValue.add(Integer.parseInt(hexRgbValue.substring(5), 16));
        return rgbValue;
    }
}
