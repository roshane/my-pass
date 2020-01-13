package com.example.mypass;

import android.os.Bundle;
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

public class AddPasswordFragment extends DialogFragment {
    private EditText mEditTextTitle;
    private EditText mEditTextNotes;
    private EditText mEditTextPassword;
    private EditText mEditTextWebsite;
    private EditText mEditTextUsername;

    private PasswordSaveEventHandler passwordSaveEventHandler;

    private final DefaultPasswordGenerator passwordGenerator;
    private Map<Integer,Integer> formInputFields;

    {
        formInputFields = new HashMap<>();
        formInputFields.put(R.id.form_txt_title, R.id.btn_clear_txt_title);
        formInputFields.put(R.id.form_txt_username, R.id.btn_clear_txt_username);
        formInputFields.put(R.id.form_txt_password, R.id.btn_clear_txt_password);
        formInputFields.put(R.id.form_txt_website, R.id.btn_clear_txt_website);
        formInputFields.put(R.id.form_txt_notes, R.id.btn_clear_txt_notes);
    }

    AddPasswordFragment(PasswordSaveEventHandler passwordSaveEventHandler,
                        DefaultPasswordGenerator passwordGenerator) {
        this.passwordSaveEventHandler = passwordSaveEventHandler;
        this.passwordGenerator = passwordGenerator;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_add_password, container, false);
        initializeComponents(mRootView);
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
            passwordSaveEventHandler.onSave(new Password(passwordTitle, username, website, passwordNotes, password));
            dismiss();
        });
    }

}
