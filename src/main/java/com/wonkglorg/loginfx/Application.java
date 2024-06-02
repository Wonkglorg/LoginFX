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


        //sessionManager.deleteUser("58c38e19-727d-4d51-9c60-5cb8be963608");
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