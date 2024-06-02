package com.wonkglorg.loginfx.pages.login;

import com.wonkglorg.fxutility.manager.ManagedController;
import com.wonkglorg.loginfx.Application;
import com.wonkglorg.loginfx.constants.Scenes;
import com.wonkglorg.loginfx.manager.SessionManager;
import com.wonkglorg.loginfx.objects.Action;
import com.wonkglorg.loginfx.pages.editor.EditorController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController extends ManagedController {
    @FXML
    protected TextField username;
    @FXML
    protected TextField password;
    @FXML
    protected Label errorLabel;


    private SessionManager sessionManager = SessionManager.getInstance();


    /**
     * Logs the user into the application
     */
    public void login() {
        String usernameString;
        String passwordString;

        if (username.getText() == null) {
            errorLabel.setText("Username is required");
            return;
        } else {
            usernameString = username.getText();
        }

        if (password.getText() == null) {
            errorLabel.setText("Password is required");
            return;
        } else {
            passwordString = password.getText();
        }

        if (sessionManager.isValidUser(usernameString, passwordString)) {

        var user = sessionManager.getUserData(usernameString);

        Application.getInstance().<EditorController>getController(Scenes.EDITOR.getName()).setUser(user);
        Application.getInstance().loadScene(Scenes.EDITOR.getName());
        sessionManager.logAction(new Action(user.userID(), "Login", "successful login", usernameString, null));
    } else

    {
        errorLabel.setText("Invalid username or password");
    }


}

    /**
     * Opens the register window
     */
    public void register() {
        Stage stage = new Stage();
        stage.setScene(Application.getInstance().getScene(Scenes.REGISTER.getName()).getKey());
        stage.show();
    }

    @Override
    public void update() {

    }
}
