package com.serori.numeri.color;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.serori.numeri.R;
import com.serori.numeri.application.Application;
import com.serori.numeri.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by serioriKETC on 2014/12/30.
 */
public class ColorManagerActivity extends ActionBarActivity {
    private List<ColorManagerItem> colorManagerItems = new ArrayList<>();
    private ColorListAdapter adapter;

    private List<String> colorIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ListView colorListView;
        Button colorSaveButton;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_manager);

        colorIds.add(ColorStorager.NOMAL_ITEM);
        colorIds.add(ColorStorager.RT_ITEM);
        colorIds.add(ColorStorager.MENTION_ITEM);
        colorIds.add(ColorStorager.MYTWEET_MARK);

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
            Application.getInstance().destroyMainActivity();
            Application.getInstance().onToast("色設定を保存しました。", Toast.LENGTH_SHORT);
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (Application.getInstance().isDestroyMainActivity()) {
                startMainActivity(true);
            } else {
                finish();
            }
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
