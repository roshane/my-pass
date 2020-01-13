package com.example.mypass.util;

import com.example.mypass.Fakes;
import com.example.mypass.model.Password;

import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class CsvExporterTest extends Fakes {

    private CsvExporter createClassUnderTest = new CsvExporter();

    @Test
    public void testExportToCsv() throws Exception {
        Password[] passwords = new Password[]{FakePassword1, FakePassword2};
        final File csvFile = createClassUnderTest.exportToCsv(passwords);
        final List<String> fileContent = Files.readAllLines(Paths.get(csvFile.getAbsolutePath()));
        assertThat(fileContent, notNullValue());
        assertThat(fileContent.size(), is(passwords.length));
        assertThat(fileContent.get(0), is(mapToCsvRow(passwords[0])));
        assertThat(fileContent.get(1), is(mapToCsvRow(passwords[1])));
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