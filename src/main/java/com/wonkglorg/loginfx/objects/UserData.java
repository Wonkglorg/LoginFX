package com.wonkglorg.loginfx.objects;

import at.favre.lib.crypto.bcrypt.BCrypt;

import javax.imageio.ImageIO;
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
    private String city;
    private String zipCode;
    private String federalState;
    private String birthday;
    private String password;
    private String email;
    private Map.Entry<String, BufferedImage> profileImage;
    private char gender;

    public UserData(String userID, String username, String firstName, String lastName, String phoneNumber, String street, String streetNumber, String city, String zipCode, String federalState, String birthday, String password, char gender, String email, BufferedImage profileImage, String fileExtension) {
        this.userID = userID;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.street = street;
        this.streetNumber = streetNumber;
        this.city = city;
        this.zipCode = zipCode;
        this.federalState = federalState;
        this.birthday = birthday;
        this.password = password;
        this.email = email;
        this.profileImage = Map.entry(fileExtension, profileImage);
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
        return city;
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

    public BufferedImage getProfileImage() {
        return profileImage.getValue();
    }

    public String getFileExtension() {
        return profileImage.getKey();
    }

    public char getGender() {
        return gender;
    }

    public String getUserID() {
        return userID;
    }


}
