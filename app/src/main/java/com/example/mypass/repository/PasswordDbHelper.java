package com.example.mypass.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.example.mypass.repository.PasswordContract.PasswordEntry;

public class PasswordDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MYPASS_STORAGE.db";
    private static final int DATABASE_VERSION = 1;
    private static final String SPACE = " ";

    public PasswordDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createStatement = getDbCreateStatement();
        db.execSQL(createStatement);
    }

    String getDbCreateStatement() {
        return new StringBuilder("CREATE TABLE").append(SPACE)
                    .append(PasswordEntry.TABLE_NAME).append(SPACE)
                    .append("(").append(SPACE)
                    .append(PasswordEntry._ID).append(SPACE).append("INTEGER PRIMARY KEY AUTOINCREMENT,").append(SPACE)
                    .append(PasswordEntry.COLUMN_TITLE).append(SPACE).append("VARCHAR(30) NOT NULL,").append(SPACE)
                    .append(PasswordEntry.COLUMN_USERNAME).append(SPACE).append("VARCHAR(30) NOT NULL,").append(SPACE)
                    .append(PasswordEntry.COLUMN_PASSWORD).append(SPACE).append("VARCHAR(30) NOT NULL,").append(SPACE)
                    .append(PasswordEntry.COLUMN_WEBSITE).append(SPACE).append("VARCHAR(100),").append(SPACE)
                    .append(PasswordEntry.COLUMN_NOTES).append(SPACE).append("VARCHAR(300),").append(SPACE)
                    .append(PasswordEntry.COLUMN_IS_ACTIVE).append(SPACE).append("BOOLEAN DEFAULT TRUE,").append(SPACE)
                    .append(PasswordEntry.COLUMN_CREATED_AT).append(SPACE).append("TIMESTAMP").append(SPACE)
                    .append(")")
                    .toString();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO nothing since version is 1
    }
}
