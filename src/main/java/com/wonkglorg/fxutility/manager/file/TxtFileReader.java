package com.wonkglorg.fxutility.manager.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class TxtFileReader {

    private final File file;

    public TxtFileReader(File file) {
        this.file = file;
    }

    public List<String> read(int amount) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            List<String> lines = new ArrayList<>();
            int count = 0;
            String line;
            line = reader.readLine();
            while (line != null && amount > count) {
                System.out.println(line);
                count++;
                lines.add(line);
                line = reader.readLine();
            }

            return lines;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
