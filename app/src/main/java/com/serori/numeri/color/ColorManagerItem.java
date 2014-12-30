package com.serori.numeri.color;

/**
 * Created by serioriKETC on 2014/12/30.
 */
public class ColorManagerItem {
    private String colorId;
    private String color;

    public ColorManagerItem(String colorId) {
        this.colorId = colorId;
    }

    public String getColorId() {
        return colorId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
