package com.wonkglorg.loginfx.constants;

public enum Scenes {

    LOGIN("Login", "/fxml/login.fxml"),

    REGISTER("Register", "/fxml/register.fxml"),
    EDITOR("Editor", "/fxml/editor.fxml"),
    ;

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