package com.wonkglorg.loginfx.constants;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public enum Nodes {

    LOGIN("Login", "/fxml/login.fxml", AnchorPane.class),
    //REGISTER("Register", "/fxml/register.fxml", Pane.class);
    EDITOR("Editor", "/fxml/editor.fxml", Pane.class),

    ;

    private final String name;
    private final String path;

    public final Class<? extends javafx.scene.Node> clazz;

    Nodes(String name, String path, Class<? extends javafx.scene.Node> clazz) {
        this.name = name;
        this.path = path;
        this.clazz = clazz;
    }


    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public Class<? extends Node> getNode() {
        return clazz;
    }

}
