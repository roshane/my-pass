package com.example.mypass;

import java.time.format.DateTimeFormatter;

public interface DateFormatter {
    DateTimeFormatter DF = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    DateTimeFormatter DFHumanReadable = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm");
}
