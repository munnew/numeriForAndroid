package com.serori.numeri.user;


import com.serori.numeri.util.toast.ToastSender;

import twitter4j.UserMentionEntity;

/**
 * 通知
 */
public class NotificationSetter {


    public static void setUp(NumeriUser numeriUser) {

        numeriUser.getStreamEvent().addOnFollowListener((source, followedUser) -> {
            if (numeriUser.getAccessToken().getUserId() == source.getId()) {
                ToastSender.sendToast(followedUser.getScreenName() + "さんをフォローしました");
            }
            if (Notification.FOLLOW.isEnable()) {
                if (followedUser.getId() == numeriUser.getAccessToken().getUserId()) {
                    ToastSender.sendToast(source.getScreenName() + "さんにフォローされました");
                }
            }
        }).addOnUnFollowListener(((source1, unfollowedUser) -> {
            if (numeriUser.getAccessToken().getUserId() == source1.getId()) {
                ToastSender.sendToast(unfollowedUser.getScreenName() + "さんをリムーブしました");
            }
            if (Notification.UN_FOLLOW.isEnable()) {
                if (unfollowedUser.getId() == numeriUser.getAccessToken().getUserId()) {
                    ToastSender.sendToast(source1.getScreenName() + "さんにリムーブされました");
                }
            }
        })).addOnFavoriteListener(((source2, target, favoritedStatus) -> {
            if (Notification.FAVORITE.isEnable()) {
                if (target.getId() == numeriUser.getAccessToken().getUserId()) {
                    ToastSender.sendToast(source2.getScreenName() + "さんにお気に入り登録されました");
                }
            }
            if (source2.getId() == numeriUser.getAccessToken().getUserId()) {
                ToastSender.sendToast(target.getScreenName() + "さんのツイートをお気に入り登録しました");
            }
        })).addOnUnFavoriteListener(((source3, target1, unfavoritedStatus) -> {
            if (Notification.UN_FAVORITE.isEnable()) {
                if (target1.getId() == numeriUser.getAccessToken().getUserId()) {
                    ToastSender.sendToast(source3.getScreenName() + "さんにお気に入りから解除されました");
                }
            }
            if (source3.getId() == numeriUser.getAccessToken().getUserId()) {
                ToastSender.sendToast(target1.getScreenName() + "さんのツイートをお気に入りから解除しました");
            }
        })).addOnStatusListener(status -> {
            if (Notification.REPLY.isEnable()) {
                for (UserMentionEntity userMentionEntity : status.getUserMentionEntities()) {
                    if (userMentionEntity.getId() == numeriUser.getAccessToken().getUserId() && !status.isRetweet()) {
                        ToastSender.sendToast(status.getUser().getScreenName() + "さんからリプライを受け取りました");
                        break;
                    }
                }
            }
            if (Notification.RT.isEnable()) {
                if (status.isRetweet()) {
                    if (numeriUser.getAccessToken().getUserId() == status.getRetweetedStatus().getUser().getId()) {
                        ToastSender.sendToast(status.getUser().getScreenName() + "にRTされました。");
                    }
                }
            }
        });
    }

}
