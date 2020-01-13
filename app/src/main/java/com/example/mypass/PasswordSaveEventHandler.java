package com.example.mypass;

import com.example.mypass.model.Password;

public interface PasswordSaveEventHandler {
    void onSave(Password password);
}
