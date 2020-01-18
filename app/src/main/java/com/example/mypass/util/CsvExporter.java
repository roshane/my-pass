package com.example.mypass.util;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.example.mypass.model.Password;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvExporter {
    private static String LOG_TAG = CsvExporter.class.getSimpleName();

    public void exportToCsv(List<Password> passwords, Uri fileUri, ContentResolver contentResolver) throws IOException {
        try (ParcelFileDescriptor pfd = contentResolver.openFileDescriptor(fileUri, "w");
             FileWriter fileWriter = new FileWriter(pfd.getFileDescriptor());
        ) {
            passwords.stream()
                    .map(this::mapToCsvRow)
                    .forEach(line -> {
                        try {
                            fileWriter.write(line);
                        } catch (Exception ex) {
                            Log.e(LOG_TAG, "Error writing line " + line, ex);
                        }
                    });
            fileWriter.flush();
        } catch (IOException ex) {
            Log.e(LOG_TAG, "Error writing to file", ex);
            throw ex;
        }
    }

    private String mapToCsvRow(Password password) {
        String DELIMITER = ",";
        StringBuilder sb = new StringBuilder(password.getTitle()).append(DELIMITER)
                .append(password.getUsername()).append(DELIMITER)
                .append(password.getPassword()).append(DELIMITER)
                .append(password.getWebsite()).append(DELIMITER)
                .append(password.getNotes()).append(DELIMITER)
                .append(password.isActive()).append(DELIMITER)
                .append(password.getCreateAt()).append("\n");
        return sb.toString();
    }
}
