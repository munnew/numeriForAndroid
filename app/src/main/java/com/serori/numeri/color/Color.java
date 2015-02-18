package com.serori.numeri.color;

/**
 * 色を表すインターフェース
 */
public interface Color {
    /**
     * 色をセットします
     *
     * @param color 16進数で表された色<br>
     *              例)#FFFFFF
     */
    void setColor(String color);

    /**
     * 色の情報を取得
     *
     * @return 16進数で表された色
     */
    String getColor();

    /**
     * 色のIdを取得する
     *
     * @return id
     */
    String getColorId();
}
