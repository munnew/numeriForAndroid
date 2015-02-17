package com.serori.numeri.util;

import android.os.AsyncTask;

/**
 */
public abstract class SimpleAsyncTask<Param, Result> extends AsyncTask<Param, Void, Result> {
    @SuppressWarnings("unchecked")
    @Override
    protected Result doInBackground(Param... params) {
        return doInBackground(params[0]);
    }

    protected abstract Result doInBackground(Param param);

    @Override
    protected abstract void onPostExecute(Result result);


}
