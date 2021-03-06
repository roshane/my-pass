package com.example.mypass;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mypass.model.Password;
import com.example.mypass.util.DefaultPasswordGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class AddPasswordFragment extends DialogFragment {
    private EditText mEditTextTitle;
    private EditText mEditTextNotes;
    private EditText mEditTextPassword;
    private EditText mEditTextWebsite;
    private EditText mEditTextUsername;
    private Button mButtonTogglePassword;

    private Optional<Password> maybePassword;
    private PasswordSaveEventHandler passwordSaveEventHandler;

    private final DefaultPasswordGenerator passwordGenerator;
    private Map<Integer, Integer> formInputFields;

    private boolean isPasswordVisible = false;

    {
        formInputFields = new HashMap<>();
        formInputFields.put(R.id.form_txt_title, R.id.btn_clear_txt_title);
        formInputFields.put(R.id.form_txt_username, R.id.btn_clear_txt_username);
        formInputFields.put(R.id.form_txt_password, R.id.btn_clear_txt_password);
        formInputFields.put(R.id.form_txt_website, R.id.btn_clear_txt_website);
        formInputFields.put(R.id.form_txt_notes, R.id.btn_clear_txt_notes);
    }

    public AddPasswordFragment(PasswordSaveEventHandler passwordSaveEventHandler,
                               DefaultPasswordGenerator passwordGenerator,
                               Optional<Password> maybePassword) {
        this.passwordSaveEventHandler = passwordSaveEventHandler;
        this.passwordGenerator = passwordGenerator;
        this.maybePassword = maybePassword;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_add_password, container, false);
        initializeComponents(mRootView);
        maybePassword.ifPresent(this::showCurrentPassword);
        return mRootView;
    }

    private String generatePassword() {
        return this.passwordGenerator.generatePassword();
    }

    private void initializeComponents(View rootView) {

        mEditTextTitle = rootView.findViewById(R.id.form_txt_title);
        mEditTextNotes = rootView.findViewById(R.id.form_txt_notes);
        mEditTextPassword = rootView.findViewById(R.id.form_txt_password);
        mEditTextWebsite = rootView.findViewById(R.id.form_txt_website);
        mEditTextUsername = rootView.findViewById(R.id.form_txt_username);
        mButtonTogglePassword = rootView.findViewById(R.id.button_toggle_password);

        formInputFields.keySet().forEach(key -> {
            ImageButton button = rootView.findViewById(formInputFields.get(key));
            button.setOnClickListener(v -> {
                ((EditText) rootView.findViewById(key)).setText("");
            });
        });

        Button mButtonGeneratePassword = rootView.findViewById(R.id.button_generate_password);

        rootView.findViewById(R.id.bt_close).setOnClickListener(l -> dismiss());

        mButtonGeneratePassword.setOnClickListener(view -> this.mEditTextPassword.setText(this.generatePassword()));

        rootView.findViewById(R.id.bt_save).setOnClickListener(l -> {
            String passwordTitle = Objects.requireNonNull(mEditTextTitle.getText()).toString();
            String passwordNotes = Objects.requireNonNull(mEditTextNotes.getText()).toString();
            String password = Objects.requireNonNull(mEditTextPassword.getText()).toString();
            String website = Objects.requireNonNull(mEditTextWebsite.getText().toString());
            String username = Objects.requireNonNull(mEditTextUsername.getText().toString());
            if (maybePassword.isPresent()) {
                Password pass = maybePassword.get();
                pass.setTitle(passwordTitle);
                pass.setPassword(password);
                pass.setUsername(username);
                pass.setNotes(passwordNotes);
                pass.setWebsite(website);
                passwordSaveEventHandler.onSave(pass);
            } else {
                passwordSaveEventHandler.onSave(new Password(passwordTitle, username, website, passwordNotes, password));
            }
            dismiss();
        });

        mButtonTogglePassword.setOnClickListener(this::togglePasswordView);
    }

    private void togglePasswordView(View view) {
        if (!isPasswordVisible) {
            mEditTextPassword.setTransformationMethod(null);
            isPasswordVisible = true;
            mButtonTogglePassword.setText(R.string.button_toggle_pw_hidePassword);
        } else {
            mEditTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            isPasswordVisible = false;
            mButtonTogglePassword.setText(R.string.button_toggle_pw_showPassword);
        }
    }

    private void showCurrentPassword(Password password) {
        mEditTextTitle.setText(password.getTitle());
        mEditTextNotes.setText(password.getNotes());
        mEditTextPassword.setText(password.getPassword());
        mEditTextWebsite.setText(password.getWebsite());
        mEditTextUsername.setText(password.getUsername());
    }

}
