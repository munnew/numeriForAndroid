package com.serori.numeri.oauth;

import android.media.Image;

/**
 * Created by seroriKETC on 2014/12/19.
 */
public class NumeriUserListItem {

    private Image image;
    private String screenName;

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
    }
}
