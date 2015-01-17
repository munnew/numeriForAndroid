package com.serori.numeri.application;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

import com.serori.numeri.main.MainActivity;
import com.serori.numeri.main.OnToast;

/**
 * Application
 */
public class Application extends android.app.Application {
    private Application() {
    }

    public static Application getInstance() {
        return ApplicationHolder.instance;
    }

    private Context applicationContext;
    private Context mainActivityContext;
    private OnToast onToastListener;
    private static final String DB_NAME = "numeri.db";

    public Context getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(Context applicationContext) {
        if (applicationContext instanceof android.app.Application) {
            this.applicationContext = applicationContext;

        } else {
            throw new IllegalArgumentException("Applicationを継承していないインスタンスを保存しようとしています");
        }
    }

    public Context getMainActivityContext() {
        return mainActivityContext;
    }

    public void setMainActivityContext(Context mainActivityContext) {
        if (mainActivityContext instanceof MainActivity) {
            this.mainActivityContext = mainActivityContext;
        } else {
            throw new IllegalArgumentException("MainActivityでないインスタンスを保存しようとしています");
        }
    }

    public void destroyMainActivity() {
        if (mainActivityContext != null) {
            if (!isDestroyMainActivity()) {
                ((Activity) mainActivityContext).finish();
            }
        }
    }

    public boolean isDestroyMainActivity() {
        return ((Activity) mainActivityContext).isFinishing();
    }

    public NumeriUsers getNumeriUsers() {
        return NumeriUsers.getInstance();
    }

    public NumeriFragmentManager getNumeriFragmentManager() {
        return NumeriFragmentManager.getInstance();

    }

    public void setOnToastListener(OnToast listener) {
        onToastListener = listener;
    }

    public void onToast(String text, int length) {
        onToastListener.onToast(text, length);
    }

    public String getDbName() {
        return DB_NAME;
    }

    public int getWindowX() {
        WindowManager wm = (WindowManager) mainActivityContext.getSystemService(WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        return size.x;
    }

    private static class ApplicationHolder {
        private static final Application instance = new Application();
    }

}
