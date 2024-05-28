package com.wonkglorg.fxutility.manager.util;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class ChooserUtil {

    private Stage stage;

    /**
     * Create a new ChooserUtil with a stage
     *
     * @param stage
     */
    public ChooserUtil(Stage stage) {
        this.stage = stage;
    }

    /**
     * Open a file chooser dialog
     *
     * @param fileType
     * @return
     */

    public Optional<File> fileChooser(File defaultDirectory, String title, FileChooser.ExtensionFilter... fileType) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(fileType);
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(defaultDirectory);

        File file = fileChooser.showOpenDialog(stage);

        if (file == null || !file.isFile()) {
            return Optional.empty();
        }
        return Optional.of(file);
    }

    /**
     * Open a file chooser dialog
     *
     * @param defaultDirectory
     * @param fileType
     * @return
     */
    public Optional<File> fileChooser(File defaultDirectory, FileChooser.ExtensionFilter... fileType) {
        return fileChooser(defaultDirectory, "Select File", fileType);
    }

    /**
     * Open a file chooser dialog in the default user directory
     *
     * @param fileType
     * @return
     */
    public Optional<File> fileChooser(FileChooser.ExtensionFilter... fileType) {
        return fileChooser(new File(System.getProperty("user.home")), "Select File", fileType);
    }

    /**
     * Open a file chooser dialog in the default user directory with no file type filter set
     *
     * @return
     */
    public Optional<File> fileChooser() {
        return fileChooser(new File(System.getProperty("user.home")), "Select File");
    }


    /**
     * Open a file chooser dialog
     *
     * @param defaultDirectory
     * @param title
     * @return
     */
    public Optional<File> directoryChooser(File defaultDirectory, String title) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(title);
        chooser.setInitialDirectory(defaultDirectory);

        File selectedDirectory = chooser.showDialog(stage);

        if (selectedDirectory == null || !selectedDirectory.isDirectory()) {
            return Optional.empty();
        }
        return Optional.of(selectedDirectory);
    }


    /**
     * Open a file chooser dialog
     *
     * @param defaultDirectory
     * @return
     */
    public Optional<File> directoryChooser(File defaultDirectory) {
        return directoryChooser(defaultDirectory, "Select Directory");
    }

    /**
     * Open a file chooser dialog in the default user directory
     *
     * @return
     */
    public Optional<File> directoryChooser() {
        return directoryChooser(new File(System.getProperty("user.home")));
    }


    /**
     * Sets the stage for the ChooserUtil
     *
     * @param stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
