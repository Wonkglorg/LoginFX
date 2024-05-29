package com.wonkglorg.loginfx;

import com.wonkglorg.fxutility.manager.ManagedApplication;
import com.wonkglorg.loginfx.constants.Scenes;
import javafx.stage.Stage;

public class Application extends ManagedApplication {


    @Override
    public void applicationStart(Stage stage) {
        stage.setResizable(false);
        addScenes();

        loadScene(Scenes.LOGIN.getName());

    }

    /**
     * Add all scenes to the application.
     */
    private void addScenes() {
        for (var scene : Scenes.values()) {
            addScene(scene.getName(), Application.class.getResource(scene.getPath()));
        }


    }
}