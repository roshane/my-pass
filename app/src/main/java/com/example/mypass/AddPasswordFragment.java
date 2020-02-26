package com.example.mypass;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mypass.model.Password;
import com.example.mypass.util.DefaultPasswordGenerator;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

public class AddPasswordFragment extends DialogFragment {
    private EditText mEditTextTitle;
    private EditText mEditTextNotes;
    private EditText mEditTextPassword;
    private EditText mEditTextWebsite;
    private EditText mEditTextUsername;
    private Button mButtonTogglePassword;
    private Button mButtonCancelEdit;

    private TextView mTextViewTitle;
    private TextView mTextViewUsername;
    private TextView mTextViewPassword;
    private TextView mTextViewWebsite;
    private TextView mTextViewDescription;

    private LinearLayout mListViewPasswordEditView;
    private LinearLayout mListViewPasswordDisplayView;

    private Optional<Password> maybePassword;
    private PasswordSaveEventHandler passwordSaveEventHandler;

    private final DefaultPasswordGenerator passwordGenerator;
    private ViewMode viewMode;

    private Button mButtonEditPassword;
    private Button mButtonSavePassword;

    private boolean isPasswordVisible = false;

    public AddPasswordFragment(PasswordSaveEventHandler passwordSaveEventHandler,
                               DefaultPasswordGenerator passwordGenerator,
                               Optional<Password> maybePassword) {
        this.passwordSaveEventHandler = passwordSaveEventHandler;
        this.passwordGenerator = passwordGenerator;
        this.maybePassword = maybePassword;
        this.viewMode = ViewMode.READONLY;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_add_password, container, false);
        initializeComponents(mRootView);
        maybePassword.ifPresent(this::showCurrentPasswordInEditView);
        maybePassword.ifPresent(this::showCurrentPasswordInDetailView);
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

        mTextViewTitle = rootView.findViewById(R.id.text_title);
        mTextViewUsername = rootView.findViewById(R.id.text_username);
        mTextViewPassword = rootView.findViewById(R.id.text_password);
        mTextViewWebsite = rootView.findViewById(R.id.text_website);
        mTextViewDescription = rootView.findViewById(R.id.text_description);

        mButtonEditPassword = rootView.findViewById(R.id.button_edit_password);
        mButtonSavePassword = rootView.findViewById(R.id.button_save_password);
        mButtonCancelEdit = rootView.findViewById(R.id.button_cancel_edit);

        mListViewPasswordEditView = rootView.findViewById(R.id.password_edit_view);
        mListViewPasswordDisplayView = rootView.findViewById(R.id.password_detail_view);

        Button mButtonGeneratePassword = rootView.findViewById(R.id.button_generate_password);

        rootView.findViewById(R.id.bt_close).setOnClickListener(l -> dismiss());

        mButtonGeneratePassword.setOnClickListener(view -> this.mEditTextPassword.setText(this.generatePassword()));

        mButtonEditPassword.setOnClickListener(view -> this.toggleViewMode(ViewMode.EDIT));

        mButtonSavePassword.setOnClickListener(l -> {
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
        mButtonEditPassword.setOnClickListener(view -> this.displayEditView());
        mButtonCancelEdit.setOnClickListener(view -> this.displayDetailView());
        toggleViewMode(this.viewMode);
    }

    private void toggleViewMode(ViewMode viewMode) {
        if (viewMode == ViewMode.READONLY) {
            this.displayDetailView();
        } else {
            this.displayEditView();
        }
    }

    private void displayDetailView() {
        mListViewPasswordDisplayView.setVisibility(View.VISIBLE);
        mListViewPasswordEditView.setVisibility(View.GONE);
        mButtonEditPassword.setVisibility(View.VISIBLE);
        mButtonSavePassword.setVisibility(View.GONE);
    }

    private void displayEditView() {
        mListViewPasswordDisplayView.setVisibility(View.GONE);
        mListViewPasswordEditView.setVisibility(View.VISIBLE);
        mButtonEditPassword.setVisibility(View.GONE);
        mButtonSavePassword.setVisibility(View.VISIBLE);
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

    private void showCurrentPasswordInEditView(Password password) {
        mEditTextTitle.setText(password.getTitle());
        mEditTextNotes.setText(password.getNotes());
        mEditTextPassword.setText(password.getPassword());
        mEditTextWebsite.setText(password.getWebsite());
        mEditTextUsername.setText(password.getUsername());
    }

    private void showCurrentPasswordInDetailView(Password password) {
        mTextViewTitle.setText(password.getTitle());
        mTextViewUsername.setText(password.getUsername());
        StringBuilder sb = new StringBuilder(password.getPassword().length());
        IntStream.rangeClosed(0, password.getPassword().length())
                .forEach(i -> sb.append("*"));
        mTextViewPassword.setText(sb.toString());
        mTextViewWebsite.setText(password.getWebsite());
        mTextViewDescription.setText(password.getNotes());
    }

}
