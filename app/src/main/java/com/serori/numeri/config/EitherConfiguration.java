package com.serori.numeri.config;

/**
 * どちらかを選択するような2値的な設定のインターフェース
 */
public interface EitherConfiguration {
    /**
     * 設定のidを取得する
     *
     * @return id
     */
    String getId();

    /**
     * 有効か無効かを取得する
     *
     * @return true : 有効<br>false : 無効
     */
    boolean isEnabled();

    /**
     * 有効化無効化をセットする
     *
     * @param enabled true : 有効<br>false : 無効
     */
    void setEnabled(boolean enabled);

}
