package com.wonkglorg.loginfx.enums;

public enum Scene {

    MAIN("Main", "/fxml/main.fxml");


    private final String name;
    private final String path;


    Scene(String name, String path) {
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