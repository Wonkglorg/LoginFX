package com.wonkglorg.fxutility.manager.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TxtFileWriter {
    private final File file;

    public TxtFileWriter(File file) {
        this.file = file;
    }

    public void write(String... text) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for(String line : text){
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
