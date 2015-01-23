package com.serori.numeri.color;

/**
 * ColorManagerItem
 */
public class ColorManagerItem {
    private String colorValue;
    private Color color;

    public ColorManagerItem(Color color) {
        this.colorValue = color.getColor();
        this.color = color;
    }


    public void setColorValue(String color) {
        this.colorValue = color;
    }

    public String getColorValue() {
        return this.colorValue;
    }

    public Color getColor() {
        return color;
    }
}
