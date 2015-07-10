package com.serori.numeri.directmessage;

import android.os.Bundle;
import android.util.Log;

import com.serori.numeri.temp.activity.SubsidiaryActivity;
import com.serori.numeri.main.Global;
import com.serori.numeri.user.NumeriUser;

import twitter4j.DirectMessage;
import twitter4j.TwitterException;
import twitter4j.api.DirectMessagesResources;

/**
 */
public class DirectMessageActivity extends SubsidiaryActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NumeriUser numeriUser = Global.getInstance().getNumeriUsers().getNumeriUsers().get(0);
        hoge(numeriUser);
    }

    private void hoge(NumeriUser numeriUser) {

        new Thread(() -> {
            try {
                DirectMessagesResources directMessagesResources = numeriUser.getTwitter().directMessages();
                for (DirectMessage directMessage : directMessagesResources.getSentDirectMessages()) {
                    Log.v(toString(), "  directMessage:\n" +
                            "name :" + directMessage.getSenderScreenName() +
                            "\ntext :" + directMessage.getText() +
                            " " + directMessage.getRecipient().getScreenName());
                }
            } catch (TwitterException e) {
                Log.v(toString(), e.getMessage());
            }
        }).start();
    }

}
