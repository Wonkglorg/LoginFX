package com.wonkglorg.loginfx;

import com.wonkglorg.fxutility.manager.ManagedApplication;
import com.wonkglorg.loginfx.constants.Nodes;
import com.wonkglorg.loginfx.constants.Scenes;
import com.wonkglorg.loginfx.manager.SessionManager;
import javafx.stage.Stage;

public class Application extends ManagedApplication {


    @Override
    public void applicationStart(Stage stage) {
        addNodes();
        addScenes();

        loadScene(Scenes.BORDER.getName());

    }

    /**
     * Add all scenes to the application.
     */
    private void addScenes() {
        for (var scene : Scenes.values()) {
            addScene(scene.getName(), Application.class.getResource(scene.getPath()));
        }


    }

    /**
     * Add all nodes to the application.
     */
    private void addNodes() {
        for (var node : Nodes.values()) {
            addNode(node.getName(), Application.class.getResource(node.getPath()));
        }
    }
}