package com.serori.numeri.action;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.serori.numeri.R;
import com.serori.numeri.application.Application;
import com.serori.numeri.item.TimeLineItem;
import com.serori.numeri.twitter.TweetActivity;
import com.serori.numeri.user.NumeriUser;


import twitter4j.TwitterException;

/**
 * Created by serioriKETC on 2014/12/25.
 */
public class Actions {


    private Context context;

    private Actions() {
        context = Application.getInstance().getMainActivityContext();
    }

    public static Actions getInstance() {
        return ActionsHolder.instance;
    }

    public static final int REPLY = 0;
    public static final int FAVORITE = 1;
    public static final int RT = 2;
    public static final int MENU = 4;

    public void onTouchAction(int action, TimeLineItem item, NumeriUser numeriUser, View view) {
        switch (action) {
            case REPLY:
                reply(item);
                break;
            case FAVORITE:
                favorite(item, numeriUser, view);
                break;
            case RT:
                retweet(item, numeriUser);
                break;
            default:
                break;
        }

    }

    private void retweet(TimeLineItem item, NumeriUser numeriUser) {

        new AlertDialog.Builder(context).setMessage("このツイートをRTしますか？")
                .setNegativeButton("いいえ", (dialog, id) -> {
                })
                .setPositiveButton("はい", (dialog, id) -> {
                    AsyncTask.execute(() -> {
                        if (!item.isRT()) {
                            try {
                                numeriUser.getTwitter().retweetStatus(item.getStatusId());
                                item.setRT(true);
                                Application.getInstance().onToast(item.getScreenName()+"さんのツイートをRTしました",Toast.LENGTH_SHORT);
                            } catch (TwitterException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                })
                .create().show();

    }

    private void favorite(TimeLineItem item, NumeriUser numeriUser, View view) {
        ImageView favoriteStar = (ImageView) view.findViewById(R.id.favoriteStar);
        if (!item.isFavorite()) {
            AsyncTask.execute(() -> {
                try {
                    numeriUser.getTwitter().createFavorite(item.getStatusId());
                    item.setFavorite(true);
                    ((Activity) context).runOnUiThread(() -> favoriteStar.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite_star)));
                    Application.getInstance().onToast(item.getScreenName()+"さんのツイートをお気に入り登録しました。", Toast.LENGTH_SHORT);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            });
        } else {
            AsyncTask.execute(() -> {
                try {
                    numeriUser.getTwitter().destroyFavorite(item.getStatusId());
                    item.setFavorite(false);
                    ((Activity) context).runOnUiThread(() -> favoriteStar.setImageBitmap(null));
                    Application.getInstance().onToast(item.getScreenName()+"さんのツイートをお気に入りから解除しました。", Toast.LENGTH_SHORT);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void reply(TimeLineItem item) {
        item.getStatusId();
        TweetActivity.setDestination(item.getStatusId(), item.getDestinationUserNames());
        Intent intent = new Intent(context, TweetActivity.class);
        context.startActivity(intent);
    }

    private static class ActionsHolder {
        private static final Actions instance = new Actions();
    }
}