package com.example.mypass.util;

import android.util.Log;

import com.example.mypass.model.Password;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;

public class CsvImporter {

    private static final String LOG_TAG = CsvImporter.class.getSimpleName();
    private String DELIMITER = ",";
    private int TOKEN_COUNT = 7;

    public Password[] importFromCsv(Path filePath) throws IOException {
        try (FileReader fileReader = new FileReader(new File(filePath.toUri()));
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            return bufferedReader.lines().map(line -> {
                try {
                    return mapToPassword(line);
                } catch (Exception ex) {
                    Log.e(LOG_TAG, "Error importing line", ex);
                }
                return null;
            }).filter(Objects::nonNull).toArray(Password[]::new);
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Error reading file " + filePath, ex);
            throw ex;
        }
    }

    private Password mapToPassword(String line) throws Exception {
        final String[] tokens = line.split(DELIMITER);
        if (tokens.length != TOKEN_COUNT) {
            throw new Exception("Parser Exception at line " + line);
        }
        String title = tokens[0];
        String username = tokens[1];
        String password = tokens[2];
        String website = tokens[3];
        String notes = tokens[4];
        boolean isActive = Boolean.valueOf(tokens[5]);
        LocalDateTime createdAt = LocalDateTime.parse(tokens[6]);
        return new Password(title, username, password, website, notes, createdAt, isActive);
    }
}
