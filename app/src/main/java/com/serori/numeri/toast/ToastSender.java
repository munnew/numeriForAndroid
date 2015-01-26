package com.serori.numeri.toast;

import android.widget.Toast;

import com.serori.numeri.application.Application;

/**
 */
public class ToastSender {
    private ToastSender() {
    }


    public static ToastSender getInstance() {
        return ToastSenderHolder.instance;
    }

    public void sendToast(String text) {
        Application.getInstance().runOnUiThread(() -> Toast.makeText(Application.getInstance().getApplicationContext(), text, android.widget.Toast.LENGTH_SHORT).show());
    }

    private static class ToastSenderHolder {
        private static final ToastSender instance = new ToastSender();
    }
}
