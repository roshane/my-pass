package com.example.mypass.util;

import android.content.Context;
import android.net.Uri;

import androidx.test.core.app.ApplicationProvider;

import com.example.mypass.Fakes;
import com.example.mypass.model.Password;

import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CsvImporterTest extends Fakes {

    private CsvImporter createClassUnderTest = new CsvImporter();
    private String fileName = "mypass_backup.csv";

    private Context context = ApplicationProvider.getApplicationContext();

    private Path loadPasswordFile() throws URISyntaxException {
        final URL url = CsvImporter.class.getResource("/" + fileName);
        if (url == null) {
            throw new RuntimeException(String.format("Failed to load the file [%s]", fileName));
        }
        return Paths.get(url.toURI());
    }

    @Test
    public void testImportSuccess() throws Exception {
        final List<Password> passwords = createClassUnderTest.importFromCsv(Uri.fromFile(loadPasswordFile().toFile()), context.getContentResolver());
        assertThat(passwords.size(), is(2));
        assertThat(passwords.get(0), is(FakePassword1));
        assertThat(passwords.get(1), is(FakePassword2));
    }

}