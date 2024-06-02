package com.wonkglorg.loginfx.manager;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.wonkglorg.loginfx.objects.Action;
import com.wonkglorg.loginfx.objects.UserData;
import com.wonkglorg.loginfx.typehandler.TypeHandlerBufferedImage;
import com.wonkglorg.util.database.ConnectionBuilder;
import com.wonkglorg.util.database.Database;
import com.wonkglorg.util.database.GenericServerDatabase;
import com.wonkglorg.util.database.MsSqlServerDatabase;
import com.wonkglorg.util.database.response.DatabaseUpdateResponse;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.List;

public class SessionManager {

    private static SessionManager instance;
    private final GenericServerDatabase database;

    private SessionManager() {
        com.wonkglorg.util.database.ConnectionBuilder builder = new ConnectionBuilder("jdbc:sqlserver://jmd-webdb-new.database.windows.net:1433;database=WebDB;user=dominik@jmd-webdb-new;password=x7!E8\"phKW;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;");
        database = new MsSqlServerDatabase(builder, 3);
        Database.addDataMapper(BufferedImage.class, new TypeHandlerBufferedImage());
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

    public boolean checkPassword(String password, String hash) {
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified;
    }

    /**
     * Register a user in the database
     *
     * @param userData the user data to register
     * @return if the user was registered
     */
    public boolean registerUser(UserData userData, File image) {
        if (!createAccountData(userData)) return false;

        try (FileInputStream fileInputStream = new FileInputStream(image);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }


            String extension = image.getName().split("\\.")[image.getName().split("\\.").length - 1];

            if (!createUserProfilePicture(byteArrayOutputStream, userData.userID(), "profile_picture", "profile_picture", extension)) {
                return false;
            }

            if (!createUserData(userData)) return false;

            return true;
        } catch (IOException e) {
            logAction(new Action(userData.userID(), "register", "error", "Error reading Profile Picture: " + e.getMessage(), null));
            return false;
        }
    }

