package com.example.mypass.dependencies;

import android.app.Application;
import android.content.Context;

import com.example.mypass.repository.PasswordRepository;
import com.example.mypass.util.CsvExporter;
import com.example.mypass.util.DefaultPasswordGenerator;

public class MyPassApplication extends Application {

    private AppContainer appContainer;
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = getApplicationContext();
        this.appContainer = new AppContainer(this.mContext);
    }

    public PasswordRepository getPasswordRepository() {
        return this.appContainer.getPasswordRepository();
    }

    public DefaultPasswordGenerator getPasswordGenerator() {
        return this.appContainer.getPasswordGenerator();
    }

    public CsvExporter getCsvExporter() {
        return this.appContainer.getCsvExporter();
    }
}
