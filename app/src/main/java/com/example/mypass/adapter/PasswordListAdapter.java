package com.example.mypass.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mypass.AddPasswordFragment;
import com.example.mypass.DateFormatter;
import com.example.mypass.R;
import com.example.mypass.dependencies.MyPassApplication;
import com.example.mypass.model.Password;
import com.example.mypass.repository.PasswordRepository;
import com.example.mypass.util.DefaultPasswordGenerator;

import java.util.Optional;

public class PasswordListAdapter extends ArrayAdapter<Password> implements DateFormatter {
    private final Context context;
    private final Password[] passwords;
    private final FragmentManager fragmentManager;
    private final DefaultPasswordGenerator passwordGenerator;
    private final PasswordRepository passwordRepository;

    private final String LOG_TAG = PasswordListAdapter.class.getSimpleName();

    private Button mButtonCopyPassword;
    //    private LinearLayout mLinearLayout;
    private ImageView mImageViewEditPasswordButton;

    public PasswordListAdapter(@NonNull Context context, @NonNull Password[] passwords, @NonNull FragmentManager fragmentManager) {
        super(context, -1, passwords);
        this.context = context;
        this.passwords = passwords;
        this.fragmentManager = fragmentManager;
        this.passwordGenerator = ((MyPassApplication) context).getPasswordGenerator();
        this.passwordRepository = ((MyPassApplication) context).getPasswordRepository();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout cardView = (FrameLayout) layoutInflater.inflate(R.layout.password_card_view, null, false);

        TextView createDate = cardView.findViewById(R.id.created_date);
        TextView username = cardView.findViewById(R.id.username);
        TextView title = cardView.findViewById(R.id.password_title);
//        mLinearLayout = cardView.findViewById(R.id.password_action_pane);
        mImageViewEditPasswordButton = cardView.findViewById(R.id.toggle_password_button);

        mButtonCopyPassword = cardView.findViewById(R.id.button_copy_password);
        mButtonCopyPassword.setOnClickListener(view -> {
            final ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            try {
                clipboardManager.setPrimaryClip(ClipData.newPlainText(passwords[position].getPassword(), passwords[position].getPassword()));
                Toast.makeText(this.context, String.format("copied %s", passwords[position].getTitle()), Toast.LENGTH_SHORT)
                        .show();
            } catch (NullPointerException npe) {
                //TODO ignore for now
            }
        });

        mImageViewEditPasswordButton.setOnClickListener(view -> {
            Password selectedPassword = passwords[position];
            showEditPasswordViewScreen(selectedPassword);
        });

        title.setText(passwords[position].getTitle());
        username.setText(passwords[position].getUsername());
        String formattedDate = String.format("Created at: %s", DFHumanReadable.format(passwords[position].getCreateAt()));
        createDate.setText(formattedDate);
        return cardView;
    }

    private void onUpdatePassword(Password password) {
        Log.d(LOG_TAG, "Updating password " + password);
        this.passwordRepository.update(password);
        Toast.makeText(this.context, "updated: " + password.getTitle(), Toast.LENGTH_SHORT)
                .show();
    }

    private void showEditPasswordViewScreen(Password password) {
        FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction();
        AddPasswordFragment newFragment = new AddPasswordFragment(this::onUpdatePassword, this.passwordGenerator, Optional.of(password),Optional.empty());//TODO get from application DI
        fragmentTransaction.add(android.R.id.content, newFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }


}
