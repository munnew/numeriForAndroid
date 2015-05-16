package com.serori.numeri.twitter;

import android.text.Html;
import android.util.Log;

import com.serori.numeri.main.Global;
import com.serori.numeri.user.NumeriUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;

/**
 * Statusから生成される軽量化されたStatus
 */
public class SimpleTweetStatus {
    private String biggerIconImageUrl;
    private String iconImageUrl;
    private String name, mainText, via;
    private long statusId, userId, retweetedStatusId = -1;
    private static final String DATE_FORMAT = "MM/dd HH:mm:ss";
    private String screenName;
    private boolean isMyRT = false, isMention = false, isFavorite = false;
    private boolean isMyTweet = false;
    private boolean isProtectedUser = false;
    private String createdTime;
    private List<String> destinationUserNames = new ArrayList<>();
    private List<String> uris = new ArrayList<>();
    private List<String> mediaUris = new ArrayList<>();
    private long inReplyToStatusId;
    private volatile static Map<String, SimpleTweetStatus> simpleTweetStatusMap = new LinkedHashMap<>();
    private static boolean observeFavoriteStarted = false;
    private long statusAcquirerId;
    private static boolean observeDestroyTweetStarted = false;

    /**
     * StatusをSimpleTweetStatusとしてキャッシュします。
     *
     * @param status     取得済みのStatus
     * @param numeriUser そのStatusを取得したユーザー
     * @return キャッシュされたSimpleTweetStatus
     */
    public static SimpleTweetStatus build(Status status, NumeriUser numeriUser) {
        String key = createSimpleTweetStatusKey(numeriUser, status.getId());
        SimpleTweetStatus simpleTweetStatus = simpleTweetStatusMap.get(key);
        if (simpleTweetStatus == null) {
            simpleTweetStatus = new SimpleTweetStatus(status, numeriUser);
            simpleTweetStatusMap.put(key, simpleTweetStatus);
            return simpleTweetStatus;
        }
        Log.v("SimpleTweetStatus", simpleTweetStatus.getScreenName());
        return simpleTweetStatus;
    }

    /**
     * 指定されたIdのStatusを参照しSimpleTweetStatusを返します<br>
     * 取得されたStatusはSimpleTweetStatusとしてキャッシュされます<br>
     * 存在しなければnullを返します<br>
     * このメソッドは非同期タスクで実行されるべきです。
     *
     * @param statusId   参照するStatusのId
     * @param numeriUser 参照させたいユーザー
     * @return 参照されたキャッシュ済みのSimpleTweetStatus
     * @throws TwitterException
     */
    public static SimpleTweetStatus showStatus(long statusId, NumeriUser numeriUser) throws TwitterException {
        String key = createSimpleTweetStatusKey(numeriUser, statusId);
        SimpleTweetStatus simpleTweetStatus = simpleTweetStatusMap.get(key);
        if (simpleTweetStatus != null) return simpleTweetStatus;
        Status status = numeriUser.getTwitter().showStatus(statusId);
        if (status != null) {
            return build(status, numeriUser);
        }
        return null;
    }

    /**
     * お気に入りにイベントの監視を開始しアプリケーションのユーザーがお気に入り登録を行った場合はそのパラメータを変更する<br>
     * このメソッドはアプリケーションが生きている間一度しか実行することが出来ない
     */
    public static void startObserveFavorite() {
        List<NumeriUser> numeriUsers = Global.getInstance().getNumeriUsers().getNumeriUsers();
        if (observeFavoriteStarted || numeriUsers.isEmpty()) return;
        observeFavoriteStarted = true;

        for (NumeriUser numeriUser : numeriUsers) {
            numeriUser.getStreamEvent().addOnFavoriteListener((user1, user2, favoritedStatus) -> {
                if (user1.getId() == numeriUser.getAccessToken().getUserId()) {
                    SimpleTweetStatus favoritedSimpleTweetStatus = SimpleTweetStatus.build(favoritedStatus, numeriUser);
                    for (SimpleTweetStatus simpleTweetStatus : simpleTweetStatusMap.values()) {
                        if (simpleTweetStatus.equals(favoritedSimpleTweetStatus)) {
                            simpleTweetStatus.setFavorite(true);
                        }
                    }
                }

            }).addOnUnFavoriteListener((user1, user2, unFavoritedStatus) -> {
                if (user1.getId() == numeriUser.getAccessToken().getUserId()) {
                    SimpleTweetStatus favoritedSimpleTweetStatus = SimpleTweetStatus.build(unFavoritedStatus, numeriUser);
                    for (SimpleTweetStatus simpleTweetStatus : simpleTweetStatusMap.values()) {
                        if (simpleTweetStatus.equals(favoritedSimpleTweetStatus)) {
                            simpleTweetStatus.setFavorite(false);
                        }
                    }
                }
            });
        }
        Global.getInstance().addOnFinishMainActivityListener(() -> observeFavoriteStarted = false);
    }

    /**
     * ツイートの削除イベントの監視を開始し削除された場合はキャッシュから削除する<br>
     * このメソッドはアプリケーションが生きている間一度しか実行することが出来ない
     */
    public static void startObserveDestroyTweet() {
        List<NumeriUser> numeriUsers = Global.getInstance().getNumeriUsers().getNumeriUsers();
        if (observeDestroyTweetStarted || numeriUsers.isEmpty()) return;

        for (NumeriUser numeriUser : numeriUsers) {
            numeriUser.getStreamEvent().addOnDeletionNoticeListener(statusDeletionNotice -> {
                long deletedStatusId = statusDeletionNotice.getStatusId();
                simpleTweetStatusMap.remove(createSimpleTweetStatusKey(numeriUser, deletedStatusId));
            });
        }
    }

