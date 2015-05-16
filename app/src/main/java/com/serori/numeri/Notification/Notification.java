package com.serori.numeri.Notification;

/**
 */
public enum Notification {
    FOLLOW("FOLLOW"),
    UN_FOLLOW("UN_FOLLOW"),
    RT("RT"),
    FAVORITE("FAVORITE"),
    UN_FAVORITE("UN_FAVORITE"),
    MENTION("MENTION");
    private boolean isEnable = true;
    private String id = "";

    Notification(String id) {
        this.id = id;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public String getId() {
        return id;
    }
}
