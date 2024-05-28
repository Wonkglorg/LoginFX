package com.wonkglorg.loginfx.constants;

public enum Scenes {

    BORDER("Border", "/fxml/border.fxml");


    private final String name;
    private final String path;


    Scenes(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}