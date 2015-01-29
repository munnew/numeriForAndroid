package com.serori.numeri.listview.item;

import android.text.Html;

import com.serori.numeri.user.NumeriUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;

/**
 * タイムラインに表示するアイテムのモデルクラス
 */
public class TimeLineItem {
    private String iconImageUrl;
    private String name, mainText, via;
    private long statusId, userId;
    private static final String DATE_FORMAT = "MM/dd HH:mm:ss";
    private String screenName;
    private boolean isRT = false, isMention = false, isFavorite = false;
    private boolean isRetweeted = false;
    private boolean isMyTweet = false;
    private String createdTime;
    private List<String> destinationUserNames = new ArrayList<>();
    private List<String> uris = new ArrayList<>();
    private List<String> mediaUris = new ArrayList<>();
    private Long conversationId;

    public TimeLineItem(Status status, NumeriUser numeriUser) {
        statusId = status.getId();
        userId = status.getUser().getId();
        createdTime = new SimpleDateFormat(DATE_FORMAT).format(status.getCreatedAt());
        isRT = status.isRetweetedByMe();
        isFavorite = status.isFavorited();
        if (status.isRetweet()) { //RT
            isRetweeted = true;
            iconImageUrl = status.getRetweetedStatus().getUser().getBiggerProfileImageURL();
            mainText = status.getRetweetedStatus().getText();
            name = status.getRetweetedStatus().getUser().getName();
            via = "via " + Html.fromHtml(status.getRetweetedStatus().getSource()).toString() + " RT by " + status.getUser().getScreenName();
            screenName = status.getRetweetedStatus().getUser().getScreenName();
        } else {//!RT
            iconImageUrl = status.getUser().getBiggerProfileImageURL();
            mainText = status.getText();
            name = status.getUser().getName();
            via = "via " + Html.fromHtml(status.getSource()).toString();
            screenName = status.getUser().getScreenName();
            if (numeriUser.getAccessToken().getUserId() == status.getUser().getId()) {
                isMyTweet = true;
            }
        }

        UserMentionEntity[] mentionEntity = status.getUserMentionEntities();
        destinationUserNames.add(screenName);
        for (UserMentionEntity userMentionEntity : mentionEntity) {//?Mention
            if (userMentionEntity.getId() == numeriUser.getAccessToken().getUserId()) {
                isMention = true;
            } else if (!userMentionEntity.getScreenName().equals(screenName)) {
                destinationUserNames.add(userMentionEntity.getScreenName());
            }
        }
        name = name.replaceAll("\r", "");
        name = name.replaceAll("\n", "");
        name = name.replaceAll("\t", "");
        conversationId = status.getInReplyToStatusId();

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

    ///setter
    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public void setRT(boolean isRT) {
        this.isRT = isRT;
    }

    ///getter
    public long getStatusId() {
        return statusId;
    }

    public String getScreenName() {
        return screenName;
    }

    public long getUserId() {
        return userId;
    }

    public boolean isRT() {
        return isRT;
    }

    public boolean isMention() {
        return isMention;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public String getcreatedTime() {
        return createdTime;
    }

    public String getIconImageUrl() {
        return iconImageUrl;
    }

    public boolean isRetweeted() {
        return isRetweeted;
    }

    public Long getConversationId() {
        return conversationId;
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
}
