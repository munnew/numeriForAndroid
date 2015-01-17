package com.serori.numeri.twitter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;

import com.serori.numeri.R;
import com.serori.numeri.listview.NumeriListView;
import com.serori.numeri.listview.item.TimeLineItem;
import com.serori.numeri.listview.item.TimeLineItemAdapter;
import com.serori.numeri.user.NumeriUser;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.TwitterException;

/**
 * 会話を表示するためのActivity
 */
public class ConversationActivity extends ActionBarActivity {
    private NumeriListView conversationListView;
    private List<TimeLineItem> timeLineItems = new ArrayList<>();
    private TimeLineItemAdapter adapter;
    private static NumeriUser numeriUser;
    private static long nextStatusId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_conversation);
        adapter = new TimeLineItemAdapter(this, 0, timeLineItems);
        conversationListView = (NumeriListView) findViewById(R.id.conversationListView);
        if (numeriUser == null) {
            throw new NullPointerException("numeriUserがセットされていません");
        }
        conversationListView.onTouchItemEnabled(numeriUser, this);
        conversationListView.setAdapter(adapter);
        AsyncTask.execute(() -> {
            while (nextStatusId != -1) try {
                Status status = numeriUser.getTwitter().showStatus(nextStatusId);
                runOnUiThread(() -> adapter.add(new TimeLineItem(status, numeriUser)));
                nextStatusId = status.getInReplyToStatusId();
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        });

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
