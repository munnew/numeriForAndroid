package com.serori.numeri.main;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

/**
 * Applicationのフィールド的な役割を持つクラス
 */
public class Application extends android.app.Application {
    private Application() {
    }

    public static Application getInstance() {
        return ApplicationHolder.instance;
    }

    private Context applicationContext;
    private Context mainActivityContext;


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

    /**
     * 現在のMainActivityを殺す
     */
    public void destroyMainActivity() {
        if (mainActivityContext != null) {
            if (!isDestroyMainActivity()) {
                ((Activity) mainActivityContext).finish();
            }
        }
    }

    /**
     * MainActivityのUIThreadで実行する
     *
     * @param runnable Runnable
     */
    public void runOnUiThread(Runnable runnable) {
        ((Activity) mainActivityContext).runOnUiThread(runnable);
    }

    /**
     * 今のMainActivityが生きているかどうか
     *
     * @return true : 生きている false : 死んでいる
     */
    public boolean isDestroyMainActivity() {
        return ((Activity) mainActivityContext).isFinishing();
    }

    public NumeriUsers getNumeriUsers() {
        return NumeriUsers.getInstance();
    }

    public Point getWindowSize() {
        WindowManager wm = (WindowManager) mainActivityContext.getSystemService(WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        return size;
    }

    private static class ApplicationHolder {
        private static final Application instance = new Application();
    }

}
