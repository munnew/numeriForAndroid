package com.serori.numeri.temp.activity;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;


/**
 */
public class DialogActivity extends AppCompatActivity {
    private AlertDialog currentShowDialog = null;

    @Override
    protected void onStop() {
        if (currentShowDialog != null) {
            if (currentShowDialog.isShowing()) {
                currentShowDialog.hide();
            } else {
                currentShowDialog = null;
            }
        }
        super.onStop();
    }

    @Override
    protected void onRestart() {
        if (currentShowDialog != null) {
            currentShowDialog.show();
        }
        super.onRestart();
    }

    /**
     * Dialogをセットすると同時に表示します<br>
     * このメソッドを使用してDialogを表示した場合、そのDialogのライフサイクルはActivityのライフサイクルに準じます
     *
     * @param dialog セットするダイアログ
     */
    public void setCurrentShowDialog(AlertDialog dialog) {
        runOnUiThread(dialog::show);
        currentShowDialog = dialog;
    }

    @Override
    protected void onDestroy() {
        if (currentShowDialog != null) {
            currentShowDialog.dismiss();
        }

        currentShowDialog = null;
        super.onDestroy();
    }
}
