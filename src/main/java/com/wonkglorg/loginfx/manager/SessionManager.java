package com.wonkglorg.loginfx.manager;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.wonkglorg.loginfx.objects.Action;
import com.wonkglorg.loginfx.objects.UserData;
import com.wonkglorg.util.database.ConnectionBuilder;
import com.wonkglorg.util.database.GenericServerDatabase;
import com.wonkglorg.util.database.MsSqlServerDatabase;
import com.wonkglorg.util.database.response.DatabaseResponse;
import com.wonkglorg.util.database.response.DatabaseUpdateResponse;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

import static com.wonkglorg.loginfx.objects.UserData.blobToImage;
import static com.wonkglorg.util.database.Database.convertToByteArray;

public class SessionManager {

    private static SessionManager instance;
    private final GenericServerDatabase database;

    private UserData userData = null;

    private SessionManager() {
        com.wonkglorg.util.database.ConnectionBuilder builder = new ConnectionBuilder("jdbc:sqlserver://jmd-webdb-new.database.windows.net:1433;database=WebDB;user=dominik@jmd-webdb-new;password=x7!E8\"phKW;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;");
        database = new MsSqlServerDatabase(builder, 3);
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Hash the password using BCrypt with a cost of 10 (matching the default php password_hash cost)
     *
     * @param password
     * @return the hashed password
     */
    public String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(10, password.toCharArray());
    }

    private byte[] getBlobData(BufferedImage image, String imageType) {
        try {
            return convertToByteArray(image, imageType);
        } catch (IOException e) {
            Logger.getLogger("SessionManager").severe("Error converting image to byte array: " + e.getMessage());
            return null;
        }
    }

    /**
     * Register a user in the database
     *
     * @param userData the user data to register
     * @return if the user was registered
     */
    public boolean registerUser(UserData userData) {
        String insertAccountData = "INSERT INTO dbo.AccountData (id,username,password,account_creation_date,profile_picture,email,profile_picture_exension) VALUES (?,?,?,GetDate(),?,?,?)";

        BufferedImage image = userData.getProfileImage();
        String imageType = userData.getFileExtension();
        Blob blob = database.createBlob(getBlobData(image, imageType));


        DatabaseResponse response = database.executeUpdate(connection -> {
            try (var statement = connection.prepareStatement(insertAccountData)) {
                statement.setString(1, userData.getUserID());
                statement.setString(2, userData.getUsername());
                statement.setString(3, hashPassword(userData.getPassword()));
                statement.setBlob(4, blob);
                statement.setString(5, userData.getEmail());
                statement.setString(6, imageType);
                return statement.executeUpdate();
            }
        });

        if (response.hasError()) {
            logAction(new Action(userData.getUserID(), "register", "error", "Error registering Account: " + response.getException().getMessage()));
            return false;
        } else {
            logAction(new Action(userData.getUserID(), "register", "success", "User registered Account successfully"));
        }


        String insertUserData = "INSERT INTO dbo.UserData (id,first_name,last_name,phone_number,street,street_number,city,zip_code,federal_state,birthday,gender) VALUES (?,?,?,?,?,?,?,?,?,?)";

        DatabaseResponse response1 = database.executeUpdate(connection -> {
            try (var statement = connection.prepareStatement(insertUserData)) {
                statement.setString(1, userData.getUserID());
                statement.setString(2, userData.getFirstName());
                statement.setString(3, userData.getLastName());
                statement.setString(4, userData.getPhoneNumber());
                statement.setString(5, userData.getStreet());
                statement.setString(6, userData.getStreetNumber());
                statement.setString(7, userData.getCity());
                statement.setString(8, userData.getZipCode());
                statement.setString(9, userData.getFederalState());
                statement.setString(10, userData.getBirthday());
                return statement.executeUpdate();
            }
        });

        if (response1.hasError()) {
            logAction(new Action(userData.getUserID(), "register", "error", "Error registering UserData: " + response1.getException().getMessage()));
            return false;
        } else {
            logAction(new Action(userData.getUserID(), "register", "success", "User registered UserData successfully"));
        }
        return true;
    }


    /**
     * Check if an email exists in the database
     *
     * @param email the email to check
     * @return if the email exists
     */
    public boolean emailExists(String email) {
        return valueExists(email, "SELECT * FROM AccountData WHERE email = ?");
    }

    /**
     * Check if a username exists in the database
     *
     * @param username the username to check
     * @return if the username exists
     */
    public boolean usernameExists(String username) {
        return valueExists(username, "SELECT * FROM AccountData WHERE username = ?");
    }


    /**
     * Check if a value exists in the database format:
     * <p>
     * SELECT * FROM table WHERE column = ?
     *
     * @param value             the value to check
     * @param preparedStatement the prepared statement to use
     * @return if the value exists
     */
    private boolean valueExists(String value, String preparedStatement) {
        return database.executeSingleObjQuery(connection -> {
            PreparedStatement statement = connection.prepareStatement(preparedStatement);
            statement.setString(1, value);
            return statement.executeQuery().next();
        }).getData();
    }

    public boolean isValidUser(String username, String password) {
        String sql = "SELECT * FROM dbo.AccountData WHERE username = ? AND password = ?";
        return database.executeSingleObjQuery(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, hashPassword(password));
            return !statement.executeQuery().next();
        }).getData();
    }


    public UserData getUserData(String username) {

        String sql = """
                SELECT *
                  FROM dbo.AccountData account
                  JOIN dbo.UserData user
                  ON account.id = user.id
                  WHERE username = ?""";
        return database.executeSingleObjQuery(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String id = resultSet.getString("account.id");
                String password = resultSet.getString("account.password");
                String account_creation_date = resultSet.getString("account.account_creation_date");
                String username1 = resultSet.getString("account.username");
                String email = resultSet.getString("account.email");
                String firstName = resultSet.getString("user.first_name");
                String lastName = resultSet.getString("user.last_name");
                String federalState = resultSet.getString("user.federal_state");
                String street = resultSet.getString("user.street");
                String street_nr = resultSet.getString("user.street_nr");
                String birthday = resultSet.getString("user.birthday");
                char gender = resultSet.getString("user.gender").charAt(0);
                String phoneNumber = resultSet.getString("user.phonenumber");
                BufferedImage profilePicture = blobToImage(resultSet.getBlob("account.profile_picture"));
                String profilePictureType = resultSet.getString("account.profile_picture_extension");


                return new UserData(id, username, firstName, lastName, phoneNumber, street, street_nr, "", email, federalState, birthday, password, gender, email, profilePicture, profilePictureType);
            }
            return null;
        }).getData();
    }

    /**
     * Log events to the database
     *
     * @param action the action to log
     */
    public void logAction(Action action) {


        String sql = "INSERT INTO WebDB.dbo.user_action (userID, actionType, actionStatus, actionMessage, actionTime) VALUES (?, ?, ?, ?, GetDate())";
        DatabaseUpdateResponse response = database.executeUpdate(connection -> {
            var statement = connection.prepareStatement(sql);
            statement.setString(1, action.getUserID());
            statement.setString(2, action.getActionType());
            statement.setString(3, action.getActionStatus());
            statement.setString(4, action.getActionMessage());
            return statement.executeUpdate();
        });

        if (response.hasError()) {
            throw new RuntimeException("Error logging action: " + response.getException().getMessage());
        }
    }

}
