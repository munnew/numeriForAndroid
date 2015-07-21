package com.serori.numeri.twitter;

import android.text.Html;
import android.util.Log;

import com.serori.numeri.user.NumeriUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;

/**
 * Statusから生成される軽量化されたStatus
 */
public final class SimpleTweetStatus {
    @Getter
    private String biggerProfileImageURL;
    @Getter
    private String profileImageURL;
    @Getter
    private String originalProfileImageURL;
    @Getter
    private String name;
    @Getter
    private String screenName;
    @Getter
    private String text;
    @Getter
    private String via;
    @Getter
    private final long statusId;
    @Getter
    private final long userId;
    @Getter
    private final long retweetedStatusId;
    @Getter
    private final String createdTime;
    @Getter
    private final long inReplyToStatusId;
    @Getter
    private final NumeriUser ownerUser;
    @Getter
    private boolean isMention = false;
    @Getter
    private boolean isMyTweet = false;
    @Getter
    private boolean isProtectedUser = false;

    private static final String DATE_FORMAT = "MM/dd HH:mm:ss";

    @Setter
    @Getter
    private boolean isReTweeted = false;
    @Setter
    @Getter
    private boolean isFavorite = false;

    private final List<String> destinationUserNames = new ArrayList<>();
    private final List<String> uris = new ArrayList<>();
    private final List<String> mediaUris = new ArrayList<>();
    private final List<String> thumbnailUris = new ArrayList<>();
    private final List<String> hashtags = new ArrayList<>();
    private final List<String> involvedUserNames = new ArrayList<>();

    private volatile static Map<String, SimpleTweetStatus> simpleTweetStatusMap = new LinkedHashMap<>();

