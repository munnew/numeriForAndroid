package com.serori.numeri.config;

/**
 * 数値的な情報をもつ設定
 */
public interface NumericalConfiguration {
    String getId();

    int getNumericValue();

    void setNumericValue(int numericValue);
}
