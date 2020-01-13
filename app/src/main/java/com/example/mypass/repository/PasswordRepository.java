package com.example.mypass.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.mypass.model.Password;
import com.example.mypass.repository.PasswordContract.PasswordEntry;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PasswordRepository {

    private final String LOG_TAG = PasswordRepository.class.getName();

    private final PasswordDbHelper passwordDbHelper;

    public PasswordRepository(PasswordDbHelper passwordDbHelper) {
        this.passwordDbHelper = passwordDbHelper;
    }

    public List<Password> findAll() {
        try (
                SQLiteDatabase database = passwordDbHelper.getWritableDatabase();
                Cursor cursor = database.query(PasswordEntry.TABLE_NAME, null, null, null, null, null, PasswordEntry.COLUMN_CREATED_AT);
        ) {
            List<Password> passwords = new ArrayList<>(cursor.getCount());
            while (cursor.move(1)) {
                passwords.add(mapToPassword(cursor));
            }
            return passwords;
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Error findAll Passwords", ex);
            throw ex;
        }
    }

    public List<Password> search(String key) {
        String formattedKey = MessageFormat.format(" LIKE \"%{0}%\" ", key);
        try (
                final SQLiteDatabase database = passwordDbHelper.getWritableDatabase();
                Cursor cursor = database.query(PasswordEntry.TABLE_NAME, null,
                        PasswordEntry.COLUMN_TITLE + formattedKey, null, null, null, PasswordEntry.COLUMN_CREATED_AT)
        ) {
            List<Password> passwords = new ArrayList<>(cursor.getCount());
            while (cursor.move(1)) {
                passwords.add(mapToPassword(cursor));
            }
            return passwords;
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Error findAll Passwords", ex);
            throw ex;
        }
    }

    public void delete(long id) {
        String deleteQuery = MessageFormat.format(" {0} = {1} ", PasswordEntry._ID, id);
        try (
                final SQLiteDatabase database = passwordDbHelper.getWritableDatabase()
        ) {
            database.delete(PasswordEntry.TABLE_NAME, deleteQuery, null);
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Error delete password", ex);
            throw ex;
        }
    }

    public long save(Password password) {
        try (
                final SQLiteDatabase database = passwordDbHelper.getWritableDatabase()
        ) {
            ContentValues values = new ContentValues();
            values.put(PasswordEntry.COLUMN_CREATED_AT, password.getCreateAt().toString());
            values.put(PasswordEntry.COLUMN_NOTES, password.getNotes());
            values.put(PasswordEntry.COLUMN_IS_ACTIVE, password.isActive() ? 1 : 0);
            values.put(PasswordEntry.COLUMN_TITLE, password.getTitle());
            values.put(PasswordEntry.COLUMN_PASSWORD, password.getPassword());
            values.put(PasswordEntry.COLUMN_USERNAME, password.getUsername());
            values.put(PasswordEntry.COLUMN_WEBSITE, password.getWebsite());
            return database.insert(PasswordEntry.TABLE_NAME, null, values);
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Error inserting password", ex);
            throw ex;
        }
    }

    public void deleteAll() {
        try (final SQLiteDatabase database = passwordDbHelper.getWritableDatabase()) {
            database.execSQL(String.format("DELETE FROM %s", PasswordEntry.TABLE_NAME));
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Error deleting all passwords", ex);
            throw ex;
        }
    }

    private Password mapToPassword(Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex(PasswordEntry.COLUMN_TITLE));
        LocalDateTime createdAt = LocalDateTime.parse(cursor.getString(cursor.getColumnIndex(PasswordEntry.COLUMN_CREATED_AT)));
        String description = cursor.getString(cursor.getColumnIndex(PasswordEntry.COLUMN_NOTES));
        int id = cursor.getInt(cursor.getColumnIndex(PasswordEntry._ID));
        String username = cursor.getString(cursor.getColumnIndex(PasswordEntry.COLUMN_USERNAME));
        String website = cursor.getString(cursor.getColumnIndex(PasswordEntry.COLUMN_WEBSITE));
        String password = cursor.getString(cursor.getColumnIndex(PasswordEntry.COLUMN_PASSWORD));
        boolean isActive = cursor.getInt(cursor.getColumnIndex(PasswordEntry.COLUMN_IS_ACTIVE)) == 1;
        return new Password(id, title, username, password, description, website, createdAt, isActive);
    }
}
