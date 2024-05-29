package com.wonkglorg.loginfx.objects;

import javafx.scene.image.Image;

public class UserData {

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
    private Image profileImage;
    private String gender;

    public UserData(String username, String firstName, String lastName, String phoneNumber, String street, String streetNumber, String city, String zipCode, String federalState, String birthday, String password, String gender, String email, Image profileImage) {
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
        this.profileImage = profileImage;
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

    public Image getProfileImage() {
        return profileImage;
    }

    public String getGender() {
        return gender;
    }
}
