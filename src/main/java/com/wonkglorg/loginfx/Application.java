package com.wonkglorg.loginfx;

import com.wonkglorg.fxutility.manager.ManagedApplication;
import com.wonkglorg.loginfx.enums.Node;
import com.wonkglorg.loginfx.enums.Scene;
import javafx.stage.Stage;

public class Application extends ManagedApplication {

    @Override
    public void applicationStart(Stage stage) {
        addNodes();
        addScenes();

        loadScene(Scene.MAIN.getName());
        setNodeVisibility(Node.LOGIN.getName(), Node.LOGIN.getNode());
    }

    private void addScenes() {
        for (var scene : Scene.values()) {
            addScene(scene.getName(), Application.class.getResource(scene.getPath()));
        }


    }

    private void addNodes() {
        for (var node : Node.values()) {
            addNode(node.getName(), Application.class.getResource(node.getPath()));
        }
    }
}