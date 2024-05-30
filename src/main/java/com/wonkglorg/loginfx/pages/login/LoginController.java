package com.wonkglorg.loginfx.pages.login;

import com.wonkglorg.fxutility.manager.ManagedController;
import com.wonkglorg.loginfx.Application;
import com.wonkglorg.loginfx.constants.Scenes;
import com.wonkglorg.loginfx.manager.SessionManager;
import com.wonkglorg.loginfx.pages.register.RegisterController;
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

            return;
        }

        if (sessionManager.isValidUser(usernameString, passwordString)) {
            var user = sessionManager.getUserData(usernameString);
        } else {
            errorLabel.setText("Invalid username or password");
        }


    }

    public void register() {
        Stage stage = new Stage();
        stage.setScene(Application.getInstance().getScene(Scenes.REGISTER.getName()).getKey());
        //Hacky solution otherwise errors persist over reopens of the menu
        Application.getInstance().getScene(Scenes.REGISTER.getName()).getValue().<RegisterController>getController().clearErrors();
        stage.show();
    }

    @Override
    public void update() {

    }
}
