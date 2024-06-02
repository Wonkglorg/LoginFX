package com.wonkglorg.loginfx.typehandler;

import com.wonkglorg.util.interfaces.functional.database.DataTypeHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class TypeHandlerBufferedImage implements DataTypeHandler<BufferedImage> {


    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, Object o) throws SQLException {
        if (o instanceof BufferedImage bufferedImage) {
            try {

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                preparedStatement.setBytes(i, byteArrayOutputStream.toByteArray());
            } catch (IOException e) {
                Logger.getGlobal().warning("Failed to convert image to byte array: " + e.getMessage());
            }
        }
    }

    @Override
    public BufferedImage getParameter(ResultSet resultSet, int index) throws SQLException {
        byte[] bytes = resultSet.getBytes(index);
        if (bytes != null && bytes.length > 0) {
            try {
                return ImageIO.read(new ByteArrayInputStream(bytes));
            } catch (Exception e) {
                Logger.getGlobal().warning("Failed to convert byte array to image: " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    public BufferedImage getParameter(ResultSet resultSet, String columnName) throws SQLException {
        byte[] bytes = resultSet.getBytes(columnName);
        if (bytes != null && bytes.length > 0) {
            try {
                return ImageIO.read(new ByteArrayInputStream(bytes));
            } catch (Exception e) {
                Logger.getGlobal().warning("Failed to convert byte array to image: " + e.getMessage());
            }
        }
        return null;
    }
}