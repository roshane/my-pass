package com.example.mypass.util;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import androidx.test.core.app.ApplicationProvider;

import com.example.mypass.Fakes;
import com.example.mypass.model.Password;

import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class CsvExporterTest extends Fakes {

    private CsvExporter createClassUnderTest = new CsvExporter();

    @Test
    public void testExportToCsv() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        ContentResolver contentResolver = context.getContentResolver();

        List<Password> passwords = Arrays.asList(FakePassword1, FakePassword2);
        File filePath = context.getCacheDir();
        final File testCsvFile = new File(filePath.getPath(), "test.csv");
        createClassUnderTest.exportToCsv(passwords, Uri.fromFile(testCsvFile), contentResolver);
        if (!Files.exists(Paths.get(testCsvFile.getPath()))) {
            throw new Exception("File doesn't exists");
        }
        final List<String> fileContent = Files.readAllLines(Paths.get(testCsvFile.getPath()));
        assertThat(fileContent, notNullValue());
        assertThat(fileContent.size(), is(passwords.size()));
        assertThat(fileContent.get(0), is(mapToCsvRow(passwords.get(0))));
        assertThat(fileContent.get(1), is(mapToCsvRow(passwords.get(1))));
        System.out.println(fileContent);
    }

    private String mapToCsvRow(Password password) {
        String COMMA = ",";
        StringBuilder sb = new StringBuilder(password.getTitle()).append(COMMA)
                .append(password.getUsername()).append(COMMA)
                .append(password.getPassword()).append(COMMA)
                .append(password.getWebsite()).append(COMMA)
                .append(password.getNotes()).append(COMMA)
                .append(password.isActive()).append(COMMA)
                .append(password.getCreateAt());
        return sb.toString();
    }
}