package com.wonkglorg.loginfx.pages.border;

import com.wonkglorg.fxutility.manager.ManagedController;
import com.wonkglorg.loginfx.Application;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class BorderController extends ManagedController {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private HBox userInfoBox;

    private final Image defaultProfilePicture = new Image(getClass().getResourceAsStream("/images/default.png"));
    private Image profilePicture = defaultProfilePicture;
    private String userName = "Guest";
    private boolean isLoggedIn = false;

    @FXML
    void initialize() {
        toggleLoggedIn(isLoggedIn);
        userInfoBox.setAlignment(Pos.CENTER_RIGHT);
    }

    public void showLoginPanel() {
        VBox loginPanel = Application.getInstance().getNode("Login", VBox.class);
        if (loginPanel != null) {
            mainBorderPane.setCenter(loginPanel);
        }
    }

    public void login(String user, Image image) {
        userName = user;
        profilePicture = image;
        if (!isLoggedIn) {
            toggleLoggedIn(true);
        }
    }

    public void logout() {
        toggleLoggedIn(false);
    }

    private void toggleLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
        userInfoBox.getChildren().clear();

        if (loggedIn) {
            userInfoBox.getChildren().addAll(createProfilePicture(), createUserNameLabel());
        } else {
            userInfoBox.getChildren().add(createLoginLabel());
            userName = "Guest";
            profilePicture = defaultProfilePicture;
        }
    }

    private Label createLoginLabel() {
        Label loginLabel = new Label("Login");
        loginLabel.getStyleClass().add("primary-text-color");
        loginLabel.setFont(new Font(23));
        loginLabel.setOnMouseClicked(event -> showLoginPanel());
        return loginLabel;
    }

    private ImageView createProfilePicture() {
        ImageView imageView = new ImageView(profilePicture);
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        return imageView;
    }

    private Label createUserNameLabel() {
        Label userNameLabel = new Label(userName);
        userNameLabel.setFont(new Font(23));
        return userNameLabel;
    }

    @Override
    public void update() {
        // Implementation for update if required
    }
}