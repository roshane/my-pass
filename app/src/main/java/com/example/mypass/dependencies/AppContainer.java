package com.example.mypass.dependencies;

import android.content.Context;

import com.example.mypass.repository.PasswordDbHelper;
import com.example.mypass.repository.PasswordRepository;
import com.example.mypass.util.CsvExporter;
import com.example.mypass.util.CsvImporter;
import com.example.mypass.util.DefaultPasswordGenerator;

public class AppContainer {

    private final Context appContext;

    private final PasswordDbHelper passwordDbHelper;
    private final PasswordRepository passwordRepository;
    private final DefaultPasswordGenerator passwordGenerator;
    private final CsvExporter csvExporter;
    private final CsvImporter csvImporter;

    public AppContainer(Context appContext) {
        this.appContext = appContext;
        this.passwordDbHelper = new PasswordDbHelper(appContext);
        this.passwordRepository = new PasswordRepository(passwordDbHelper);
        this.csvExporter = new CsvExporter();
        this.passwordGenerator = new DefaultPasswordGenerator();
        this.csvImporter = new CsvImporter();
    }

    public PasswordRepository getPasswordRepository() {
        return passwordRepository;
    }

    public DefaultPasswordGenerator getPasswordGenerator() {
        return passwordGenerator;
    }

    public CsvExporter getCsvExporter() {
        return csvExporter;
    }

    public CsvImporter getCsvImporter() {
        return csvImporter;
    }
}
