package com.serori.numeri.main;


import android.util.Log;

import com.serori.numeri.color.ColorStorager;
import com.serori.numeri.config.ConfigurationStorager;
import com.serori.numeri.exceptionreport.ExceptionReportStorager;
import com.serori.numeri.listview.action.ActionStorager;
import com.serori.numeri.util.toast.ToastSender;

/**
 */
public class Application extends android.app.Application {
    public Application() {
        super();
    }

    private volatile boolean clashing = false;

    @Override
    public void onCreate() {
        Global.getInstance().setApplicationContext(getApplicationContext());
        ConfigurationStorager.getInstance().loadConfigurations();
        ActionStorager.getInstance().initializeActions();
        ColorStorager.getInstance().loadColor();
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            try {
                if (!clashing) {
                    clashing = true;
                    Log.v(toString(), "uncaughtException catch");
                    ExceptionReportStorager.getInstance().savExceptionReportTableTable(new Exception(throwable));
                }
            } finally {
                Log.v(toString(), "uncaughtException finally");
                uncaughtExceptionHandler.uncaughtException(thread, throwable);
            }
        });
        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        ToastSender.sendToast("onLowMemory");
        super.onLowMemory();
    }

}

