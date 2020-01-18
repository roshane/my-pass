package com.example.mypass.util;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.example.mypass.model.Password;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CsvImporter {

    private static final String LOG_TAG = CsvImporter.class.getSimpleName();

    public List<Password> importFromCsv(Uri fileUri, ContentResolver contentResolver) throws IOException {
        try (ParcelFileDescriptor pfd = contentResolver.openFileDescriptor(fileUri, "r");
             FileReader fileReader = new FileReader(pfd.getFileDescriptor());
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            return bufferedReader.lines().map(line -> {
                try {
                    return mapToPassword(line);
                } catch (Exception ex) {
                    Log.e(LOG_TAG, "Error importing line", ex);
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Error reading file " + fileUri.toString(), ex);
            throw ex;
        }
    }

    private Password mapToPassword(String line) throws Exception {
        String DELIMITER = ",";
        int TOKEN_COUNT = 7;
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
