package com.example.mypass;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mypass.adapter.PasswordListAdapter;
import com.example.mypass.dependencies.MyPassApplication;
import com.example.mypass.model.Password;
import com.example.mypass.repository.PasswordRepository;
import com.example.mypass.util.DefaultPasswordGenerator;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static android.view.KeyEvent.KEYCODE_ENTER;
import static java.lang.String.format;

public class DashboardActivity extends AppCompatActivity {
    private ListView mListView;
    private ImageButton mBtnAddPassword;
    private ImageButton mBtnSearchPasswords;
    private ImageButton mBtnShowPopupMenu;
    private EditText mTextSearchKey;
    private ProgressBar mProgressBar;

    private final String LOG_TAG = DashboardActivity.class.getName();

    private PasswordRepository passwordRepository = null;
    private DefaultPasswordGenerator passwordGenerator = null;
    private DialogBusyIndicator busyIndicator;

    private final AtomicReference<PopupMenu> popupMenuAtomicReference = new AtomicReference<>();

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.passwordRepository = ((MyPassApplication) getApplication()).getPasswordRepository();
        this.passwordGenerator = ((MyPassApplication) getApplication()).getPasswordGenerator();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initializeComponents();
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        clearSearchResult();
        Log.d(LOG_TAG, "Clearing previous results");
    }

    private void initializeComponents() {
        busyIndicator = DialogBusyIndicator.newInstance(null, null);
        mListView = findViewById(R.id.password_list_view);
        mBtnAddPassword = findViewById(R.id.button_add_password);
        mBtnSearchPasswords = findViewById(R.id.button_search_passwords);
        mTextSearchKey = findViewById(R.id.text_search_key);
        mBtnShowPopupMenu = findViewById(R.id.btn_show_popup_menu);
        mProgressBar = findViewById(R.id.progressbar);

        mBtnAddPassword.setOnClickListener(this::showDialogFullscreen);
        mBtnSearchPasswords.setOnClickListener(this::searchSavedPasswords);
        mTextSearchKey.setOnKeyListener(this::searchOnEnterKeyDown);
        mBtnShowPopupMenu.setOnClickListener(this::showPopupMenu);
    }

    private void showPopupMenu(View view) {
        if (popupMenuAtomicReference.get() == null) {
            PopupMenu popupMenu = new PopupMenu(this, view);
            popupMenu.getMenuInflater().inflate(R.menu.dashboard_popup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(this::handleMenuItemCliek);
            popupMenuAtomicReference.set(popupMenu);
        }
        popupMenuAtomicReference.get().show();
    }

    private boolean handleMenuItemCliek(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_export_passwords:
                Log.d(LOG_TAG, "Menu item clicked " + menuItem.getTitle());
                busyIndicator.show(getSupportFragmentManager(), "busyindicator");
                break;
            case R.id.menu_import_passwords:
                break;
            case R.id.menu_delete_all_passwords:
                break;
            default:
                Log.d(LOG_TAG, "Invalid menu item click");
        }
        return true;
    }


    private boolean searchOnEnterKeyDown(View view, int keyCode, KeyEvent keyEvent) {
        if (keyCode == KEYCODE_ENTER) {
            searchSavedPasswords(view);
            return true;
        }
        return false;
    }

    private void searchSavedPasswords(View view) {
        int searchCount;
        final String searchKey = mTextSearchKey.getText().toString().trim();
        if (!searchKey.isEmpty()) {
            Password[] passwords = passwordRepository.searchByTitle(searchKey).toArray(new Password[0]);
            searchCount = passwords.length;
            showPasswords(passwords);
        } else {
            Password[] passwords = passwordRepository.findAll().toArray(new Password[0]);
            searchCount = passwords.length;
            showPasswords(passwords);
        }
        Toast.makeText(getApplicationContext(),
                format(Locale.getDefault(), "found %d passwords", searchCount), Toast.LENGTH_SHORT)
                .show();
    }

    private void showPasswords(Password[] passwords) {
        PasswordListAdapter passwordListAdapter = new PasswordListAdapter(getApplicationContext(),
                passwords,
                getSupportFragmentManager());
        mListView.setAdapter(passwordListAdapter);
    }

    private void clearSearchResult() {
        this.showPasswords(new Password[]{});
    }

    private void saveNewPassword(Password password) {
        Log.d(LOG_TAG, String.format(Locale.getDefault(), "Saving password for %s", password.getTitle()));
        try {
            passwordRepository.save(password);
            Toast.makeText(getApplicationContext(),
                    String.format(Locale.getDefault(), "Saving: %s", password.getTitle()), Toast.LENGTH_SHORT)
                    .show();
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Error saving password for " + password.getTitle(), ex);
            Toast.makeText(getApplicationContext(),
                    "Error saving password for " + password.getTitle(), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void showDialogFullscreen(View ignore) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AddPasswordFragment newFragment = new AddPasswordFragment(this::saveNewPassword, this.passwordGenerator, Optional.empty());
        fragmentTransaction.add(android.R.id.content, newFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        clearSearchResult();
    }
}
