package com.serori.numeri.fragment.listview.action;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.fragment.listview.item.TimeLineItemAdapter;
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
            case QT:
                quoteRetweet(position);
                break;
            case SHOW_CONVERSATION:
                showConversation(position);
                break;
            case MENU:
                showMenu(position);
                break;
            case SHOW_MEDIA:
                showMedia(adapter.getItem(position).getMediaUris());
                break;
            case OPEN_USER_PROFILE:
                openUserProfile(position);
                break;
            case ACTION_NONE:
                break;
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
            long statusId = item.isRT() ? item.getRetweetedStatusId() : item.getStatusId();
            AlertDialog alertDialog = new AlertDialog.Builder(context).setMessage("このツイートをRTしますか？")
                    .setNegativeButton("いいえ", (dialog, id) -> {
                    })
                    .setPositiveButton("はい", (dialog, id) -> {
                        AsyncTask.execute(() -> {
                            if (!item.isMyRT()) {
                                try {
                                    numeriUser.getTwitter().retweetStatus(statusId);
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
        long statusId = item.isRT() ? item.getRetweetedStatusId() : item.getStatusId();
        if (!item.isFavorite()) {
            AsyncTask.execute(() -> {
                try {
                    numeriUser.getTwitter().createFavorite(statusId);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            });
        } else {
            AsyncTask.execute(() -> {
                try {
                    numeriUser.getTwitter().destroyFavorite(statusId);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void reply(int position) {
        SimpleTweetStatus item = adapter.getItem(position);
        TweetActivity.replyTweet(context, numeriUser, item);
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

        if (!item.isProtectedUser()) menuItems.add(new MenuItem(Actions.QT));

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
                        case QT:
                            quoteRetweet(position);
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
                            break;
                        default:
                            break;
                    }
                    dialog.dismiss();
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
        UserInformationActivity.show(context, adapter.getItem(position).getUserId(), numeriUser);
    }

    private void quoteRetweet(int position) {
        SimpleTweetStatus item = adapter.getItem(position);
        if (item.isProtectedUser()) {
            ToastSender.sendToast("非公開ユーザーのツイートは引用RTできません");
            return;
        }
        TweetActivity.quoteRetweet(context, numeriUser, item);
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
    public enum Actions {
        REPLY("リプライ", "REPLY"),
        RT("リツイート", "RT"),
        FAVORITE("お気に入り", "FAVORITE"),
        MENU("メニュー", "MENU"),
        QT("引用リツイート", "QT"),
        OPEN_USER_PROFILE("ユーザー情報", "OPEN_USER_PROFILE"),
        SHOW_CONVERSATION("会話を表示", "SHOW_CONVERSATION"),
        SHOW_MEDIA("画像を表示", "SHOW_MEDIA"),
        ACTION_NONE("何もしない", "ACTION_NONE"),
        OPEN_URI("", "OPEN_URI");


        private String id;
        private String name = null;

        Actions(String name, String id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

    }
}