package com.wonkglorg.loginfx.pages.register;

import com.wonkglorg.fxutility.manager.ManagedController;
import com.wonkglorg.loginfx.Application;
import com.wonkglorg.loginfx.manager.SessionManager;
import com.wonkglorg.loginfx.objects.UserData;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterController extends ManagedController {

    @FXML
    protected ImageView profilePicture;
    @FXML
    protected TextField username;
    @FXML
    protected TextField firstName;
    @FXML
    protected TextField lastName;
    @FXML
    protected TextField email;

    @FXML
    protected TextField phoneNumber;

    @FXML
    protected DatePicker birthday;

    @FXML
    protected TextField street;
    @FXML
    protected TextField streetNumber;
    @FXML
    protected TextField city;
    @FXML
    protected TextField zipCode;

    @FXML
    protected ComboBox<String> federalState;

    @FXML
    protected ToggleGroup gender;


    @FXML
    protected TextField password;
    @FXML
    protected TextField passwordRepeat;

    //Map of all the fields and a message if they are required
    private Map<Control, String> fields;
    private SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public void update() {

    }

    @FXML
    public void initialize() {
        federalState.getItems().addAll("Salzburg", "Tirol", "Vorarlberg", "Wien", "Niederösterreich", "Oberösterreich", "Steiermark", "Kärnten", "Burgenland");
        fields = new HashMap<>();
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
        fields.put(password, "Password is required");
        fields.put(passwordRepeat, "Password repeat is required");
    }


    /**
     * Clear all fields.
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
     * Opens a file chooser Dialog to choose an image for the profile picture (png,jpg,jpeg).
     */
    public void chooseImage() {
        Application.getChooser().fileChooser(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")).ifPresent(file -> profilePicture.setImage(new Image(file.toURI().toString())));
    }

    private final Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private final Pattern phoneNumberPattern = Pattern.compile("^0\\d{3}-\\d{3}-\\d{2}-\\d{2}$");

    private void validateUserInput() {
        if (!checkIfRequiredFieldsAreFilled()) {
            return;
        }

        if (!phoneNumberPattern.matcher(phoneNumber.getText()).find()) {
            phoneNumber.setTooltip(new Tooltip("Invalid phone number"));
            return;
        }
        if (!sessionManager.isValidUser(username.getText(), password.getText())) {
            username.setTooltip(new Tooltip("Username already exists"));
            return;
        }


        if (!password.getText().

                equals(passwordRepeat.getText())) {
            passwordRepeat.setTooltip(new Tooltip("Passwords do not match"));
            return;
        }

    }

    private Border errorBorder = new Border(new BorderStroke(Paint.valueOf("RED"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));

    private Tooltip createErrorToolTip(String message) {
        Tooltip tooltip = new Tooltip();
        tooltip.setShowDelay(javafx.util.Duration.ZERO);
        tooltip.setText(message);
        return tooltip;
    }

    private void showError(Control node, String message) {
        node.setTooltip(createErrorToolTip(message));
        node.setBorder(errorBorder);
    }

    /**
     * Check if all required fields are filled.
     *
     * @return true if all required fields are filled, false otherwise
     */
    private boolean checkIfRequiredFieldsAreFilled() {
        boolean allFieldsFilled = true;

        for (Map.Entry<Control, String> entry : fields.entrySet()) {
            if (entry.getKey() instanceof TextField textField) {
                if (textField.getText().isEmpty()) {
                    showError(textField, entry.getValue());
                    allFieldsFilled = false;
                }
            } else if (entry.getKey() instanceof ComboBox comboBox) {
                if (comboBox.getSelectionModel().isEmpty()) {
                    showError(comboBox, entry.getValue());
                    allFieldsFilled = false;
                }
            } else if (entry.getKey() instanceof DatePicker datePicker) {
                if (datePicker.getValue() == null) {
                    showError(datePicker, entry.getValue());
                    allFieldsFilled = false;
                }
            }
        }

        return allFieldsFilled;
    }

    /**
     * Clear all errors.
     */
    public void clearErrors() {
        for (Control control : fields.keySet()) {
            control.setTooltip(null);
            control.setBorder(null);
        }
    }

    private Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    /**
     * Register a new user.
     */
    public void register() {
        clearErrors();
        validateUserInput();

        var passwordRepeatString = passwordRepeat.getText();

        UserData userData = new UserData(
                username.getText(),
                firstName.getText(),
                lastName.getText(),
                phoneNumber.getText(),
                street.getText(),
                streetNumber.getText(),
                city.getText(),
                zipCode.getText(),
                federalState.getValue(),
                birthday.getValue().toString(),
                password.getText(),
                gender.getSelectedToggle().toString(),
                email.getText(),
                new Image(profilePicture.getImage().getUrl()));

        if (passwordPattern.matcher(userData.getPassword()).find()) {
            showError(password, "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character");
            return;
        }

        if (!userData.getPassword().equals(passwordRepeatString)) {
            showError(passwordRepeat, "Passwords do not match");
            return;
        }

        if (!isValidEmail(userData.getEmail())) return;
        if (!isValidPhoneNumber(userData.getPhoneNumber())) return;
        if (!isValidUsername(userData.getUsername())) return;


        sessionManager.registerUser(userData);

        Stage stage = (Stage) profilePicture.getScene().getWindow();
        stage.close();
    }

    private boolean isValidEmail(String email) {
        return emailPattern.matcher(email).find() && !sessionManager.emailExists(email);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumberPattern.matcher(phoneNumber).find();
    }

    private boolean isValidUsername(String username) {
        return !sessionManager.usernameExists(username);
    }


    /**
     * Go back to the login page.
     */
    public void backToLogin() {
        Stage stage = (Stage) profilePicture.getScene().getWindow();
        stage.close();
    }
}
