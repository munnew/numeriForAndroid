package com.serori.numeri.twitter;

import android.text.Html;
import android.util.Log;

import com.serori.numeri.main.Application;
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
 * Statusから生成される軽量化されたオブジェクト
 */
public class SimpleTweetStatus {
    private String iconImageUrl;
    private String name, mainText, via;
    private long statusId, userId;
    private static final String DATE_FORMAT = "MM/dd HH:mm:ss";
    private String screenName;
    private boolean isMyRT = false, isMention = false, isFavorite = false;
    private boolean isRT = false;
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

    /**
     * StatusをSimpleTweetStatusとしてキャッシュします。
     *
     * @param status     取得済みのStatus
     * @param numeriUser そのStatusを取得したユーザー
     * @return キャッシュされたSimpleTweetStatus
     */
    public static SimpleTweetStatus build(Status status, NumeriUser numeriUser) {
        String key = numeriUser.getScreenName() + status.getId();
        SimpleTweetStatus simpleTweetStatus = simpleTweetStatusMap.get(key);
        if (simpleTweetStatus == null) {
            simpleTweetStatus = new SimpleTweetStatus(status, numeriUser);
            simpleTweetStatusMap.put(key, simpleTweetStatus);
            Log.v("SimpleTweetStatus", "new " + key + " : " + status.getText());
            return simpleTweetStatus;
        }
        Log.v("SimpleTweetStatus", key + " : " + status.getText());
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
        String key = numeriUser.getScreenName() + statusId;
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
        List<NumeriUser> numeriUsers = Application.getInstance().getNumeriUsers().getNumeriUsers();
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
        Application.getInstance().addOnFinishMainActivityListener(() -> observeFavoriteStarted = false);
    }

    private SimpleTweetStatus(Status status, NumeriUser numeriUser) {
        createdTime = new SimpleDateFormat(DATE_FORMAT).format(status.getCreatedAt());
        statusAcquirerId = numeriUser.getAccessToken().getUserId();
        if (status.isRetweet()) { //RT
            statusId = status.getRetweetedStatus().getId();
            isProtectedUser = status.getRetweetedStatus().getUser().isProtected();
            isRT = true;
            iconImageUrl = status.getRetweetedStatus().getUser().getBiggerProfileImageURL();
            mainText = status.getRetweetedStatus().getText();
            name = status.getRetweetedStatus().getUser().getName();
            via = "via " + Html.fromHtml(status.getRetweetedStatus().getSource()).toString() + " RT by " + status.getUser().getScreenName();
            screenName = status.getRetweetedStatus().getUser().getScreenName();
            isFavorite = status.getRetweetedStatus().isFavorited();
            userId = status.getRetweetedStatus().getUser().getId();
            inReplyToStatusId = status.getRetweetedStatus().getInReplyToStatusId();
            isMyRT = status.getRetweetedStatus().isRetweetedByMe();
        } else {//!RT
            statusId = status.getId();
            isProtectedUser = status.getUser().isProtected();
            iconImageUrl = status.getUser().getBiggerProfileImageURL();
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


    public String getName() {
        return name;
    }

    public List<String> getDestinationUserNames() {
        List<String> destinationUserNames = new ArrayList<>();
        destinationUserNames.addAll(this.destinationUserNames);
        return destinationUserNames;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainText() {
        return mainText;
    }


    public String getVia() {
        return via;
    }

    //setter
    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public void setMyRT(boolean isRT) {
        this.isMyRT = isRT;
    }

    //getter
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

    public String getIconImageUrl() {
        return iconImageUrl;
    }

    public boolean isRT() {
        return isRT;
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

    @Override
    public boolean equals(Object o) {
        boolean isSimpleTweetStatus = o instanceof SimpleTweetStatus;
        return isSimpleTweetStatus && (getStatusId() == ((SimpleTweetStatus) o).getStatusId()) && ((SimpleTweetStatus) o).statusAcquirerId == this.statusAcquirerId;
    }
}