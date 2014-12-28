package com.serori.numeri.item;

import android.text.Html;
import android.util.Log;

import com.serori.numeri.user.NumeriUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
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
    private UserMentionEntity[] mentionEntity;
    private boolean isRT = false, isMention = false, isFavorite = false;
    private String createdTime;
    List<String> destinationUserNames = new ArrayList<>();

   public TimeLineItem(Status status, NumeriUser numeriUser) {
        statusId = status.getId();
        userId = status.getUser().getId();//4 -19
        createdTime = new SimpleDateFormat(DATE_FORMAT).format(status.getCreatedAt());
        isRT = status.isRetweetedByMe();
        isFavorite = status.isFavorited();
        if (status.isRetweet()) { //RT
            iconImageUrl = status.getRetweetedStatus().getUser().getProfileImageURL();
            mainText = status.getRetweetedStatus().getText();
            name = status.getRetweetedStatus().getUser().getName();
            via = "via " + Html.fromHtml(status.getRetweetedStatus().getSource()).toString() + " RT by " + status.getUser().getScreenName();
            screenName = status.getRetweetedStatus().getUser().getScreenName();
        } else {//!RT
            iconImageUrl = status.getUser().getProfileImageURL();
            mainText = status.getText();
            name = status.getUser().getName();
            via = "via " + Html.fromHtml(status.getSource()).toString();
            screenName = status.getUser().getScreenName();
        }

        mentionEntity = status.getUserMentionEntities();
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

    public UserMentionEntity[] getMentionEntity() {
        return mentionEntity;
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
}
