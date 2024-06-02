package com.wonkglorg.loginfx.objects;

import at.favre.lib.crypto.bcrypt.BCrypt;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.IntBuffer;
import java.sql.Date;

/**
 * This class represents the user data that is stored in the database
 *
 * @param userID       the unique identifier of the user
 * @param username     the username of the user
 * @param firstName    the first name of the user
 * @param lastName     the last name of the user
 * @param phoneNumber  the phone number of the user
 * @param street       the street of the user
 * @param streetNr     the street number of the user
 * @param country      the country of the user
 * @param zipCode      the zip code of the user
 * @param federalState the federal state of the user
 * @param birthday     the birthday of the user
 * @param password     the password of the user
 * @param gender       gender as char (M = Male /F = Female /O = Other)
 * @param email        the email of the user
 * @param profileImage the profile image of the user
 * @param imageName    the name of the image
 */
public record UserData(String userID, String username, String firstName, String lastName, String phoneNumber,
                       String street, String streetNr, String country, String zipCode, String federalState,
                       Date birthday, String password, char gender, String email, BufferedImage profileImage,
                       String imageName) {

    /**
     * Hashes the password with BCrypt
     */
    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(10, password.toCharArray());
    }


    public BufferedImage getProfileImage() {
        return profileImage();
    }

    //fastest solution I could find
    public static javafx.scene.image.Image getImage(BufferedImage img) {
        //converting to a good type, read about types here: https://openjfx.io/javadoc/13/javafx.graphics/javafx/scene/image/PixelBuffer.html
        BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
        newImg.createGraphics().drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);

        //converting the BufferedImage to an IntBuffer
        int[] type_int_agrb = ((DataBufferInt) newImg.getRaster().getDataBuffer()).getData();
        IntBuffer buffer = IntBuffer.wrap(type_int_agrb);

        //converting the IntBuffer to an Image, read more about it here: https://openjfx.io/javadoc/13/javafx.graphics/javafx/scene/image/PixelBuffer.html
        PixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbPreInstance();
        PixelBuffer<IntBuffer> pixelBuffer = new PixelBuffer(newImg.getWidth(), newImg.getHeight(), buffer, pixelFormat);
        return new WritableImage(pixelBuffer);
    }

    /**
     * Changes the password of the user
     *
     * @param userData    the user data
     * @param newPassword the new password (will be hashed by the function)
     * @return the new user data with the new password
     */
    public static UserData changePassword(UserData userData, String newPassword) {
        return new UserData(userData.userID(), userData.username(), userData.firstName(), userData.lastName(),
                userData.phoneNumber(), userData.street(), userData.streetNr(), userData.country(), userData.zipCode(),
                userData.federalState(), userData.birthday(), hashPassword(newPassword), userData.gender, userData.email(), userData.profileImage, userData.imageName());
    }

}
