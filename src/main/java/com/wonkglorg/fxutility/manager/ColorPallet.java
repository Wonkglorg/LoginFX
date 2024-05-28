package com.wonkglorg.fxutility.manager;

public enum ColorPallet {

    NON("#ffffff", "#000000", "#ff0000", "#000000"),
    DARK("#333841", "#21252b", "#333841", "#cdd0d4"),
    ;
    private final String primary;
    private final String secondary;
    private final String highlight;
    private final String text;


    ColorPallet(String primary, String secondary, String highlight, String text) {
        this.primary = primary;
        this.secondary = secondary;
        this.highlight = highlight;
        this.text = text;
    }

    public String getPrimary() {
        return primary;
    }

    public String getSecondary() {
        return secondary;
    }

    public String getHighlight() {
        return highlight;
    }

    public String getText() {
        return text;
    }
}



