package com.serori.numeri.main.manager;

/**
 * FragmentManagerActivityに表示されるリストのセルが持つモデル
 */
public class FragmentManagerItem {
    private String fragmentName;
    private String fragmentKey;

    /**
     * コンストラクタ
     *
     * @param fragmentName
     */
    public FragmentManagerItem(String fragmentName) {
        this.fragmentName = fragmentName;
    }

    /**
     * Fragmentのキーをセットする
     *
     * @param fragmentKey キー
     */
    public void setFragmentKey(String fragmentKey) {
        this.fragmentKey = fragmentKey;
    }

    /**
     * フラグメントの名前を取得する
     *
     * @return 名前
     */
    public String getFragmentName() {
        return fragmentName;
    }

    /**
     * Fragmentのキーを取得する
     *
     * @return
     */
    public String getFragmentKey() {
        return fragmentKey;
    }
}
