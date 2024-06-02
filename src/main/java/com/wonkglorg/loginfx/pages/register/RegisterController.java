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

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.wonkglorg.loginfx.objects.UserData.hashPassword;

public class RegisterController extends ManagedController {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0\\d{3}-\\d{3}-\\d{2}-\\d{2}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-\\.]).{8,}$");
    private static final Border ERROR_BORDER = new Border(new BorderStroke(Paint.valueOf("RED"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));

    private final Map<Control, String> fields = new HashMap<>();
    private final SessionManager sessionManager = SessionManager.getInstance();

    @FXML
    private ImageView profilePicture;
    @FXML
    private TextField username, firstName, lastName, email, phoneNumber, street, streetNumber, city, zipCode, password, passwordRepeat;
    @FXML
    private DatePicker birthday;
    @FXML
    private ComboBox<String> federalState;
    @FXML
    private ToggleGroup gender;
    @FXML
    private Label errorLabel;
    private File profilePictureFile;

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
        fields.put(password, "Password is required");
        fields.put(passwordRepeat, "Password repeat is required");

        testValues();
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
     * Fills the form with test values.
     */

    private void testValues() {
        username.setText("Wonkglorg");
        firstName.setText("Dominik");
        lastName.setText("Meierhofer");
        email.setText("Wonkglorg.18@gmail.com");
        phoneNumber.setText("0676-480-00-27");
        birthday.getEditor().setText("06/06/2024");
        street.setText("Römergasse");
        streetNumber.setText("36");
        city.setText("Salzburg");
        zipCode.setText("5020");
        federalState.getSelectionModel().select("Salzburg");
        password.setText("Password.1");
        passwordRepeat.setText("Password.1");
    }

    /**
     * Opens a file chooser dialog to select a profile picture.
     */
    public void chooseImage() {
        Application.getChooser().fileChooser(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"))
                .ifPresent(file -> {
                    if (file.length() > 5000000) {
                        errorLabel.setText("File is too big, please select a file smaller than 5MB");
                        return;
                    }
                    profilePictureFile = file;
                    profilePicture.setImage(new Image(file.toURI().toString()));
                });
    }

    /**
     * Validates the user input and shows error messages if necessary.
     *
     * @return true if the input is valid, false otherwise.
     */

    private boolean validateUserInput() {


        if (profilePicture.getImage() == null) {
            errorLabel.setText("Please select a profile picture");
            return false;
        }

        if (!checkIfRequiredFieldsAreFilled()) {
            return false;
        }

        if (!PHONE_PATTERN.matcher(phoneNumber.getText()).matches()) {
            showError(phoneNumber, "Invalid phone number format: 0XXX-XXX-XX-XX");
            return false;
        }

        if (sessionManager.usernameExists(username.getText())) {
            showError(username, "Username already exists");
            return false;
        }

        if (!password.getText().equals(passwordRepeat.getText())) {
            showError(passwordRepeat, "Passwords do not match");
            return false;
        }

        if (!PASSWORD_PATTERN.matcher(password.getText()).matches()) {
            showError(password, "Password must be at least 8 long, contain at least one uppercase letter, one lowercase letter, one number, and one special character");
            return false;
        }

        if (!EMAIL_PATTERN.matcher(email.getText()).matches() || sessionManager.emailExists(email.getText())) {
            showError(email, "Invalid email or already in use!");
            return false;
        }


        return true;
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

    /**
     * Clears all error messages and borders from the form.
     */
    public void clearErrors() {
        errorLabel.setText("");
        fields.keySet().forEach(control -> {
            control.setTooltip(null);
            control.setBorder(null);
        });
    }

    /**
     * Registers a new user with the provided data.
     *
     * @throws IOException If the profile picture could not be read.
     */
    public void register() throws IOException {
        clearErrors();


        if (!validateUserInput()) {
            return;
        }


        String userID = UUID.randomUUID().toString();
        UserData userData = new UserData(
                userID,
                username.getText(),
                firstName.getText(),
                lastName.getText(),
                phoneNumber.getText(),
                street.getText(),
                streetNumber.getText(),
                city.getText(),
                zipCode.getText(),
                federalState.getValue(),
                Date.valueOf(birthday.getValue()),
                hashPassword(password.getText()),
                gender.getSelectedToggle().getUserData().toString().charAt(0),
                email.getText(),
                ImageIO.read(profilePictureFile.toURI().toURL()),
                profilePicture.getImage().getUrl().substring(profilePicture.getImage().getUrl().lastIndexOf(".") + 1)
        );

        sessionManager.registerUser(userData, profilePictureFile);

        clear();
        clearErrors();
        Stage stage = (Stage) profilePicture.getScene().getWindow();
        stage.close();
    }

    /**
     * Closes the registration window and goes back to the login window. Doesn't clear on purpose to allow the user to go back to the registration form. If missclicked
     */
    public void backToLogin() {
        clearErrors();
        Stage stage = (Stage) profilePicture.getScene().getWindow();
        stage.close();
    }
}