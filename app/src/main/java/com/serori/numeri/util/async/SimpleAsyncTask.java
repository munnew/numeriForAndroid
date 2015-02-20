package com.serori.numeri.util.async;

import android.os.AsyncTask;

/**
 * ただonPostExecuteが自動で補完されるようにしたいが為に作成されたクラス
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
