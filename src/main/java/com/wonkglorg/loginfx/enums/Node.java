package com.wonkglorg.loginfx.enums;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public enum Node {

    LOGIN("Login", "/fxml/login.fxml", AnchorPane.class),
    REGISTER("Register", "/fxml/register.fxml", Pane.class);

    private final String name;
    private final String path;

    private Class<? extends Node> node;

    Node(String name, String path, Class<? extends Node> node) {
        this.name = name;
        this.path = path;
        this.node = node;
    }


    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public Class<? extends Node> getNode() {
        return node;
    }

}
