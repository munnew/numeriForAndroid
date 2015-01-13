package com.serori.numeri.color;

/**
 * 色を保存するクラス
 */
public class Colors {

    private String nomalColor;
    private String mentionColor;
    private String retweetColor;
    private String myTweetMarkColor;
    public static Colors getInstance() {
        return ColorsHolder.instance;
    }

    public String getNomalColor() {
        return nomalColor;
    }

    public void setNomalColor(String nomalColor) {
        this.nomalColor = nomalColor;
    }

    public String getMentionColor() {
        return mentionColor;
    }

    public void setMentionColor(String mentionColor) {
        this.mentionColor = mentionColor;
    }

    public String getRetweetColor() {
        return retweetColor;
    }

    public void setRetweetColor(String retweetColor) {
        this.retweetColor = retweetColor;
    }

    public String getMyTweetMarkColor() {
        return myTweetMarkColor;
    }

    public void setMyTweetMarkColor(String myTweetMarkColor) {
        this.myTweetMarkColor = myTweetMarkColor;
    }

    private static class ColorsHolder {
        private static final Colors instance = new Colors();
    }
}
