package com.example.mypass.dependencies;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

public class AppContainerTest {
    private final Context testAppContext = ApplicationProvider.getApplicationContext();

    @Test
    public void testPasswordRepositoryInitializationSucceeds() {
        final AppContainer appContainer = new AppContainer(testAppContext);
        assertThat(appContainer.getPasswordRepository(), notNullValue());
    }
}