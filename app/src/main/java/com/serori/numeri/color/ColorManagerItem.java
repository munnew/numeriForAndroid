package com.serori.numeri.color;

/**
 * 色設定に表示されるリストのセルが持つモデルクラス
 */
public class ColorManagerItem {
    private String colorValue;
    private Color color;

    /**
     * Colorを元にインスタンスを生成する
     *
     * @param color 色
     */
    public ColorManagerItem(Color color) {
        this.colorValue = color.getColor();
        this.color = color;
    }

    /**
     * 色をセットします
     *
     * @param color 16進数で表された色<br>
     *              例)#FFFFFF
     */
    public void setColorValue(String color) {
        this.colorValue = color;
    }

    /**
     * 色の情報を取得
     *
     * @return 16進数で表された色
     */
    public String getColorValue() {
        return this.colorValue;
    }

    public Color getColor() {
        return color;
    }
}
