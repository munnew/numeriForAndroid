package com.serori.numeri.fragment;

import android.text.Html;
import android.util.Log;

import com.serori.numeri.user.NumeriUser;

import java.text.SimpleDateFormat;

import twitter4j.Status;
import twitter4j.UserMentionEntity;

/**
 * タイムラインに表示するアイテムのモデルクラス
 */
public class TimeLineItem {
    // private Image IconImage = null;
    private String name, mainText, via;
    private long statusId, userId;
    private static final String DATE_FORMAT ="MM/dd HH:mm:ss";
    private String screenName;
    private UserMentionEntity[] mentionEntity;
    private boolean isRT = false, isMention = false, isFavorite = false;
    private String createdTime;
    TimeLineItem(Status status, NumeriUser numeriUser) {
        statusId = status.getId();
        userId = status.getUser().getId();//4 -19
        createdTime = new SimpleDateFormat(DATE_FORMAT).format(status.getCreatedAt());
        Log.v("created",createdTime);
        if (status.isRetweet()) { //RT
            isRT = true;
            mainText = status.getRetweetedStatus().getText();
            name = status.getRetweetedStatus().getUser().getName();
            via = "via " + Html.fromHtml(status.getRetweetedStatus().getSource()).toString() + " RT by " + status.getUser().getScreenName();
            screenName = status.getRetweetedStatus().getUser().getScreenName();
        } else {//!RT
            mainText = status.getText();
            name = status.getUser().getName();
            via = "via " + Html.fromHtml(status.getSource()).toString();
            screenName = status.getUser().getScreenName();
        }
        if (status.isFavorited()) {//?Favorited
            isFavorite = true;
        }
        mentionEntity = status.getUserMentionEntities();
        for (UserMentionEntity userMentionEntity : mentionEntity) {//?Mention
            if (userMentionEntity.getId() == numeriUser.getAccessToken().getUserId()) {
                isFavorite = true;
            }
        }
        name = name.replaceAll("\r", "");
        name = name.replaceAll("\n", "");
        name = name.replaceAll("\t", "");
    }

    public String getName() {
        return name;
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
    public String getcreatedTime(){
        return createdTime;
    }
}
