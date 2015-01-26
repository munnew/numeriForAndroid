package com.serori.numeri.listview.action;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.serori.numeri.R;
import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.listview.item.TimeLineItem;
import com.serori.numeri.listview.item.TimeLineItemAdapter;
import com.serori.numeri.media.MediaActivity;
import com.serori.numeri.toast.ToastSender;
import com.serori.numeri.twitter.ConversationActivity;
import com.serori.numeri.twitter.TweetActivity;
import com.serori.numeri.user.NumeriUser;

import java.util.ArrayList;
import java.util.List;

import twitter4j.TwitterException;

/**
 * Numeri
 */
public class Actions {

    private Context context;
    private NumeriUser numeriUser;
    private TimeLineItemAdapter adapter;

    public Actions(Context context, NumeriUser numeriUser, TimeLineItemAdapter adapter) {
        this.context = context;
        this.numeriUser = numeriUser;
        this.adapter = adapter;
    }

    public static final int REPLY = 0;
    public static final int FAVORITE = 1;
    public static final int RT = 2;
    public static final int SHOW_CONVERSATION = 4;
    public static final int MENU = 5;
    public static final int OPEN_URI = 6;
    public static final int SHOW_MEDIA = 7;

    public void onTouchAction(int action, int position) {
        switch (action) {
            case REPLY:
                reply(position);
                break;
            case FAVORITE:
                favorite(position);
                break;
            case RT:
                retweet(position);
                break;
            case SHOW_CONVERSATION:
                showConversation(position);
                break;
            case MENU:
                showMenu(position);
                break;
            default:
                break;
        }

    }

    private void retweet(int position) {
        if (!((Activity) context).isFinishing()) {
            TimeLineItem item = adapter.getItem(position);
            AlertDialog alertDialog = new AlertDialog.Builder(context).setMessage("このツイートをRTしますか？")
                    .setNegativeButton("いいえ", (dialog, id) -> {
                    })
                    .setPositiveButton("はい", (dialog, id) -> {
                        AsyncTask.execute(() -> {
                            if (!item.isRT()) {
                                try {
                                    numeriUser.getTwitter().retweetStatus(item.getStatusId());
                                    item.setRT(true);
                                    ToastSender.getInstance().sendToast(item.getScreenName() + "さんのツイートをRTしました");
                                } catch (TwitterException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    })
                    .create();
            ((NumeriActivity) context).setCurrentShowDialog(alertDialog);
        }
    }

    private void favorite(int position) {
        TimeLineItem item = adapter.getItem(position);
        View view = adapter.getView(position, adapter.getCurrentVeiw(), null);
        ImageView favoriteStar = (ImageView) view.findViewById(R.id.favoriteStar);
        if (!item.isFavorite()) {
            AsyncTask.execute(() -> {
                try {
                    numeriUser.getTwitter().createFavorite(item.getStatusId());
                    item.setFavorite(true);
                    ((Activity) context).runOnUiThread(() -> favoriteStar.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite_star)));
                    ToastSender.getInstance().sendToast(item.getScreenName() + "さんのツイートをお気に入り登録しました。");
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
                    ToastSender.getInstance().sendToast(item.getScreenName() + "さんのツイートをお気に入りから解除しました。");
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void reply(int position) {
        TimeLineItem item = adapter.getItem(position);
        item.getStatusId();
        TweetActivity.setDestination(item.getStatusId(), item.getDestinationUserNames());
        TweetActivity.setTweetNunmeriUser(numeriUser);
        Intent intent = new Intent(context, TweetActivity.class);
        context.startActivity(intent);
    }

    private void showConversation(int position) {
        TimeLineItem item = adapter.getItem(position);
        if (item.getConversationId() != -1) {
            ConversationActivity.setNumeriUser(numeriUser);
            ConversationActivity.setConversationStatusId(item.getStatusId());
            Intent intent = new Intent(context, ConversationActivity.class);
            context.startActivity(intent);
        }
    }


    private void showMenu(int position) {
        TimeLineItem item = adapter.getItem(position);
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(REPLY, "リプライ"));

        if (item.isFavorite()) menuItems.add(new MenuItem(FAVORITE, "お気に入り解除"));
        else menuItems.add(new MenuItem(FAVORITE, "お気に入り"));

        menuItems.add(new MenuItem(RT, "リツイート"));

        if (item.getConversationId() != -1) menuItems.add(new MenuItem(SHOW_CONVERSATION, "会話を表示"));

        if (!item.getMediaUris().isEmpty()) menuItems.add(new MenuItem(SHOW_MEDIA, "画像を表示"));

        if (!item.getUris().isEmpty()) {
            for (String s : item.getUris()) {
                menuItems.add(new MenuItem(OPEN_URI, s));
            }
        }
        List<CharSequence> menuItemText = new ArrayList<>();
        for (MenuItem menuItem : menuItems) {
            menuItemText.add(menuItem.getText());
        }
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(item.getScreenName() + "\n" + item.getName())
                .setItems(menuItemText.toArray(new CharSequence[menuItemText.size()]), (dialog, which) -> {
                    switch (menuItems.get(which).getAction()) {
                        case REPLY:
                            reply(position);
                            break;
                        case FAVORITE:
                            favorite(position);
                            break;
                        case RT:
                            retweet(position);
                            break;
                        case SHOW_CONVERSATION:
                            showConversation(position);
                            break;
                        case OPEN_URI:
                            opneUri(menuItems.get(which).text.toString());
                            break;
                        case SHOW_MEDIA:
                            showMedia(item.getMediaUris());
                            break;
                        default:
                            break;
                    }
                }).create();
        ((NumeriActivity) context).setCurrentShowDialog(alertDialog);
    }

    private void opneUri(String uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        context.startActivity(intent);
    }

    private void showMedia(List<String> uris) {
        MediaActivity.setMediaUris(uris);
        Intent intent = new Intent(context, MediaActivity.class);
        context.startActivity(intent);
    }

    private static class MenuItem {
        private int action;
        private CharSequence text;

        MenuItem(int action, CharSequence text) {
            this.action = action;
            this.text = text;
        }

        public int getAction() {
            return action;
        }

        public CharSequence getText() {
            return text;
        }
    }


}