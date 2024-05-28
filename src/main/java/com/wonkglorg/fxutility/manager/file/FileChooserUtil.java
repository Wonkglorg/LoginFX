package com.wonkglorg.fxutility.manager.file;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class FileChooserUtil {
    private final Stage stage;
    private File defaultDirectory = new File(System.getProperty("user.home"));

    public FileChooserUtil(Stage stage) {
        this.stage = stage;
    }

    public FileChooserUtil(Stage stage, File defaultDirectory) {
        this.stage = stage;
        this.defaultDirectory = defaultDirectory;
    }

    /**
     * Opens a file chooser dialog and returns the selected file if one was selected.
     */
    private Optional<File> chooseFile(FileType fileType, File defaultDirectory) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter(fileType.name, fileType.fileType));
        fileChooser.setTitle(fileType.title);
        fileChooser.setInitialDirectory(defaultDirectory.getParentFile());

        File file = fileChooser.showOpenDialog(stage);

        if (file == null || !file.isFile()) {
            return Optional.empty();
        }
        return Optional.of(file);
    }

    /**
     * Opens a file chooser dialog and returns the selected file if one was selected.
     *
     * @param fileType
     * @return
     */
    private Optional<File> chooseFile(FileType fileType) {
        return chooseFile(fileType, defaultDirectory);
    }

    /**
     * Opens a directory chooser dialog and returns the selected directory if one was selected.
     *
     * @param defaultDirectory
     * @return
     */
    private Optional<File> chooseDirectory(File defaultDirectory) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Directory");

        chooser.setInitialDirectory(defaultDirectory);

        File selectedDirectory = chooser.showDialog(stage);

        if (selectedDirectory == null || !selectedDirectory.isDirectory()) {
            return Optional.empty();
        }
        return Optional.of(selectedDirectory);
    }

    /**
     * Opens a directory chooser dialog and returns the selected directory if one was selected.
     *
     * @return
     */
    private Optional<File> chooseDirectory() {
        return chooseDirectory(defaultDirectory);
    }

    public enum FileType {
        IMAGE("Image", List.of("*.png", "*.jpg", "*.jpeg", "*webm"), "Select Image"), PNG("Image", List.of("*.png"), "Select Image"), TXT("Text", List.of("*.txt"), "Select Text"), FOLDER("Folder", List.of("*"), "Select Folder"), HTML("HTML", List.of("*.html"), "Select HTML"),

        ;
        private final String name;
        private final List<String> fileType;
        private final String title;

        FileType(String name, List<String> fileTypes, String title) {
            this.name = name;
            this.fileType = fileTypes;
            this.title = title;
        }

        public String getName() {
            return name;
        }

        public List<String> getFileTypes() {
            return fileType;
        }

        public String getTitle() {
            return title;
        }
    }
}