package com.wonkglorg.loginfx.manager;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.wonkglorg.loginfx.objects.Action;
import com.wonkglorg.loginfx.objects.UserData;
import com.wonkglorg.util.database.ConnectionBuilder;
import com.wonkglorg.util.database.GenericServerDatabase;
import com.wonkglorg.util.database.MsSqlServerDatabase;
import com.wonkglorg.util.database.response.DatabaseResponse;
import com.wonkglorg.util.database.response.DatabaseUpdateResponse;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

import static com.wonkglorg.loginfx.objects.UserData.blobToImage;
import static com.wonkglorg.util.database.Database.convertToByteArray;

public class SessionManager {

    private static SessionManager instance;
    private final GenericServerDatabase database;

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
    public boolean registerUser(UserData userData, File image) {
        if (!createAccountData(userData)) return false;

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(image);
        } catch (IOException e) {
            logAction(new Action(userData.getUserID(), "register", "error", "Error reading Profile Picture: " + e.getMessage()));
            return false;
        }


        if (!createUserProfilePicture(fileInputStream, userData.getUserID(), "profile_picture", "profile_picture")) {
            return false;
        }

        if (!createUserData(userData)) return false;


        return true;
    }

    /**
     * Register a user in the database
     *
     * @param stream      the input stream of the image
     * @param userID      the user id
     * @param contentType the content type of the image
     * @param name        the name of the image
     * @return if the user was registered
     */
    private boolean createUserProfilePicture(InputStream stream, String userID, String contentType, String name) {

        String insertImage = "INSERT INTO dbo.images (id, name, contentType, image) VALUES (?,?,?,?)";

        if (database.executeSingleObjQuery(connection -> {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM dbo.images WHERE id = ?");
            statement.setString(1, userID);
            return statement.executeQuery().next();
        }).getData()) {

            var response = database.executeUpdate(connection -> {
                try (var statement = connection.prepareStatement("UPDATE dbo.images SET name = ?, contentType = ?, image = ? WHERE id = ?")) {
                    statement.setString(1, name);
                    statement.setString(2, contentType);
                    statement.setBinaryStream(3, stream);
                    statement.setString(4, userID);
                    return statement.executeUpdate();
                }
            });

            if (response.hasError()) {
                logAction(new Action(userID, "register", "error", "Error updating " + contentType + " Picture: " + response.getException().getMessage()));
                return false;
            } else {
                logAction(new Action(userID, "register", "success", "User updated " + contentType + " Picture successfully"));
            }

            return true;
        }


        var response = database.executeUpdate(connection -> {
            try (var statement = connection.prepareStatement(insertImage)) {
                statement.setString(1, userID);
                statement.setString(2, name);
                statement.setString(3, contentType);
                statement.setBinaryStream(4, stream);
                return statement.executeUpdate();
            }
        });

        if (response.hasError()) {
            logAction(new Action(userID, "register", "error", "Error adding Profile Picture: " + response.getException().getMessage()));
            return false;
        } else {
            logAction(new Action(userID, "register", "success", "User added Profile Picture successfully"));
        }
        return true;
    }

    /**
     * Create an account in the database
     *
     * @param userData the user data to create the account for
     * @return if the account was created
     */
    private boolean createAccountData(UserData userData) {
        String insertAccountData = "INSERT INTO dbo.AccountData (id,username,password,account_creation_date,email) VALUES (?,?,?,GetDate(),?)";
        var response = database.executeUpdate(connection -> {
            try (var statement = connection.prepareStatement(insertAccountData)) {
                statement.setString(1, userData.getUserID());
                statement.setString(2, userData.getUsername());
                statement.setString(3, hashPassword(userData.getPassword()));
                statement.setString(4, userData.getEmail());
                return statement.executeUpdate();
            }
        });

        if (response.hasError()) {
            logAction(new Action(userData.getUserID(), "register", "error", "Error registering Account: " + response.getException().getMessage()));
            return false;
        } else {
            logAction(new Action(userData.getUserID(), "register", "success", "User registered Account successfully"));
        }

        return true;
    }

    /**
     * Create user data in the database
     *
     * @param userData the user data to create
     * @return if the user data was created
     */
    private boolean createUserData(UserData userData) {

        String insertUserData = "INSERT INTO dbo.UserData (id,first_name,last_name,phonenumber,street,street_nr,country,zip_code,federal_state,birthday,gender) VALUES (?,?,?,?,?,?,?,?,?,?,?)";

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
                statement.setString(11, String.valueOf(userData.getGender()));
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
                SELECT account.id         as id, account.password as password, account.email as email, userdata.first_name as firstName, 
                       userdata.last_name as lastName, userdata.federal_state as federalState, 
                       userdata.street as street, userdata.street_nr as streetNr, userdata.country as country, 
                       userdata.birthday as birthday, userdata.gender as gender, userdata.phonenumber as phonenumber, images.image as image, images.name as imageName
                  FROM dbo.AccountData account
                  JOIN dbo.UserData userdata
                  ON account.Id = userdata.Id
                  JOIN dbo.images images
                    ON account.Id = images.Id
                  WHERE username = ?""";
        return database.executeSingleObjQuery(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String id = resultSet.getString("id");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String federalState = resultSet.getString("federalState");
                String street = resultSet.getString("street");
                String street_nr = resultSet.getString("streetNr");
                String country = resultSet.getString("country");
                String birthday = resultSet.getString("birthday");
                char gender = resultSet.getString("gender").charAt(0);
                String phoneNumber = resultSet.getString("phonenumber");
                BufferedImage profilePicture = resultSet.getBytes("image") != null ? ImageIO.read(new ByteArrayInputStream(resultSet.getBytes("image"))) : null;
                String profilePictureName = resultSet.getString("imageName");


                return new UserData(id, username, firstName, lastName, phoneNumber, street, street_nr, country, email, federalState, birthday, password, gender, email, profilePicture, profilePictureName);
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
