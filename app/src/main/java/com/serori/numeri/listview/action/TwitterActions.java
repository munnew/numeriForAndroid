package com.serori.numeri.listview.action;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.listview.item.TimeLineItemAdapter;
import com.serori.numeri.media.MediaActivity;
import com.serori.numeri.twitter.ConversationActivity;
import com.serori.numeri.twitter.SimpleTweetStatus;
import com.serori.numeri.twitter.TweetActivity;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.userprofile.UserInformationActivity;
import com.serori.numeri.util.toast.ToastSender;

import java.util.ArrayList;
import java.util.List;

import twitter4j.TwitterException;

/**
 * ツイートに対するアクション
 */
public class TwitterActions {

    private Context context;
    private NumeriUser numeriUser;
    private TimeLineItemAdapter adapter;

    public TwitterActions(Context context, NumeriUser numeriUser, TimeLineItemAdapter adapter) {
        this.context = context;
        this.numeriUser = numeriUser;
        this.adapter = adapter;
    }


    public void onTouchAction(ActionStorager.RespectTapPositionActions respectTapPositionAction, int position) {

        switch (respectTapPositionAction.getTwitterAction()) {
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
            case SHOW_MEDIA:
                showMedia(adapter.getItem(position).getMediaUris());
            case OPEN_USER_PROFILE:
                openUserProfile(position);
            default:
                break;
        }

    }

    private void retweet(int position) {
        if (adapter.getItem(position).isProtectedUser()) {
            ToastSender.sendToast("非公開ユーザーのツイートはRTできません");
            return;
        }
        if (!((Activity) context).isFinishing()) {
            SimpleTweetStatus item = adapter.getItem(position);
            AlertDialog alertDialog = new AlertDialog.Builder(context).setMessage("このツイートをRTしますか？")
                    .setNegativeButton("いいえ", (dialog, id) -> {
                    })
                    .setPositiveButton("はい", (dialog, id) -> {
                        AsyncTask.execute(() -> {
                            if (!item.isMyRT()) {
                                try {
                                    numeriUser.getTwitter().retweetStatus(item.getStatusId());
                                    item.setMyRT(true);
                                    ToastSender.sendToast(item.getScreenName() + "さんのツイートをRTしました");
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
        SimpleTweetStatus item = adapter.getItem(position);
        if (!item.isFavorite()) {
            AsyncTask.execute(() -> {
                try {
                    numeriUser.getTwitter().createFavorite(item.getStatusId());
                    ToastSender.sendToast(item.getScreenName() + "さんのツイートをお気に入り登録しました。");
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            });
        } else {
            AsyncTask.execute(() -> {
                try {
                    numeriUser.getTwitter().destroyFavorite(item.getStatusId());
                    ToastSender.sendToast(item.getScreenName() + "さんのツイートをお気に入りから解除しました。");
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void reply(int position) {
        SimpleTweetStatus item = adapter.getItem(position);
        item.getStatusId();
        TweetActivity.setDestination(item.getStatusId(), item.getDestinationUserNames());
        TweetActivity.setTweetNunmeriUser(numeriUser);
        Intent intent = new Intent(context, TweetActivity.class);
        context.startActivity(intent);
    }

    private void showConversation(int position) {
        SimpleTweetStatus item = adapter.getItem(position);
        if (item.getInReplyToStatusId() != -1) {
            ConversationActivity.setNumeriUser(numeriUser);
            ConversationActivity.setConversationStatusId(item.getStatusId());
            Intent intent = new Intent(context, ConversationActivity.class);
            context.startActivity(intent);
        }
    }


    private void showMenu(int position) {
        SimpleTweetStatus item = adapter.getItem(position);
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(Actions.REPLY));

        if (item.isFavorite()) menuItems.add(new MenuItem(Actions.FAVORITE));
        else menuItems.add(new MenuItem(Actions.FAVORITE));

        if (!item.isProtectedUser()) menuItems.add(new MenuItem(Actions.RT));

        menuItems.add(new MenuItem(Actions.OPEN_USER_PROFILE));

        if (item.getInReplyToStatusId() != -1)
            menuItems.add(new MenuItem(Actions.SHOW_CONVERSATION));

        if (!item.getMediaUris().isEmpty()) menuItems.add(new MenuItem(Actions.SHOW_MEDIA));

        if (!item.getUris().isEmpty()) {
            for (String s : item.getUris()) {
                menuItems.add(new MenuItem(Actions.OPEN_URI, s));
            }
        }
        List<CharSequence> menuItemText = new ArrayList<>();
        for (MenuItem menuItem : menuItems) {
            if (menuItem.getUrl() == null) {
                menuItemText.add(menuItem.getAction().getName());
            } else {
                menuItemText.add(menuItem.getUrl());
            }

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
                            openUri(menuItems.get(which).getUrl());
                            break;
                        case SHOW_MEDIA:
                            showMedia(item.getMediaUris());
                            break;
                        case OPEN_USER_PROFILE:
                            openUserProfile(position);
                        default:
                            break;
                    }
                }).create();
        ((NumeriActivity) context).setCurrentShowDialog(alertDialog);
    }

    private void openUri(String uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        context.startActivity(intent);
    }

    private void showMedia(List<String> uris) {
        MediaActivity.setMediaUris(uris);
        Intent intent = new Intent(context, MediaActivity.class);
        context.startActivity(intent);
    }

    private void openUserProfile(int position) {
        UserInformationActivity.setUserId(adapter.getItem(position).getUserId());
        UserInformationActivity.setNumeriUser(numeriUser);
        Intent intent = new Intent(context, UserInformationActivity.class);
        context.startActivity(intent);
    }

    private static class MenuItem {
        private Actions action;
        private String url = null;

        MenuItem(Actions action) {
            this.action = action;
        }

        MenuItem(Actions action, String url) {
            this.action = action;
            this.url = url;
        }

        public Actions getAction() {
            return action;
        }

        public String getUrl() {
            return url;
        }

    }


    /**
     * アクションのidと名前を保持する列挙型
     */
    public static enum Actions {
        REPLY("リプライ", 0),
        RT("リツイート", 1),
        FAVORITE("お気に入り", 2),
        MENU("メニュー", 3),
        QT("引用リツイート", 4),
        OPEN_USER_PROFILE("ユーザー情報", 5),
        SHOW_CONVERSATION("会話を表示", 6),
        SHOW_MEDIA("画像を表示", 7),
        OPEN_URI("", 8);

        private final int id;
        private String name = null;

        private Actions(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }
    }
}