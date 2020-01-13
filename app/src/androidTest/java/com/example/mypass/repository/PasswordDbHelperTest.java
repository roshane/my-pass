package com.example.mypass.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PasswordDbHelperTest {

    private Context testContext = ApplicationProvider.getApplicationContext();
    private String LOG_TAG = PasswordDbHelperTest.class.getSimpleName();

    private final Class mDocumentDbHelper = PasswordDbHelper.class;

    @Before
    public void setUp() {
        deleteDatabase();
    }

    @Test
    public void testDatabaseCreationScriptIsCorrect() {
        PasswordDbHelper classUnderTest = new PasswordDbHelper(testContext);
        String createStatement = classUnderTest.getDbCreateStatement();
        Log.d(LOG_TAG, createStatement);
        String expected = "CREATE TABLE documents ( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " title VARCHAR(30) NOT NULL," +
                " username VARCHAR(30) NOT NULL," +
                " password VARCHAR(30) NOT NULL," +
                " website VARCHAR(100)," +
                " notes VARCHAR(300)," +
                " is_active BOOLEAN DEFAULT TRUE, created_at TIMESTAMP )";
        assertNotNull("create statement cannot be null", createStatement);
        assertEquals("create statement should be correct", expected, createStatement);
    }

    @Test
    public void testDatabaseCreatedSuccessfully() throws Exception {
        SQLiteOpenHelper dbHelper = (SQLiteOpenHelper) mDocumentDbHelper.getDeclaredConstructor(Context.class)
                .newInstance(testContext);
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        assertTrue("database is accessible", writableDatabase.isOpen());
        String query = String.format("SELECT name FROM sqlite_master WHERE type='table' AND name='%s'", PasswordContract.PasswordEntry.TABLE_NAME);
        Cursor cursor = writableDatabase.rawQuery(query, null);
        assertTrue("table should exist", cursor.moveToFirst());
        cursor.close();
        writableDatabase.close();
    }

    void deleteDatabase() {
        try {
            Field databaseNameField = mDocumentDbHelper.getDeclaredField("DATABASE_NAME");
            databaseNameField.setAccessible(true);
            testContext.deleteDatabase((String) databaseNameField.get(null));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}