    public String getAccountCreationDate(String username) {
        String sql = "SELECT account_creation_date FROM dbo.AccountData WHERE username = ?";
        return database.executeSingleObjQuery(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            var resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return resultSet.getString(1);
        }).getData();
    }

    /**
     * Register a user in the database
     *
     * @param imageArrayOutputStream the input imageArrayOutputStream of the image
     * @param userID                 the user id
     * @param contentType            the content type of the image
     * @param name                   the name of the image
     * @return if the user was registered
     */
    private boolean createUserProfilePicture(ByteArrayOutputStream imageArrayOutputStream, String userID, String contentType, String name, String extension) {

        System.out.println("Creating user profile picture" + userID + " " + contentType + " " + name);
        String containsImageSql = "SELECT * FROM dbo.images WHERE id = ?";
        String insertImageSql = "INSERT INTO dbo.images (id, name, contentType,  extension, image) VALUES (?,?,?,?,?)";
        String updateImageSql = "UPDATE dbo.images SET name = ?, contentType = ?, image = ?, extension = ?  WHERE id = ?";

        //checks if the user already has a profile picture
        boolean containsImage = database.executeSingleObjQuery(connection -> {
            PreparedStatement statement = connection.prepareStatement(containsImageSql);
            statement.setString(1, userID);
            return statement.executeQuery().next();
        }).getData();


        //if the user already has a profile picture, update it
        if (containsImage) {
            var updateResponse = database.executeUpdate(connection -> {
                try (var statement = connection.prepareStatement(updateImageSql)) {
                    statement.setString(1, name);
                    statement.setString(2, contentType);
                    statement.setBytes(3, imageArrayOutputStream.toByteArray());
                    statement.setString(4, extension);
                    statement.setString(5, userID);
                    return statement.executeUpdate();
                }
            });

            if (updateResponse.hasError()) {
                logAction(new Action(userID, "register", "error", "Error updating " + contentType + " Picture: " + updateResponse.getException().getMessage(), null));
                return false;
            } else {
                logAction(new Action(userID, "register", "success", "User updated " + contentType + " Picture successfully", null));
            }
            return true;
        }

        //if the user does not have a profile picture, insert it
        var insertResponse = database.executeUpdate(connection -> {
            try (var statement = connection.prepareStatement(insertImageSql)) {
                statement.setString(1, userID);
                statement.setString(2, name);
                statement.setString(3, contentType);
                statement.setString(4, extension);
                statement.setBytes(5, imageArrayOutputStream.toByteArray());
                return statement.executeUpdate();
            }
        });

        if (insertResponse.hasError()) {
            logAction(new Action(userID, "register", "error", "Error adding " + contentType + " Picture: " + insertResponse.getException().getMessage(), null));
            return false;
        } else {
            logAction(new Action(userID, "register", "success", "User added " + contentType + " Picture successfully", null));
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
                statement.setString(1, userData.userID());
                statement.setString(2, userData.username());
                statement.setString(3, hashPassword(userData.password()));
                statement.setString(4, userData.email());
                return statement.executeUpdate();
            }
        });


        if (response.hasError()) {
            logAction(new Action(userData.userID(), "register", "error", "Error registering Account: " + response.getException().getMessage(), null));
            return false;
        } else {
            logAction(new Action(userData.userID(), "register", "success", "User registered Account successfully", null));
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

        var response = database.executeUpdate(connection -> {
            try (var statement = connection.prepareStatement(insertUserData)) {
                statement.setString(1, userData.userID());
                statement.setString(2, userData.firstName());
                statement.setString(3, userData.lastName());
                statement.setString(4, userData.phoneNumber());
                statement.setString(5, userData.street());
                statement.setString(6, userData.streetNr());
                statement.setString(7, userData.country());
                statement.setString(8, userData.zipCode());
                statement.setString(9, userData.federalState());
                statement.setDate(10, userData.birthday());
                statement.setString(11, String.valueOf(userData.gender()));
                return statement.executeUpdate();
            }
        });

        if (response.hasError()) {
            logAction(new Action(userData.userID(), "register", "error", "Error registering UserData: " + response.getException().getMessage(), null));
            return false;
        } else {
            logAction(new Action(userData.userID(), "register", "success", "User registered UserData successfully", null));
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

    /**
     * Check if a user is valid
     *
     * @param username the username
     * @param password the password
     * @return if the user is valid
     */
    public boolean isValidUser(String username, String password) {
        String sql = "SELECT password,id FROM dbo.AccountData WHERE username = ?";

        var dbResponse = database.executeSingleObjQuery(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, username);
            var resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                logAction(new Action("0", "Login", "successful login", username, null));
                return false;
            }

            boolean isValid = checkPassword(password, resultSet.getString(1));

            if (isValid) {
                logAction(new Action(resultSet.getString(2), "Login", "successful login", username, null));
            } else {
                logAction(new Action(resultSet.getString(2), "Login", "failed login invalid password", username, null));
            }
            return isValid;
        });

        if (dbResponse.hasError()) {
            System.out.println("Error checking if user is valid: " + dbResponse.getException().getMessage());
            return false;
        }
        return dbResponse.getData();
    }


    /**
     * Get the user data for a user
     *
     * @param username the username
     * @return the user data
     */
    public UserData getUserData(String username) {

        String sql = """
                SELECT account.id,             account.username,       userdata.first_name,
                       userdata.last_name,     userdata.phonenumber,   userdata.street,
                       userdata.street_nr,     userdata.country,       userdata.zip_code,
                       userdata.federal_state, userdata.birthday,
                       account.password,       userdata.gender,        account.email,
                       images.image,           images.name
                  FROM dbo.AccountData account
                  JOIN dbo.UserData userdata
                    ON account.Id = userdata.Id
                  JOIN dbo.images images
                    ON account.Id = images.Id
                  WHERE username = ?""";

        var response = database.executeSingleObjQuery(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            var resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return database.recordIndexAdapter(UserData.class, 0).apply(resultSet);
        });

        if (response.hasError()) {
            System.out.println("Error getting user data: " + response.getException().getMessage());
        }
        return response.getData();
    }

    /**
     * Update the account data for a user
     *
     * @param userData the user data to update
     * @param image    the image to update
     * @return if the user data was updated
     */
    //todo:jmd current update does not work!
    public boolean updateUserData(UserData userData, File image) {
        String updateAccount = """
                UPDATE dbo.AccountData
                SET
                    password = ?,
                    email = ?
                FROM dbo.AccountData account
                JOIN dbo.UserData userdata ON account.Id = userdata.Id
                JOIN dbo.images images ON account.Id = images.Id
                WHERE account.username = ?;
                """;

        var response = database.executeUpdate(connection -> {
            PreparedStatement statement = connection.prepareStatement(updateAccount);
            statement.setString(1, hashPassword(userData.password()));
            statement.setString(2, userData.email());
            statement.setString(3, userData.username());
            return statement.executeUpdate();
        });

        if (response.hasError()) {
            logAction(new Action(userData.userID(), "update", "error", "Error updating Account Data: " + response.getException().getMessage(), null));
            return false;
        } else {
            logAction(new Action(userData.userID(), "update", "success", "User updated Account Data successfully", null));
        }


        String sql = """    
                 UPDATE
                    dbo.AccountData account,
                    dbo.AccountData account,
                    dbo.images images
                SET
                    account.password = ?,
                    account.email = ?,
                    userdata.first_name = ?,
                    userdata.last_name = ?,
                    userdata.federal_state = ?,
                    userdata.zip_code
                    userdata.street = ?,
                    userdata.street_nr = ?,
                    userdata.country = ?,
                    userdata.birthday = ?,
                    userdata.gender = ?,
                    userdata.phonenumber = ?,
                    images.image = ?,
                FROM
                    dbo.AccountData account
                JOIN
                    dbo.UserData userdata ON account.Id = userdata.Id
                JOIN
                    dbo.images images ON account.Id = images.Id
                WHERE
                    account.username = ?;""";

        var response1 = database.executeUpdate(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, hashPassword(userData.password()));
            statement.setString(2, userData.email());
            statement.setString(3, userData.firstName());
            statement.setString(4, userData.lastName());
            statement.setString(5, userData.federalState());
            statement.setString(6, userData.zipCode());
            statement.setString(7, userData.street());
            statement.setString(8, userData.street());
            statement.setString(9, userData.country());
            statement.setString(10, userData.zipCode());
            statement.setDate(11, userData.birthday());
            statement.setString(12, String.valueOf(userData.gender()));
            statement.setString(13, userData.phoneNumber());
            statement.setBinaryStream(14, new FileInputStream(image));
            statement.setString(15, userData.username());
            return statement.executeUpdate();

        });
        if (response1.hasError()) {
            logAction(new Action(userData.userID(), "update", "error", "Error updating User Data: " + response.getException().getMessage(), null));
            return false;
        } else {
            logAction(new Action(userData.userID(), "update", "success", "User updated User Data successfully", null));
        }

        return true;
    }


    /**
     * Get all actions for a user
     *
     * @param userID the user id
     * @return the actions
     */
    public List<Action> getActions(String userID) {
        String sql = "SELECT userID,actionType,actionTime,actionMessage,actionStatus FROM dbo.user_action WHERE userID = ?";
        return database.executeObjQuery(connection -> {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, userID);
                    return statement.executeQuery();
                },

                resultSet -> database.mapRecords(resultSet, database.recordAdapter(Action.class))).getData();
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
            statement.setString(1, action.userID());
            statement.setString(2, action.actionType());
            statement.setString(3, action.actionStatus());
            statement.setString(4, action.actionMessage());
            return statement.executeUpdate();
        });

        if (response.hasError()) {
            throw new RuntimeException("Error logging action: " + response.getException().getMessage());
        }
    }


    public void deleteUser(String id) {
        String deleteAccount = """
                DELETE FROM dbo.AccountData
                WHERE id = ?;
                """;

        String deleteImages = """
                DELETE FROM dbo.images
                WHERE id = ?;
                """;

        String deleteUserData = """
                DELETE FROM dbo.UserData
                WHERE id = ?;
                """;

        var response = database.executeUpdate(connection -> {
            PreparedStatement statement = connection.prepareStatement(deleteAccount);
            statement.setString(1, id);
            return statement.executeUpdate();
        });

        if (response.hasError()) {
            logAction(new Action(id, "delete", "error", "Error deleting Account Data: " + response.getException().getMessage(), null));
        } else {
            logAction(new Action(id, "delete", "success", "User deleted Account Data successfully", null));
        }


        var response1 = database.executeUpdate(connection -> {
            PreparedStatement statement = connection.prepareStatement(deleteImages);
            statement.setString(1, id);
            return statement.executeUpdate();
        });


        if (response1.hasError()) {
            logAction(new Action(id, "delete", "error", "Error deleting Images: " + response1.getException().getMessage(), null));
        } else {
            logAction(new Action(id, "delete", "success", "User deleted Images successfully", null));
        }

        var response2 = database.executeUpdate(connection -> {
            PreparedStatement statement = connection.prepareStatement(deleteUserData);
            statement.setString(1, id);
            return statement.executeUpdate();
        });

        if (response2.hasError()) {
            logAction(new Action(id, "delete", "error", "Error deleting User Data: " + response2.getException().getMessage(), null));
        } else {
            logAction(new Action(id, "delete", "success", "User deleted User Data successfully", null));
        }


    }

}
