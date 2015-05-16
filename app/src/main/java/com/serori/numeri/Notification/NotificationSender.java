package com.serori.numeri.Notification;

import android.util.Log;

import com.serori.numeri.main.Global;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.util.toast.ToastSender;

import twitter4j.UserMentionEntity;

/**
 * 通知
 */
public class NotificationSender {

    private NotificationSender() {

    }

    public static NotificationSender getInstance() {
        return NotificationHolder.instance;
    }

    public void sendStart() {
        for (NumeriUser numeriUser : Global.getInstance().getNumeriUsers().getNumeriUsers()) {
            numeriUser.getStreamEvent().addOnFollowListener((source, followedUser) -> {
                if (numeriUser.getAccessToken().getUserId() == source.getId()) {
                    ToastSender.sendToast(followedUser.getScreenName() + "さんをフォローしました");
                }
                if (followedUser.getId() == numeriUser.getAccessToken().getUserId()) {
                    ToastSender.sendToast(source.getScreenName() + "さんにフォローされました");
                }

            }).addOnUnFollowListener(((source1, unfollowedUser) -> {
                if (numeriUser.getAccessToken().getUserId() == source1.getId()) {
                    ToastSender.sendToast(unfollowedUser.getScreenName() + "さんをリムーブしました");
                }
                Log.v(toString(), unfollowedUser.getScreenName());
                if (unfollowedUser.getId() == numeriUser.getAccessToken().getUserId()) {
                    ToastSender.sendToast(source1.getScreenName() + "さんにリムーブされました");
                }
            })).addOnFavoriteListener(((source2, target, favoritedStatus) -> {
                if (target.getId() == numeriUser.getAccessToken().getUserId()) {
                    ToastSender.sendToast(source2.getScreenName() + "さんにお気に入り登録されました");
                }
                if (source2.getId() == numeriUser.getAccessToken().getUserId()) {
                    ToastSender.sendToast(target.getScreenName() + "さんのツイートをお気に入り登録しました");
                }
            })).addOnUnFavoriteListener(((source3, target1, unfavoritedStatus) -> {
                if (target1.getId() == numeriUser.getAccessToken().getUserId()) {
                    ToastSender.sendToast(source3.getScreenName() + "さんにお気に入りから解除されました");
                }
                if (source3.getId() == numeriUser.getAccessToken().getUserId()) {
                    ToastSender.sendToast(target1.getScreenName() + "さんのツイートをお気に入りから解除しました");
                }
            })).addOnStatusListener(status -> {
                for (UserMentionEntity userMentionEntity : status.getUserMentionEntities()) {
                    if (userMentionEntity.getId() == numeriUser.getAccessToken().getUserId()) {
                        ToastSender.sendToast(status.getUser().getScreenName() + "さんからリプライを受け取りました");
                        break;
                    }
                }
            });
        }
    }

    private static class NotificationHolder {
        private static final NotificationSender instance = new NotificationSender();
    }
}
