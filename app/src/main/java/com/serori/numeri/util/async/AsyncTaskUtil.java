package com.serori.numeri.util.async;

import android.os.AsyncTask;


public class AsyncTaskUtil<Param, Result> {
    private BackgroundRunnable<Param, Result> backgroundRunnable;
    private OnPostUiThreadRunnable<Result> onPostUiThreadRunnable;
    private Param param;

    private AsyncTask<Param, Void, Result> previousRunAsync = null;

    public AsyncTaskUtil(Param param) {
        this.param = param;
    }

    public AsyncTaskUtil() {

    }

    public void setBackgroundRunnable(BackgroundRunnable<Param, Result> runnable) {
        backgroundRunnable = runnable;
    }

    public void setBackgroundRunnable(Param param, BackgroundRunnable<Param, Result> runnable) {
        this.param = param;
        backgroundRunnable = runnable;
    }

    public void setOnPostUiThreadRunnable(OnPostUiThreadRunnable<Result> runnable) {
        onPostUiThreadRunnable = runnable;
    }

    @SuppressWarnings("unchecked")
    public void execute() {
        previousRunAsync = new AsyncTask<Param, Void, Result>() {
            private boolean isRunning = false;

            @Override
            protected Result doInBackground(Param... params) {
                if (backgroundRunnable == null) {
                    throw new NullPointerException();
                }
                isRunning = true;
                return backgroundRunnable.run(params[0]);
            }

            @Override
            protected void onPostExecute(Result result) {
                if (onPostUiThreadRunnable != null) {
                    onPostUiThreadRunnable.run(result);
                    isRunning = false;
                }
            }

            public boolean isRunning() {
                return isRunning;
            }
        }.execute(param);
    }

    public AsyncTask<Param, Void, Result> getPreviousRunAsync() {
        return previousRunAsync;
    }


}

