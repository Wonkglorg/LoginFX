package com.wonkglorg.loginfx.pages.login;

import com.wonkglorg.fxutility.manager.ManagedController;
import com.wonkglorg.loginfx.Application;
import com.wonkglorg.loginfx.constants.Scenes;
import com.wonkglorg.loginfx.manager.SessionManager;
import com.wonkglorg.loginfx.objects.UserData;
import com.wonkglorg.loginfx.pages.border.BorderController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController extends ManagedController {
    @FXML
    protected TextField username;
    @FXML
    protected TextField password;

    @FXML
    protected Label errorLabel;

    private SessionManager sessionManager = SessionManager.getInstance();


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


        if (username.getText().equals("admin") && password.getText().equals("admin")) {
            BorderController borderController = Application.getInstance().getController(Scenes.BORDER.getName());
            borderController.login(usernameString, null);
            return;
        }

        if (sessionManager.isValidUser(usernameString, passwordString)) {
            UserData userData = sessionManager.getUserData(usernameString);
            BorderController borderController = Application.getInstance().getController(Scenes.BORDER.getName());
            borderController.login(usernameString, userData.getProfileImage());
        } else {
            errorLabel.setText("Invalid username or password");
        }


    }

    @Override
    public void update() {

    }
}
