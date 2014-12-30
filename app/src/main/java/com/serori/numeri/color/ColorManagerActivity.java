package com.serori.numeri.color;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ListView;

import com.serori.numeri.R;
import com.serori.numeri.item.TimeLineItem;
import com.serori.numeri.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by serioriKETC on 2014/12/30.
 */
public class ColorManagerActivity extends Activity {
    private ListView colorListView;
    private List<ColorManagerItem> colorManagerItems = new ArrayList<>();
    private ColorListAdapter adapter;
    private Button colorSaveButton;
    private List<String> colorIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_manager);

        colorIds.add(ColorStorager.NOMAL_ITEM);
        colorIds.add(ColorStorager.RT_ITEM);
        colorIds.add(ColorStorager.MENTION_ITEM);

        colorSaveButton = (Button) findViewById(R.id.colorSave);
        colorListView = (ListView) findViewById(R.id.ColorList);
        adapter = new ColorListAdapter(this, 0, colorManagerItems);
        colorListView.setAdapter(adapter);
        for (String colorId : colorIds) {
            ColorManagerItem item = new ColorManagerItem(colorId);
            item.setColor(ColorStorager.getInstance().loadColorForId(colorId));
            adapter.add(item);
        }
        colorSaveButton.setOnClickListener(v -> {
            for (int i = 0; i < adapter.getCount(); i++) {
                ColorManagerItem item = adapter.getItem(i);
                ColorStorager.ColorData data = new ColorStorager.ColorData(item.getColorId(), item.getColor());
                ColorStorager.getInstance().saveColorData(data);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startMainActivity(true);
            return true;
        }
        return false;
    }

    private void startMainActivity(boolean isFinish) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }
}
