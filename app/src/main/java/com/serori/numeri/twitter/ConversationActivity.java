package com.serori.numeri.twitter;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.serori.numeri.R;
import com.serori.numeri.listview.NumeriListView;
import com.serori.numeri.listview.item.TimeLineItem;
import com.serori.numeri.listview.item.TimeLineItemAdapter;
import com.serori.numeri.user.NumeriUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by serioriKETC on 2015/01/07.
 */
public class ConversationActivity extends ActionBarActivity {
    private NumeriListView conversationListView;
    private static List<TimeLineItem> timeLineItems = new ArrayList<>();
    private TimeLineItemAdapter adapter;
    private static long statusId;
    private static NumeriUser numeriUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_conversation);
        adapter = new TimeLineItemAdapter(this, 0, timeLineItems);
        conversationListView = (NumeriListView) findViewById(R.id.conversationListView);
        conversationListView.setAdapter(adapter);

    }

    public static void setConversationItems(List<TimeLineItem> items) {
        timeLineItems.clear();
        timeLineItems.addAll(items);
    }

    public static void setNumeriUser(NumeriUser user) {
        numeriUser = user;
    }
}
