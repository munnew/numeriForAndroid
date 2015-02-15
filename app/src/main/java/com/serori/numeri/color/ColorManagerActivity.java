package com.serori.numeri.color;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ListView;

import com.serori.numeri.R;
import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.util.toast.ToastSender;

import java.util.ArrayList;
import java.util.List;

/**
 * ColorManagerActivity
 */
public class ColorManagerActivity extends NumeriActivity {
    private ColorListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ListView colorListView;
        Button colorSaveButton;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_manager);
        if (savedInstanceState == null) {
            List<ColorManagerItem> colorManagerItems = new ArrayList<>();
            colorSaveButton = (Button) findViewById(R.id.colorSave);
            colorListView = (ListView) findViewById(R.id.ColorList);
            adapter = new ColorListAdapter(this, 0, colorManagerItems);
            colorListView.setAdapter(adapter);
            for (Color color : ColorStorager.Colors.values()) {
                ColorManagerItem item = new ColorManagerItem(color);
                adapter.add(item);
            }
            colorSaveButton.setOnClickListener(v -> {
                for (int i = 0; i < adapter.getCount(); i++) {
                    ColorManagerItem item = adapter.getItem(i);
                    item.getColor().setColor(item.getColorValue());
                    ColorStorager.getInstance().saveColorData();
                }
                ToastSender.sendToast("色設定を保存しました。");
            });

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }


}
