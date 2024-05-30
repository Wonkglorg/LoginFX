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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

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

        System.out.println(password.getText());
    }

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

    private void testValues() {
        username.setText("Wonkglorg");
        firstName.setText("Dominik");
        lastName.setText("Meierhofer");
        email.setText("Wonkglorg.18@gmail.com");
        phoneNumber.setText("0676-480-00-27");
        birthday.getEditor().setText("2000-12-18");
        street.setText("Römergasse");
        streetNumber.setText("36");
        city.setText("Salzburg");
        zipCode.setText("5020");
        federalState.getSelectionModel().select("Salzburg");
        password.setText("Password.1");
        passwordRepeat.setText("Password.1");
    }

    public void chooseImage() {
        Application.getChooser().fileChooser(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"))
                .ifPresent(file -> {
                    profilePictureFile = file;
                    profilePicture.setImage(new Image(file.toURI().toString()));
                });
    }

    private boolean validateUserInput() {
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
            System.out.println(password.getText());
            showError(password, "Password must be at least 8 long, contain at least one uppercase letter, one lowercase letter, one number, and one special character");
            return false;
        }

        if (!EMAIL_PATTERN.matcher(email.getText()).matches() || sessionManager.emailExists(email.getText())) {
            showError(email, "Invalid email or already in use!");
            return false;
        }

        return true;
    }

    private void showError(Control control, String message) {
        Tooltip tooltip = new Tooltip(message);
        tooltip.setShowDelay(javafx.util.Duration.ZERO);
        control.setTooltip(tooltip);
        control.setBorder(ERROR_BORDER);
    }

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

    public void clearErrors() {
        fields.keySet().forEach(control -> {
            control.setTooltip(null);
            control.setBorder(null);
        });
    }

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
                birthday.getValue().toString(),
                password.getText(),
                'M', // Assuming gender is always 'M' for simplicity, this should be dynamically set based on user input.
                email.getText(),
                ImageIO.read(profilePictureFile.toURI().toURL()),
                profilePicture.getImage().getUrl().substring(profilePicture.getImage().getUrl().lastIndexOf(".") + 1)
        );

        sessionManager.registerUser(userData, profilePictureFile);

        clear();
        Stage stage = (Stage) profilePicture.getScene().getWindow();
        stage.close();
    }

    public void backToLogin() {
        Stage stage = (Stage) profilePicture.getScene().getWindow();
        stage.close();
    }
}