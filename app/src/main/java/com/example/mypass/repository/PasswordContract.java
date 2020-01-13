package com.example.mypass.repository;

import android.provider.BaseColumns;

class PasswordContract {

    static final class PasswordEntry implements BaseColumns {
        static final String TABLE_NAME = "documents";

        static final String COLUMN_TITLE = "title";
        static final String COLUMN_USERNAME = "username";
        static final String COLUMN_WEBSITE = "website";
        static final String COLUMN_NOTES = "notes";
        static final String COLUMN_CREATED_AT = "created_at";
        static final String COLUMN_IS_ACTIVE = "is_active";
        static final String COLUMN_PASSWORD = "password";
    }

}
