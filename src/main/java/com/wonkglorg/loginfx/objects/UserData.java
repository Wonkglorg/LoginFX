package com.wonkglorg.loginfx.objects;

import at.favre.lib.crypto.bcrypt.BCrypt;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Logger;

public class UserData {

    private String userID;
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String street;
    private String streetNumber;
    private String country;
    private String zipCode;
    private String federalState;
    private String birthday;
    private String password;
    private String email;
    private Map.Entry<String, Image> profileImage;
    private char gender;

    public UserData(String userID, String username, String firstName, String lastName, String phoneNumber, String street, String streetNumber, String country, String zipCode, String federalState, String birthday, String password, char gender, String email, Image profileImage, String imageName) {
        this.userID = userID;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.street = street;
        this.streetNumber = streetNumber;
        this.country = country;
        this.zipCode = zipCode;
        this.federalState = federalState;
        this.birthday = birthday;
        this.password = password;
        this.email = email;
        this.profileImage = Map.entry(imageName, profileImage);
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getStreet() {
        return street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public String getCity() {
        return country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getFederalState() {
        return federalState;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(10, password.toCharArray());
    }

    public static BufferedImage blobToImage(Blob blob) {
        try {
            return ImageIO.read((blob.getBinaryStream()));
        } catch (IOException | SQLException e) {
            Logger.getLogger("UserData").severe("Error converting byte array to image: " + e.getMessage());
            return null;
        }
    }

    public Image getProfileImage() {
        return profileImage.getValue();
    }

    public String getFileName() {
        return profileImage.getKey();
    }

    public char getGender() {
        return gender;
    }

    public String getUserID() {
        return userID;
    }


}
