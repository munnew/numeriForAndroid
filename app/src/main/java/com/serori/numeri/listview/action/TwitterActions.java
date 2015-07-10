package com.serori.numeri.listview.action;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;

import com.serori.numeri.temp.activity.NumeriActivity;
import com.serori.numeri.listview.item.TimeLineItemAdapter;
import com.serori.numeri.media.MediaActivity;
import com.serori.numeri.twitter.ConversationActivity;
import com.serori.numeri.twitter.SimpleTweetStatus;
import com.serori.numeri.twitter.TweetActivity;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.userprofile.UserInformationActivity;
import com.serori.numeri.util.toast.ToastSender;
import com.serori.numeri.util.twitter.TwitterExceptionDisplay;

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
            case CONFIRM_FAVORITE:
                confirmFavorite(position);
                break;
            case RT:
                retweet(position);
                break;
            case CONFIRM_RT:
                confirmRetweet(position);
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
            case ALL_TAG_TWEET:
                hashtagsTweet(position);
            case ACTION_NONE:
                break;
            default:
                break;
        }

    }

    private void confirmRetweet(int position) {
        if (adapter.getItem(position).isProtectedUser()) {
            ToastSender.sendToast("非公開ユーザーのツイートはRTできません");
            return;
        }
        if (!((Activity) context).isFinishing()) {
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setMessage("このツイートをRTしますか？")
                    .setNegativeButton("キャンセル", null)
                    .setPositiveButton("はい", (dialog, id) -> retweet(position))
                    .create();
            ((NumeriActivity) context).setCurrentShowDialog(alertDialog);
        }
    }


    private void retweet(int position) {
        SimpleTweetStatus item = adapter.getItem(position);
        long statusId = item.isRT() ? item.getRetweetedStatusId() : item.getStatusId();
        if (item.isProtectedUser()) {
            ToastSender.sendToast("非公開ユーザーのツイートはRTできません");
            return;
        }
        new Thread(() -> {
            if (!item.isReTweeted()) {
                try {
                    numeriUser.getTwitter().retweetStatus(statusId);
                    item.setReTweeted(true);
                    ToastSender.sendToast(item.getScreenName() + "さんのツイートをRTしました");
                } catch (TwitterException e) {
                    TwitterExceptionDisplay.show(e);
                }
            }
        }).start();
    }

    private void favorite(int position) {
        SimpleTweetStatus item = adapter.getItem(position);
        long statusId = item.isRT() ? item.getRetweetedStatusId() : item.getStatusId();

        new Thread(() -> {
            try {
                if (!item.isFavorite()) {
                    numeriUser.getTwitter().createFavorite(statusId);
                } else {
                    numeriUser.getTwitter().destroyFavorite(statusId);
                }
            } catch (TwitterException e) {
                TwitterExceptionDisplay.show(e);
            }
        }).start();
    }

    private void confirmFavorite(int position) {
        SimpleTweetStatus item = adapter.getItem(position);
        String message = !item.isFavorite() ?
                "このツイートをお気に入り登録しますか？" : "このツイートをお気に入りから削除しますか?";
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("はい", (dialog, which) -> favorite(position))
                .setNegativeButton("キャンセル", null)
                .create();
        ((NumeriActivity) context).setCurrentShowDialog(alertDialog);
    }

    private void reply(int position) {
        SimpleTweetStatus item = adapter.getItem(position);
        TweetActivity.replyTweet(context, numeriUser, item);
    }

    private void showConversation(int position) {
        SimpleTweetStatus item = adapter.getItem(position);
        if (item.getInReplyToStatusId() != -1) {
            ConversationActivity.show(context, numeriUser, item.getStatusId());
        }
    }

    private void hashtagTweet(String hashtag) {
        TweetActivity.hashtagTweet(context, numeriUser, hashtag);
    }

    private void hashtagsTweet(int position) {
        SimpleTweetStatus item = adapter.getItem(position);
        if (!item.getHashtags().isEmpty()) {
            TweetActivity.hashtagsTweet(context, numeriUser, item.getHashtags());
        }
    }

    private void showMenu(int position) {
        SimpleTweetStatus item = adapter.getItem(position);
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(Actions.REPLY));

        if (!item.isFavorite()) menuItems.add(new MenuItem(Actions.FAVORITE));
        else menuItems.add(new MenuItem(Actions.FAVORITE, "お気に入り解除"));

        if (!item.isProtectedUser()) menuItems.add(new MenuItem(Actions.RT));

        if (!item.isProtectedUser()) menuItems.add(new MenuItem(Actions.QT));

        for (String s : item.getInvolvedUserNames()) {
            menuItems.add(new MenuItem(Actions.OPEN_INVOLVED_USER_PROFILE, "@" + s));
        }

        if (item.getInReplyToStatusId() != -1)
            menuItems.add(new MenuItem(Actions.SHOW_CONVERSATION));

        if (!item.getHashtags().isEmpty()) {
            for (String s : item.getHashtags()) {
                menuItems.add(new MenuItem(Actions.TAG_TWEET, "#" + s));
            }
            if (item.getHashtags().size() > 1)
                menuItems.add(new MenuItem(Actions.ALL_TAG_TWEET));
        }

        if (!item.getMediaUris().isEmpty())
            menuItems.add(new MenuItem(Actions.SHOW_MEDIA));

        if (!item.getUris().isEmpty()) {
            for (String s : item.getUris()) {
                menuItems.add(new MenuItem(Actions.OPEN_URI, s));
            }
        }

        List<CharSequence> menuItemText = new ArrayList<>();
        for (MenuItem menuItem : menuItems) {
            if (menuItem.getValue().equals("")) {
                menuItemText.add(menuItem.getAction().getName());
            } else {
                menuItemText.add(menuItem.getValue());
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
                        case TAG_TWEET:
                            hashtagTweet(menuItems.get(which).getValue());
                            break;
                        case ALL_TAG_TWEET:
                            hashtagsTweet(position);
                            break;
                        case OPEN_URI:
                            openUri(menuItems.get(which).getValue());
                            break;
                        case SHOW_MEDIA:
                            showMedia(item.getMediaUris());
                            break;
                        case OPEN_INVOLVED_USER_PROFILE:
                            openInvolvedUserProfile(menuItems.get(which).getValue());
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
        MediaActivity.show(context, uris);
    }

    private void openUserProfile(int position) {
        UserInformationActivity.show(context, adapter.getItem(position).getUserId(), numeriUser);
    }

    private void openInvolvedUserProfile(String screenName) {
        screenName = screenName.replace("@", "");
        UserInformationActivity.show(context, screenName, numeriUser);
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
        private String value = "";

        MenuItem(Actions action) {
            this.action = action;
        }

        MenuItem(Actions action, String value) {
            this.action = action;
            this.value = value;
        }

        public Actions getAction() {
            return action;
        }

        public String getValue() {
            return value;
        }

    }


    /**
     * アクションのidと名前を保持する列挙型
     */
    public enum Actions {
        REPLY("リプライ", "REPLY"),
        RT("リツイート", "RT"),
        CONFIRM_RT("リツイート(確認あり)", "CONFIRM_RT"),
        FAVORITE("お気に入り", "FAVORITE"),
        CONFIRM_FAVORITE("お気に入り(確認あり)", "CONFIRM_FAVORITE"),
        MENU("メニュー", "MENU"),
        QT("引用リツイート", "QT"),
        OPEN_USER_PROFILE("ユーザー情報", "OPEN_USER_PROFILE"),
        OPEN_INVOLVED_USER_PROFILE("", "OPEN_INVOLVED_USER_PROFILE"),
        SHOW_CONVERSATION("会話を表示", "SHOW_CONVERSATION"),
        SHOW_MEDIA("画像を表示", "SHOW_MEDIA"),
        OPEN_URI("", "OPEN_URI"),
        TAG_TWEET("", "TAG_TWEET"),
        ALL_TAG_TWEET("すべてのハッシュタグをツイート", "ALL_TAG_TWEET"),
        ACTION_NONE("何もしない", "ACTION_NONE");


        private String id;
        private String name = "";

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