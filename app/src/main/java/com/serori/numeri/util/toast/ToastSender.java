package com.serori.numeri.util.toast;

import android.widget.Toast;

import com.serori.numeri.main.Application;

/**
 */
public class ToastSender {
    public static void sendToast(String text) {
        Application.getInstance().runOnUiThread(() -> Toast.makeText(Application.getInstance().getApplicationContext(), text, android.widget.Toast.LENGTH_SHORT).show());
    }
}
