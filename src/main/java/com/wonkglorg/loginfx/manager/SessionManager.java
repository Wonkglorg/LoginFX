package com.wonkglorg.loginfx.manager;

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

    public void startSession() {
        // Start the session
    }

    public void endSession() {
        // End the session
    }

    public boolean isSessionActive() {
        // Check if the session is active
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
