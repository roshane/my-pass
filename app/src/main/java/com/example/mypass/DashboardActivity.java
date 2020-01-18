package com.example.mypass;

import android.content.Intent;
import android.net.Uri;
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
import com.example.mypass.util.CsvExporter;
import com.example.mypass.util.CsvImporter;
import com.example.mypass.util.DefaultPasswordGenerator;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static android.view.KeyEvent.KEYCODE_ENTER;
import static java.lang.String.format;

public class DashboardActivity extends AppCompatActivity {
    private static final int PICK_EXPORT_DIRECTOR_REQUEST_CODE = 2;
    private static final int PICK_BACKUP_FILE_REQUEST_CODE = 3;
    private static final int CSV_EXPORT_REQUEST_CODE = 4;
    private static final int CSV_IMPORT_REQUEST_CODE = 5;
    private static final String INTENT_EXTRA_FILE_PATH = "intent.Extra.FilePath";
    private static final String BUSY_INDICATOR_TAG = "busyIndicatorTag";
    private ListView mListView;
    private ImageButton mBtnAddPassword;
    private ImageButton mBtnSearchPasswords;
    private ImageButton mBtnShowPopupMenu;
    private EditText mTextSearchKey;
    private ProgressBar mProgressBar;

    private final String LOG_TAG = DashboardActivity.class.getName();

    private PasswordRepository passwordRepository = null;
    private DefaultPasswordGenerator passwordGenerator = null;
    private CsvExporter csvExporter = null;
    private CsvImporter csvImporter = null;

    private DialogBusyIndicator busyIndicator;

    private final AtomicReference<PopupMenu> popupMenuAtomicReference = new AtomicReference<>();

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.passwordRepository = ((MyPassApplication) getApplication()).getPasswordRepository();
        this.passwordGenerator = ((MyPassApplication) getApplication()).getPasswordGenerator();
        this.csvExporter = ((MyPassApplication) getApplication()).getCsvExporter();
        this.csvImporter = ((MyPassApplication) getApplication()).getCsvImporter();
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

        mBtnAddPassword.setOnClickListener(this::showAddPasswordDialog);
        mBtnSearchPasswords.setOnClickListener(this::searchSavedPasswords);
        mTextSearchKey.setOnKeyListener(this::searchOnEnterKeyDown);
        mBtnShowPopupMenu.setOnClickListener(this::showPopupMenu);
    }

    private void showPopupMenu(View view) {
        if (popupMenuAtomicReference.get() == null) {
            PopupMenu popupMenu = new PopupMenu(this, view);
            popupMenu.getMenuInflater().inflate(R.menu.dashboard_popup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(this::handleMenuItemClick);
            popupMenuAtomicReference.set(popupMenu);
        }
        popupMenuAtomicReference.get().show();
    }

    private boolean handleMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_export_passwords:
                pickBackupFileDirectory();
                break;
            case R.id.menu_import_passwords:
                pickBackFile();
                break;
            case R.id.menu_delete_all_passwords:
                deleteAllPasswords();
                break;
            default:
                Log.d(LOG_TAG, "Invalid menu item click");
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearSearchResult();
    }

    private void deleteAllPasswords() {
        clearSearchResult();
        passwordRepository.deleteAll();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {
            case PICK_EXPORT_DIRECTOR_REQUEST_CODE:
                if (resultCode == RESULT_OK && Objects.nonNull(data)) {
                    Uri directoryLocation = data.getData();
                    createBackupCsvFile(Objects.requireNonNull(directoryLocation));
                }
                break;

            case CSV_EXPORT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
//                   TODO  busyIndicator.showNow(getSupportFragmentManager(), BUSY_INDICATOR_TAG);
                    final List<Password> allPasswords = this.passwordRepository.findAll();
                    try {
                        csvExporter.exportToCsv(allPasswords, data.getData(), getContentResolver());
                        Toast.makeText(getApplicationContext(), "Backup file created.", Toast.LENGTH_LONG).show();
                    } catch (IOException ex) {
                        Log.e(LOG_TAG, "Error exporting passwords to file", ex);
                    }
//                    busyIndicator.dismissAllowingStateLoss();
                } else {
                    Log.d(LOG_TAG, "Error getting export directory location");
                }
                break;

            case PICK_BACKUP_FILE_REQUEST_CODE:
                if (resultCode == RESULT_OK && Objects.nonNull(data)) {
                    try {
                        final List<Password> passwords = csvImporter.importFromCsv(data.getData(), getContentResolver());
                        passwords.forEach(passwordRepository::save);
                        Toast.makeText(getApplicationContext(), "Imported " + passwords.size() + " passwords", Toast.LENGTH_LONG)
                                .show();
                    } catch (IOException ex) {
                        Log.e(LOG_TAG, "Error importing password", ex);
                    }
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void pickBackupFileDirectory() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, PICK_EXPORT_DIRECTOR_REQUEST_CODE);
    }

    private void pickBackFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_BACKUP_FILE_REQUEST_CODE);
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

    private void showAddPasswordDialog(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AddPasswordFragment newFragment = new AddPasswordFragment(this::saveNewPassword, this.passwordGenerator, Optional.empty());
        fragmentTransaction.add(android.R.id.content, newFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        clearSearchResult();
    }

    private void createBackupCsvFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, Constants.getFileName());
        intent.putExtra(INTENT_EXTRA_FILE_PATH, Paths.get(pickerInitialUri.toString(), Constants.getFileName()).toString());
        startActivityForResult(intent, CSV_EXPORT_REQUEST_CODE);
    }


}