    private static String createSimpleTweetStatusKey(NumeriUser numeriUser, long statusId) {
        return numeriUser.getScreenName() + statusId;
    }

    private SimpleTweetStatus(Status status, NumeriUser numeriUser) {
        createdTime = new SimpleDateFormat(DATE_FORMAT).format(status.getCreatedAt());
        statusAcquirerId = numeriUser.getAccessToken().getUserId();
        statusId = status.getId();
        if (status.isRetweet()) { //RT
            isProtectedUser = status.getRetweetedStatus().getUser().isProtected();
            biggerIconImageUrl = status.getRetweetedStatus().getUser().getBiggerProfileImageURL();
            iconImageUrl = status.getRetweetedStatus().getUser().getProfileImageURL();
            mainText = status.getRetweetedStatus().getText();
            name = status.getRetweetedStatus().getUser().getName();
            via = "via " + Html.fromHtml(status.getRetweetedStatus().getSource()).toString() + " RT by " + status.getUser().getScreenName() + "\n RT count : " + status.getRetweetedStatus().getRetweetCount();
            screenName = status.getRetweetedStatus().getUser().getScreenName();
            isFavorite = status.getRetweetedStatus().isFavorited();
            userId = status.getRetweetedStatus().getUser().getId();
            inReplyToStatusId = status.getRetweetedStatus().getInReplyToStatusId();
            isMyRT = status.getRetweetedStatus().isRetweetedByMe();
            retweetedStatusId = status.getRetweetedStatus().getId();
        } else {//!RT
            isProtectedUser = status.getUser().isProtected();
            biggerIconImageUrl = status.getUser().getBiggerProfileImageURL();
            iconImageUrl = status.getUser().getProfileImageURL();
            mainText = status.getText();
            name = status.getUser().getName();
            via = "via " + Html.fromHtml(status.getSource()).toString();
            screenName = status.getUser().getScreenName();
            isFavorite = status.isFavorited();
            userId = status.getUser().getId();
            inReplyToStatusId = status.getInReplyToStatusId();
            isMyRT = status.isRetweetedByMe();
            if (numeriUser.getAccessToken().getUserId() == status.getUser().getId()) {
                isMyTweet = true;
            }
        }

        UserMentionEntity[] mentionEntity = status.getUserMentionEntities();
        destinationUserNames.add(screenName);
        for (UserMentionEntity userMentionEntity : mentionEntity) {//?Mention
            if (userMentionEntity.getId() == numeriUser.getAccessToken().getUserId()) {
                isMention = true;
            } else if (!screenName.equals(userMentionEntity.getScreenName())) {
                destinationUserNames.add(userMentionEntity.getScreenName());
            }
        }
        name = name.replaceAll("\r", "");
        name = name.replaceAll("\n", "");
        name = name.replaceAll("\t", "");

        for (MediaEntity mediaEntity : status.getExtendedMediaEntities()) {
            mediaUris.add(mediaEntity.getMediaURL());
        }

        for (URLEntity urlEntity : status.getURLEntities()) {
            uris.add(urlEntity.getExpandedURL());
        }

    }

    //setter
    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public void setMyRT(boolean isRT) {
        this.isMyRT = isRT;
    }

    //getter
    public String getName() {
        return name;
    }

    public List<String> getDestinationUserNames() {
        List<String> destinationUserNames = new ArrayList<>();
        destinationUserNames.addAll(this.destinationUserNames);
        return destinationUserNames;
    }

    public String getMainText() {
        return mainText;
    }


    public String getVia() {
        return via;
    }


    public long getStatusId() {
        return statusId;
    }

    public String getScreenName() {
        return screenName;
    }

    public long getUserId() {
        return userId;
    }

    public boolean isMyRT() {
        return isMyRT;
    }

    public boolean isMention() {
        return isMention;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getBiggerIconImageUrl() {
        return biggerIconImageUrl;
    }

    public String getIconImageUrl() {
        return iconImageUrl;
    }

    public boolean isRT() {
        return retweetedStatusId != -1;
    }

    public Long getInReplyToStatusId() {
        return inReplyToStatusId;
    }

    public List<String> getUris() {
        List<String> uris = new ArrayList<>();
        uris.addAll(this.uris);
        return uris;
    }

    public List<String> getMediaUris() {
        List<String> mediaUris = new ArrayList<>();
        mediaUris.addAll(this.mediaUris);
        return mediaUris;
    }

    public boolean isMyTweet() {
        return isMyTweet;
    }

    public boolean isProtectedUser() {
        return isProtectedUser;
    }

    public long getRetweetedStatusId() {
        return retweetedStatusId;
    }

    @Override
    public boolean equals(Object o) {
        boolean isSimpleTweetStatus = o instanceof SimpleTweetStatus;
        if (!isSimpleTweetStatus) return false;
        boolean isSameTweet;
        if (this.isRT()) {
            isSameTweet = this.retweetedStatusId == ((SimpleTweetStatus) o).getStatusId()
                    || this.retweetedStatusId == ((SimpleTweetStatus) o).getRetweetedStatusId();
        } else {
            isSameTweet = this.statusId == ((SimpleTweetStatus) o).getStatusId()
                    || this.statusId == ((SimpleTweetStatus) o).getRetweetedStatusId();
        }

        return isSameTweet && ((SimpleTweetStatus) o).statusAcquirerId == this.statusAcquirerId;
    }


}