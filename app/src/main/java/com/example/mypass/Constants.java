package com.example.mypass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Constants {
    private static final String FILE_NAME_PREFIX = "mypass_backup";
    private static final String FILE_NAME_SUFFIX = ".csv.txt";
    private static final DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("YYYYMMDD");

    public static String getFileName() {
        String timestampString = dtFmt.format(LocalDateTime.now());
        return String.format("%s%s%s", FILE_NAME_PREFIX, timestampString, FILE_NAME_SUFFIX);
    }
}
