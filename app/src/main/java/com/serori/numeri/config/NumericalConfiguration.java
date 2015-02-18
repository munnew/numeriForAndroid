package com.serori.numeri.config;

/**
 * 数値的な情報をもつ設定
 */
public interface NumericalConfiguration {
    /**
     * 設定のidを取得する
     *
     * @return id
     */
    String getId();

    /**
     * 数値を取得する
     *
     * @return 設定された数値
     */
    int getNumericValue();

    /**
     * 数値をセットする
     *
     * @param numericValue 設定する数値
     */

    void setNumericValue(int numericValue);
}
