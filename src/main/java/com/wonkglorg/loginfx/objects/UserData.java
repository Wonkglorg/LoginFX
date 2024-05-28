package com.wonkglorg.loginfx.objects;

import javafx.scene.image.Image;

public class UserData {

    private String username;
    private String password;
    private String email;
    private Image profileImage;

    public UserData(String username, String password, String email, Image profileImage) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Image getProfileImage() {
        return profileImage;
    }

    //todo: add the other fields as well for personal user data
}
