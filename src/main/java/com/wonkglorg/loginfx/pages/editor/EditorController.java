package com.wonkglorg.loginfx.pages.editor;

import com.wonkglorg.fxutility.manager.ManagedController;
import com.wonkglorg.loginfx.Application;
import com.wonkglorg.loginfx.constants.Scenes;
import com.wonkglorg.loginfx.manager.SessionManager;
import com.wonkglorg.loginfx.objects.Action;
import com.wonkglorg.loginfx.objects.UserData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class EditorController extends ManagedController {

    @FXML
    private ImageView profilePicture;
    @FXML
    private Label accountCreatedLabel;
    @FXML
    private Button editButton;
    @FXML
    private TextField username, firstName, lastName, email, phoneNumber, street, streetNumber, city, zipCode, password, passwordRepeat;
    @FXML
    private DatePicker birthday;
    @FXML
    private ComboBox<String> federalState;
    @FXML
    private ToggleGroup gender;
    @FXML
    TableView<Action> historyTable;
    @FXML
    private Button deleteButton;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0\\d{3}-\\d{3}-\\d{2}-\\d{2}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-\\.]).{8,}$");
    private static final Border ERROR_BORDER = new Border(new BorderStroke(Paint.valueOf("RED"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));

    private final Map<Control, String> fields = new HashMap<>();
    private File profilePictureFile;
    private UserData user;
    private boolean editing = false;

    private SessionManager sessionManager = SessionManager.getInstance();


    @Override
    public void update() {

    }

    @FXML
    public void initialize() {
        federalState.getItems().addAll("Salzburg", "Tirol", "Vorarlberg", "Wien", "Niederösterreich", "Oberösterreich", "Steiermark", "Kärnten", "Burgenland");
        fields.put(username, "Username is required");
        fields.put(firstName, "First name is required");
        fields.put(lastName, "Last name is required");
        fields.put(email, "Email is required");
        fields.put(phoneNumber, "Phone number is required");
        fields.put(birthday, "Birthday is required");
        fields.put(street, "Street is required");
        fields.put(streetNumber, "Street number is required");
        fields.put(city, "City is required");
        fields.put(zipCode, "Zip code is required");
        fields.put(federalState, "Federal state is required");
    }

    /**
     * Sets the data of the user to the fields in the editor
     */
    private void setData() {
        profilePicture.setImage(UserData.getImage(user.getProfileImage()));
        username.setText(user.username());
        firstName.setText(user.firstName());
        lastName.setText(user.lastName());
        email.setText(user.email());
        phoneNumber.setText(user.phoneNumber());
        birthday.setValue(user.birthday().toLocalDate());
        street.setText(user.street());
        streetNumber.setText(user.streetNr());
        city.setText(user.country());
        zipCode.setText(user.zipCode());
        federalState.getSelectionModel().select(user.federalState());
        accountCreatedLabel.setText("Account created: " + sessionManager.getAccountCreationDate(user.username()));
    }

    /**
     * Clears all fields in the form.
     */
    public void clear() {
        profilePicture.setImage(null);
        username.clear();
        firstName.clear();
        lastName.clear();
        email.clear();
        phoneNumber.clear();
        birthday.getEditor().clear();
        street.clear();
        streetNumber.clear();
        city.clear();
        zipCode.clear();
        federalState.getSelectionModel().clearSelection();
        federalState.setPromptText("Federal State");
        password.clear();
        passwordRepeat.clear();
    }

    /**
     * Clears all errors in the form.
     */
    public void clearErrors() {
        fields.keySet().forEach(control -> {
            control.setTooltip(null);
            control.setBorder(null);
        });
    }

    /**
     * Starts the checks for the user update if all required fields are filled and if the data is valid. Prompts the next popup menu
     */
    public void updateUser() {
        if (!editing) {
            return;
        }

        clearErrors();
        if (!checkIfRequiredFieldsAreFilled()) {
            return;
        }


        if (password.getText() != null && !password.getText().isEmpty()) {

            if (!PASSWORD_PATTERN.matcher(password.getText()).matches()) {
                showError(password, "Password must contain at least 8 characters, one uppercase letter, one lowercase letter, one number and one special character");
                return;
            }

            if (!password.getText().equals(passwordRepeat.getText())) {
                showError(passwordRepeat, "Passwords do not match");
                return;
            }
        }


        if (!EMAIL_PATTERN.matcher(email.getText()).matches()) {
            showError(email, "Invalid email");
            return;
        }

        if (!PHONE_PATTERN.matcher(phoneNumber.getText()).matches()) {
            showError(phoneNumber, "Invalid phone number format: 0XXX-XXX-XX-XX");
            return;
        }

        if (!user.username().equals(username.getText()) && sessionManager.usernameExists(username.getText())) {
            showError(username, "Username already exists");
            return;
        }

        if (!user.email().equals(email.getText()) && sessionManager.emailExists(email.getText())) {
            showError(email, "Email already exists");
            return;
        }


        var changes = changes(user, createNewUserData());

        if (changes.isEmpty()) {
            System.out.println("No changes");
            return;
        }

        var scene = Application.getInstance().getScene(Scenes.CHANGES.getName());

        Stage stage = new Stage();
        scene.getValue().<ChangesController>getController().setChanges(changes, user, profilePictureFile == null ? null : profilePictureFile);

        stage.setScene(scene.getKey());
        stage.show();
    }

    /**
     * Utility method to show an error message for a specific control.
     *
     * @param control The control to show the error message for.
     * @param message The error message to show.
     */
    private void showError(Control control, String message) {
        Tooltip tooltip = new Tooltip(message);
        tooltip.setShowDelay(javafx.util.Duration.ZERO);
        control.setTooltip(tooltip);
        control.setBorder(ERROR_BORDER);
    }

    /**
     * Creates a new UserData object with the data from the form.
     *
     * @return the created UserData object
     */
    protected UserData createNewUserData() {
        String password = this.password.getText();
        if (password.isEmpty()) {
            password = user.password();
        }

        BufferedImage profileImage;

        if (profilePictureFile == null) {
            profileImage = user.getProfileImage();
        } else {


            try {
                profileImage = ImageIO.read(profilePictureFile.toURI().toURL());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        return new UserData(user.userID(), username.getText(), firstName.getText(), lastName.getText(), phoneNumber.getText(), street.getText(), streetNumber.getText(), city.getText(), zipCode.getText(), federalState.getSelectionModel().getSelectedItem(), Date.valueOf(birthday.getValue()),
                password, gender.getSelectedToggle().getUserData().toString().charAt(0), email.getText(), profileImage, user.imageName())
                ;
    }


    /**
     * Compares the original data with the new data and returns a map with the changes.
     *
     * @param originalData the original data
     * @param newData      the new data
     * @return a map with the changes
     */

    protected Map<String, Map.Entry<String, String>> changes(UserData originalData, UserData newData) {
        Map<String, Map.Entry<String, String>> changes = new HashMap<>();
        if (!originalData.username().equals(newData.username())) {
            changes.put("username", Map.entry(originalData.username(), newData.username()));
        }
        if (!originalData.firstName().equals(newData.firstName())) {
            changes.put("firstName", Map.entry(originalData.firstName(), newData.firstName()));
        }
        if (!originalData.lastName().equals(newData.lastName())) {
            changes.put("lastName", Map.entry(originalData.lastName(), newData.lastName()));
        }
        if (!originalData.phoneNumber().equals(newData.phoneNumber())) {
            changes.put("phoneNumber", Map.entry(originalData.phoneNumber(), newData.phoneNumber()));
        }
        if (!originalData.street().equals(newData.street())) {
            changes.put("street", Map.entry(originalData.street(), newData.street()));
        }
        if (!originalData.streetNr().equals(newData.streetNr())) {
            changes.put("streetNr", Map.entry(originalData.streetNr(), newData.streetNr()));
        }
        if (!originalData.country().equals(newData.country())) {
            changes.put("country", Map.entry(originalData.country(), newData.country()));
        }
        if (!originalData.zipCode().equals(newData.zipCode())) {
            changes.put("zipCode", Map.entry(originalData.zipCode(), newData.zipCode()));
        }
        if (!originalData.federalState().equals(newData.federalState())) {
            changes.put("federalState", Map.entry(originalData.federalState(), newData.federalState()));
        }
        if (!originalData.birthday().equals(newData.birthday())) {
            changes.put("birthday", Map.entry(originalData.birthday().toString(), newData.birthday().toString()));
        }
        if (!originalData.email().equals(newData.email())) {
            changes.put("email", Map.entry(originalData.email(), newData.email()));
        }
        if (!originalData.profileImage().equals(newData.profileImage())) {
            changes.put("profilePictureName", Map.entry("", ""));
        }
        if (!originalData.password().equals(newData.password())) {
            changes.put("password", Map.entry(originalData.password(), newData.password()));
        }
        return changes;
    }

    /**
     * Gets the image from a file
     *
     * @param file
     * @return {@link BufferedImage}
     */
    private BufferedImage getImage(File file) {
        try {
            return ImageIO.read(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Sets the history of the user to the history table
     */
    public void setHistory() {
        var actions = sessionManager.getActions(user.userID());
        prepareColumns();

        for (var action : actions) {
            historyTable.getItems().add(action);
        }
    }


    /**
     * Prepares the columns for the history table needed to make it work with Action type instead of having to manually add them
     */
    protected void prepareColumns() {
        historyTable.getColumns().clear();
        List<TableColumn<Action, String>> columns = List.of(createColumn("User ID", "userID", 125), createColumn("Action Message", "actionMessage", 400), createColumn("Action Type", "actionType", 125), createColumn("Time", "actionTime", 150), createColumn("Status", "actionStatus", 100));

        historyTable.getColumns().addAll(columns);
    }

    /**
     * Clears the history table
     */
    protected void clearHistory() {
        historyTable.getItems().clear();
    }

    /**
     * Opens a file chooser to choose an image for the profile picture
     */
    public void chooseImage() {
        if (!editing) return;
        Application.getChooser().fileChooser(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")).ifPresent(file -> {
            profilePictureFile = file;
            profilePicture.setImage(new Image(file.toURI().toString()));
        });
    }

    /**
     * Toggles the editing mode
     */
    public void edit() {
        edit(!editing);
    }

    /**
     * Toggles the editing mode
     *
     * @param editing the editing mode
     */
    public void edit(boolean editing) {
        this.editing = editing;
        setFieldsEditable(editing);
        editButton.setText(editing ? "Cancel" : "Edit");

        //reverts the changes if the user cancels the editing
        if (!editing) {
            setData();
        }

    }

    /**
     * Sets the fields to editable or not editable
     *
     * @param editable true if the fields should be editable, false otherwise
     */
    private void setFieldsEditable(boolean editable) {
        username.setEditable(editable);
        username.setDisable(!editable);
        firstName.setEditable(editable);
        firstName.setDisable(!editable);
        lastName.setEditable(editable);
        lastName.setDisable(!editable);
        email.setEditable(editable);
        email.setDisable(!editable);
        phoneNumber.setEditable(editable);
        phoneNumber.setDisable(!editable);
        birthday.setEditable(editable);
        birthday.setDisable(!editable);
        street.setEditable(editable);
        street.setDisable(!editable);
        streetNumber.setEditable(editable);
        streetNumber.setDisable(!editable);
        city.setEditable(editable);
        city.setDisable(!editable);
        zipCode.setEditable(editable);
        zipCode.setDisable(!editable);
        federalState.setEditable(editable);
        federalState.setDisable(!editable);
        password.setEditable(editable);
        password.setDisable(!editable);
        passwordRepeat.setEditable(editable);
        passwordRepeat.setDisable(!editable);
    }


    /**
     * Creates a column for the history table
     *
     * @param name  the name of the column
     * @param value the value of the column
     * @param width the width of the column
     * @return the created column
     */
    protected TableColumn<Action, String> createColumn(String name, String value, int width) {
        var column = new TableColumn<Action, String>(name);
        column.setPrefWidth(width);
        column.setCellValueFactory(new PropertyValueFactory<>(value));
        return column;
    }

    /**
     * Sets the user for the editor, this is meant as the entry point for the editor without this the editor will not work correctly
     *
     * @param user
     */
    public void setUser(UserData user) {
        this.user = user;
        setData();
        setHistory();
        edit(false);
    }

    /**
     * Goes back to the login page and clears the session
     */
    public void backToLogin() {
        clear();
        clearHistory();
        Application.getInstance().loadScene(Scenes.LOGIN.getName());
    }

    /**
     * Checks if all required fields are filled and shows error messages if not.
     *
     * @return true if all required fields are filled, false otherwise.
     */

    private boolean checkIfRequiredFieldsAreFilled() {
        boolean allFieldsFilled = true;

        for (Map.Entry<Control, String> entry : fields.entrySet()) {
            Control control = entry.getKey();
            boolean isEmpty = false;

            if (control instanceof TextField textField) {
                isEmpty = textField.getText().isEmpty();
            } else if (control instanceof ComboBox<?> comboBox) {
                isEmpty = comboBox.getSelectionModel().isEmpty();
            } else if (control instanceof DatePicker datePicker) {
                isEmpty = datePicker.getValue() == null;
            }

            if (isEmpty) {
                showError(control, entry.getValue());
                allFieldsFilled = false;
            }
        }

        return allFieldsFilled;
    }

    private boolean deleteConfirmation = false;

    /**
     * Deletes the user this method has to be called twice by the user to actually delete the user
     */
    public void delete() {

        if (!deleteConfirmation) {
            deleteConfirmation = true;
            deleteButton.setText("Confirm Delete");
            startConfirmationTask();
            return;
        }

        deleteConfirmation = false;


        sessionManager.deleteUser(user.userID());
        deleteButton.setText("Delete");
        backToLogin();
    }


    /**
     * Starts a task that will revert the delete button to the original state after 5 seconds
     */
    private void startConfirmationTask() {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                Platform.runLater(() -> {
                    deleteConfirmation = false;
                    deleteButton.setText("Delete");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