    private static final String TWITPIC_URL = "^https?:\\/\\/twitpic\\.com/[a-z0-9]+$";
    private static final String TWIPPLE_URL = "^https?:\\/\\/p\\.twipple\\.jp/[a-zA-Z0-9]+$";
    private static final String PHOTOZOU_URL = "^https?:\\/\\/photozou\\.jp/photo/show/" +
            "[0-9][0-9][0-9][0-9][0-9][0-9][0-9]/" +
            "[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]";

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
     * このメソッドは非同期で実行されるべきです。
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
     *
     * @param numeriUser Applicationのユーザー
     */
    public static void startObserveFavorite(NumeriUser numeriUser) {

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

    /**
     * ツイートの削除イベントの監視を開始し削除された場合はキャッシュから削除する<br>
     * このメソッドはアプリケーションが生きている間一度しか実行することが出来ない
     *
     * @param numeriUser Applicationのユーザー
     */
    public static void startObserveDestroyTweet(NumeriUser numeriUser) {
        numeriUser.getStreamEvent().addOnStatusDeletionNoticeListener(statusDeletionNotice -> {
            long deletedStatusId = statusDeletionNotice.getStatusId();
            simpleTweetStatusMap.remove(createSimpleTweetStatusKey(numeriUser, deletedStatusId));
        });
    }

    private static String createSimpleTweetStatusKey(NumeriUser numeriUser, long statusId) {
        return numeriUser.getScreenName() + statusId;
    }

    private SimpleTweetStatus(Status status, NumeriUser numeriUser) {
        createdTime = new SimpleDateFormat(DATE_FORMAT).format(status.getCreatedAt());
        ownerUser = numeriUser;
        statusId = status.getId();
        if (status.isRetweet()) { //RT
            isProtectedUser = status.getRetweetedStatus().getUser().isProtected();
            biggerProfileImageURL = status.getRetweetedStatus().getUser().getBiggerProfileImageURL();
            profileImageURL = status.getRetweetedStatus().getUser().getProfileImageURL();
            originalProfileImageURL = status.getRetweetedStatus().getUser().getOriginalProfileImageURL();
            text = status.getRetweetedStatus().getText();
            name = status.getRetweetedStatus().getUser().getName();
            via = "via " + Html.fromHtml(status.getRetweetedStatus().getSource()).toString() + " RT by " + status.getUser().getScreenName() + "\n RT count : " + status.getRetweetedStatus().getRetweetCount();
            screenName = status.getRetweetedStatus().getUser().getScreenName();
            isFavorite = status.getRetweetedStatus().isFavorited();
            userId = status.getRetweetedStatus().getUser().getId();
            inReplyToStatusId = status.getRetweetedStatus().getInReplyToStatusId();
            isReTweeted = status.getRetweetedStatus().isRetweetedByMe();
            retweetedStatusId = status.getRetweetedStatus().getId();
        } else {//!RT
            isProtectedUser = status.getUser().isProtected();
            biggerProfileImageURL = status.getUser().getBiggerProfileImageURL();
            originalProfileImageURL = status.getUser().getOriginalProfileImageURL();
            profileImageURL = status.getUser().getProfileImageURL();
            text = status.getText();
            name = status.getUser().getName();
            via = "via " + Html.fromHtml(status.getSource()).toString();
            screenName = status.getUser().getScreenName();
            isFavorite = status.isFavorited();
            userId = status.getUser().getId();
            inReplyToStatusId = status.getInReplyToStatusId();
            isReTweeted = status.isRetweetedByMe();
            retweetedStatusId = -1;
            if (numeriUser.getAccessToken().getUserId() == status.getUser().getId()) {
                isMyTweet = true;
            }
        }
        name = name.replaceAll("\r", "");
        name = name.replaceAll("\n", "");
        name = name.replaceAll("\t", "");
        setHashtags(status);
        setEntity(status, numeriUser);
        setUrlEntity(status);
    }

    private void setHashtags(Status status) {
        for (HashtagEntity hashtagEntity : status.getHashtagEntities()) {
            hashtags.add(hashtagEntity.getText());
        }
    }

    private void setEntity(Status status, NumeriUser numeriUser) {
        UserMentionEntity[] mentionEntity = status.getUserMentionEntities();
        destinationUserNames.add(screenName);
        involvedUserNames.add(screenName);
        for (UserMentionEntity userMentionEntity : mentionEntity) {
            boolean nonMatch1 = true;
            for (String involvedUserName : involvedUserNames) {
                if (involvedUserName.equals(userMentionEntity.getScreenName())) {
                    nonMatch1 = false;
                    break;
                }
            }
            boolean nonMatch2 = true;
            for (String destinationUserName : destinationUserNames) {
                if (destinationUserName.equals(userMentionEntity.getScreenName())) {
                    nonMatch2 = false;
                    break;
                }
            }

            if (nonMatch1) involvedUserNames.add(userMentionEntity.getScreenName());
            if (userMentionEntity.getId() == numeriUser.getAccessToken().getUserId()) {
                isMention = true;
            } else if (!screenName.equals(userMentionEntity.getScreenName()) && nonMatch2) {
                destinationUserNames.add(userMentionEntity.getScreenName());
            }
        }

    }

    private void setUrlEntity(Status status) {

        for (MediaEntity mediaEntity : status.getExtendedMediaEntities()) {
            mediaUris.add(mediaEntity.getMediaURL());
            thumbnailUris.add(mediaEntity.getMediaURL() + ":thumb");
        }
        for (URLEntity urlEntity : status.getURLEntities()) {
            String expandedURL = urlEntity.getExpandedURL();
            if (expandedURL.matches(TWITPIC_URL)) {
                mediaUris.add(expandedURL.replaceFirst("twitpic\\.com", "twitpic.com/show/full"));
                thumbnailUris.add(expandedURL.replaceFirst("twitpic\\.com/", "twitpic.com/show/thumb/"));
            } else if (expandedURL.matches(TWIPPLE_URL)) {
                mediaUris.add(expandedURL.replaceFirst("twipple\\.jp/", "twipple.jp/show/orig/"));
                thumbnailUris.add(expandedURL.replaceFirst("twipple\\.jp/", "twipple.jp/show/thumb/"));
            } else if (expandedURL.matches(PHOTOZOU_URL)) {
                mediaUris.add(expandedURL.replaceFirst("photozou\\.jp/photo/show/[0-9]*/", "photozou.jp/p/img/"));
                thumbnailUris.add(expandedURL.replaceFirst("photozou\\.jp/photo/show/[0-9]*/", "photozou.jp/p/thumb/"));
            } else {
                uris.add(expandedURL);
            }
        }
    }


    //getter

    public List<String> getDestinationUserNames() {
        List<String> destinationUserNames = new ArrayList<>();
        destinationUserNames.addAll(this.destinationUserNames);
        return destinationUserNames;
    }


    public boolean isRT() {
        return retweetedStatusId != -1;
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

    public List<String> getThumbnailUris() {
        List<String> thumbnailUris = new ArrayList<>();
        thumbnailUris.addAll(this.thumbnailUris);
        return thumbnailUris;
    }


    public List<String> getHashtags() {
        List<String> hashtags = new ArrayList<>();
        hashtags.addAll(this.hashtags);
        return hashtags;
    }

    public List<String> getInvolvedUserNames() {
        List<String> involvedUserNames1 = new ArrayList<>();
        involvedUserNames1.addAll(involvedUserNames);
        return involvedUserNames1;
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
        return isSameTweet && ((SimpleTweetStatus) o).getOwnerUser().getAccessToken().getUserId() == this.ownerUser.getAccessToken().getUserId();
    }
}