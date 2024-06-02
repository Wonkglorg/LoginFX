package com.wonkglorg.loginfx;

import com.wonkglorg.fxutility.manager.ManagedApplication;
import com.wonkglorg.loginfx.constants.Scenes;
import com.wonkglorg.loginfx.manager.SessionManager;
import javafx.stage.Stage;

public class Application extends ManagedApplication {

    SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public void applicationStart(Stage stage) {
        stage.setResizable(false);
        addScenes();

        //sessionManager.deleteUser("8eac4572-8224-425f-93e4-c7a89894d68b");
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