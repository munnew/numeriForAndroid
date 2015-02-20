package com.serori.numeri.twitter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;

import com.serori.numeri.R;
import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.listview.NumeriListView;
import com.serori.numeri.listview.item.TimeLineItemAdapter;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.util.twitter.TwitterExceptionDisplay;

import java.util.ArrayList;
import java.util.List;

import twitter4j.TwitterException;

/**
 * 会話を表示するためのActivity
 */
public class ConversationActivity extends NumeriActivity {

    private NumeriListView conversationListView;
    private TimeLineItemAdapter adapter;
    private static NumeriUser numeriUser = null;
    private static long nextStatusId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_conversation);
        if (numeriUser == null) {
            throw new NullPointerException("numeriUserがセットされていません");
        }

        if (savedInstanceState == null) {
            List<SimpleTweetStatus> simpleTweetStatuses = new ArrayList<>();
            adapter = new TimeLineItemAdapter(this, 0, simpleTweetStatuses);
            conversationListView = (NumeriListView) findViewById(R.id.conversationListView);

            NumeriUser user = numeriUser;
            conversationListView.setAdapter(adapter);
            AsyncTask.execute(() -> {
                while (nextStatusId != -1) {
                    try {
                        SimpleTweetStatus status = SimpleTweetStatus.showStatus(nextStatusId, numeriUser);
                        runOnUiThread(() -> adapter.add(status));
                        nextStatusId = status.getInReplyToStatusId();
                    } catch (TwitterException e) {
                        TwitterExceptionDisplay.show(e);
                        e.printStackTrace();
                        break;
                    }
                }
                conversationListView.onTouchItemEnabled(user, this);
                conversationListView.startObserveFavorite(user);
            });
        }

    }

    /**
     * 会話を表示するためのstatusIdをセットします。
     *
     * @param statusId
     */
    public static void setConversationStatusId(long statusId) {
        nextStatusId = statusId;
    }

    /**
     * ユーザーをセットします。
     * このActivityに遷移する前に必ずセットしてください。
     *
     * @param user
     */
    public static void setNumeriUser(NumeriUser user) {
        numeriUser = user;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            numeriUser = null;
            finish();
            return true;
        }
        return false;
    }
}
