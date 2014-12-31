package com.serori.numeri.color;

/**
 * 色を保存するクラス
 */
public class Colors {

    private String nomalColor;
    private String mentionColor;
    private String reTweetColor;

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

    public String getReTweetColor() {
        return reTweetColor;
    }

    public void setReTweetColor(String reTweetColor) {
        this.reTweetColor = reTweetColor;
    }

    private static class ColorsHolder {
        private static final Colors instance = new Colors();
    }
}
