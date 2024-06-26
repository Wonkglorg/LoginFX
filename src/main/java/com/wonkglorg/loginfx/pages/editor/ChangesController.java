package com.wonkglorg.loginfx.pages.editor;

import com.wonkglorg.fxutility.manager.ManagedController;
import com.wonkglorg.loginfx.manager.SessionManager;
import com.wonkglorg.loginfx.objects.UserData;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ChangesController extends ManagedController {
    private SessionManager sessionManager = SessionManager.getInstance();
    private Map<String, Map.Entry<String, String>> changes = new HashMap<>();
    private File profilePictureFile;
    private UserData userData;

    @FXML
    VBox changesBox;

    @Override
    public void update() {

    }

    @FXML
    public void initialize() {

    }

    /**
     * Entry point should always be called first
     *
     * @param changes        the changes that should be displayed
     * @param data           the data that should be updated with the password already hashed
     * @param profilePicture the profile picture that should be updated
     */
    public void setChanges(Map<String, Map.Entry<String, String>> changes, UserData data, File profilePicture) {
        this.changes = changes;
        this.userData = data;
        this.profilePictureFile = profilePicture;
        prepareChangeMenu(changes);
    }


    /**
     * Confirms the changes and updates the user data
     */
    public void confirm() {
        sessionManager.updateUserData(userData, profilePictureFile);
        changesBox.getChildren().clear();
        profilePictureFile = null;
        userData = null;
        changes.clear();
        closeWindow();
    }

    private void closeWindow() {
        changesBox.getScene().getWindow().hide();
    }


    /**
     * Prepares the change menu sets the fields etc
     *
     * @param changes the changes that should be displayed
     */
    private void prepareChangeMenu(Map<String, Map.Entry<String, String>> changes) {
        changesBox.getChildren().clear();
        for (var change : changes.entrySet()) {
            changesBox.getChildren().add(createChangeRow(change.getKey(), change.getValue()));
        }
        Button confirm = new Button("Confirm");
        confirm.setOnAction(event -> confirm());
        changesBox.getChildren().add(confirm);
    }

    /**
     * Helper Method to create a change row
     *
     * @param name   the name of the change
     * @param change the change
     * @return the created row
     */
    private HBox createChangeRow(String name, Map.Entry<String, String> change) {
        var row = new HBox();
        Label label = new Label(name);
        label.setPadding(new Insets(0, 10, 0, 0));
        row.getChildren().add(label);
        Label changeLabel = new Label(change.getKey() + " -> " + change.getValue());
        changeLabel.setPadding(new Insets(0, 0, 0, 10));
        row.getChildren().add(changeLabel);
        return row;
    }
}
