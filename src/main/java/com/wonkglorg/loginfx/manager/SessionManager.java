package com.wonkglorg.loginfx.manager;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.wonkglorg.loginfx.objects.UserData;
import com.wonkglorg.util.database.Database;
import com.wonkglorg.util.database.MsSqlServerDatabase;
import com.wonkglorg.util.database.values.Db;

public class SessionManager {

    private static SessionManager instance;
    private final Database database;

    private UserData userData = null;

    private SessionManager() {
        database = new MsSqlServerDatabase(Db.url("jmd-webdb-new.database.windows.net:1433"), Db.user("dominik@jmd-webdb-new"), Db.password("x7!E8\"phKW"), 3);
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

    public boolean registerUser(UserData userData) {

        String hashedPassword = hashPassword(userData.getPassword());

        return false;
    }

    public boolean emailExists(String email) {
        // Check if the email exists
        return false;
    }

    public boolean usernameExists(String username) {
        // Check if the username exists
        return false;
    }


    public boolean isValidUser(String username, String password) {
        // Check if the user is valid
        return false;
    }

    public UserData getUserData(String username) {
        // Get the user data
        return null;
    }
}
