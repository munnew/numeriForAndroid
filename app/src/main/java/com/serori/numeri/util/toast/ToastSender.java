package com.serori.numeri.util.toast;

import android.widget.Toast;

import com.serori.numeri.main.Global;

/**
 * MainActivityのUIThread上でToastするクラス
 */
public class ToastSender {
    public static void sendToast(String text) {
        Global.getInstance().runOnUiThread(() -> Toast.makeText(Global.getInstance().getApplicationContext(), text, android.widget.Toast.LENGTH_SHORT).show());
    }

    public static void sendToast(String text, int length) {
        Global.getInstance().runOnUiThread(() -> Toast.makeText(Global.getInstance().getApplicationContext(), text, length).show());
    }
}
