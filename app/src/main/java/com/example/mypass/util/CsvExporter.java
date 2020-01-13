package com.example.mypass.util;

import android.util.Log;

import com.example.mypass.model.Password;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class CsvExporter {
    private static String LOG_TAG = CsvExporter.class.getSimpleName();
    private static String DELIMITER = ",";
    private static final String FILE_NAME_PREFIX = "mypass_backup";
    private static final String FILE_NAME_SUFFIX = ".csv";
    private static final DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("YYYY_MM_DD");

    public File exportToCsv(Password[] passwords) throws IOException {
        try {
            File tempFile = File.createTempFile(getFileName(), FILE_NAME_SUFFIX);
            FileWriter fileWriter = new FileWriter(tempFile);
            Arrays.stream(passwords)
                    .map(this::mapToCsvRow)
                    .forEach(line -> {
                        try {
                            fileWriter.write(line);
                        } catch (Exception ex) {
                            Log.e(LOG_TAG, "Error writing line " + line, ex);
                        }
                    });
            fileWriter.flush();
            fileWriter.close();
            return tempFile;
        } catch (IOException ex) {
            Log.e(LOG_TAG, "Error exporting to file", ex);
            throw ex;
        }
    }

    private String getFileName() {
        String timestampString = dtFmt.format(LocalDateTime.now());
        return String.format("%s_%s", FILE_NAME_PREFIX, timestampString);
    }

    private String mapToCsvRow(Password password) {
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
