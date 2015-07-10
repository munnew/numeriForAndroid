package com.serori.numeri.twitter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;

import com.serori.numeri.R;
import com.serori.numeri.temp.activity.NumeriActivity;
import com.serori.numeri.temp.activity.SubsidiaryActivity;
import com.serori.numeri.listview.TimeLineListView;
import com.serori.numeri.listview.item.TimeLineItemAdapter;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.util.twitter.TwitterExceptionDisplay;

import java.util.ArrayList;
import java.util.List;

import twitter4j.TwitterException;

/**
 * 会話を表示するためのActivity
 */
public class ConversationActivity extends SubsidiaryActivity {

    private TimeLineListView conversationListView;
    private TimeLineItemAdapter adapter;
    private static NumeriUser numeriUser = null;
    private static long nextStatusId;

    public static void show(Context activityContext, NumeriUser numeriUser, long statusId) {
        ConversationActivity.numeriUser = numeriUser;
        ConversationActivity.nextStatusId = statusId;
        if (activityContext instanceof NumeriActivity) {
            ((NumeriActivity) activityContext).startActivity(ConversationActivity.class, false);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.in_right, R.anim.out_left);
        setContentView(R.layout.activity_display_conversation);
        if (numeriUser == null) {
            throw new NullPointerException("numeriUserがセットされていません");
        }

        if (savedInstanceState == null) {
            List<SimpleTweetStatus> simpleTweetStatuses = new ArrayList<>();
            adapter = new TimeLineItemAdapter(this, 0, simpleTweetStatuses);
            conversationListView = (TimeLineListView) findViewById(R.id.conversationListView);

            conversationListView.setAdapter(adapter);

            AsyncTask.execute(() -> {
                boolean successfulCompletion = true;
                while (nextStatusId != -1) {
                    try {
                        SimpleTweetStatus status = SimpleTweetStatus.showStatus(nextStatusId, numeriUser);

                        if (status == null || adapter == null) {
                            successfulCompletion = false;
                            break;
                        }
                        runOnUiThread(() -> adapter.add(status));
                        nextStatusId = status.getInReplyToStatusId();
                    } catch (TwitterException e) {
                        TwitterExceptionDisplay.show(e);
                        e.printStackTrace();
                        break;
                    }
                }
                if (successfulCompletion) {
                    conversationListView.setNumeriUser(numeriUser);
                }

            });
        }

    }

    @Override
    public void finish() {
        numeriUser = null;
        nextStatusId = -1;
        super.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            numeriUser = null;
        }
        return super.onKeyDown(keyCode, event);
    }
}
