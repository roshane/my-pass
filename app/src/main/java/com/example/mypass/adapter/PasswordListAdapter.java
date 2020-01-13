package com.example.mypass.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mypass.DateFormatter;
import com.example.mypass.R;
import com.example.mypass.model.Password;

public class PasswordListAdapter extends ArrayAdapter<Password> implements DateFormatter {
    private final Context context;
    private final Password[] passwords;

    private Button mButtonCopyPassword;
    private LinearLayout mLinearLayout;

    public PasswordListAdapter(@NonNull Context context, Password[] passwords) {
        super(context, -1, passwords);
        this.context = context;
        this.passwords = passwords;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout cardView = (FrameLayout) layoutInflater.inflate(R.layout.password_card_view, null, false);

        TextView createDate = cardView.findViewById(R.id.created_date);
        TextView username = cardView.findViewById(R.id.username);
        TextView title = cardView.findViewById(R.id.password_title);
        mLinearLayout = cardView.findViewById(R.id.password_action_pane);

        mButtonCopyPassword = cardView.findViewById(R.id.button_copy_password);
        mButtonCopyPassword.setOnClickListener(view -> {
            final ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            try {
                clipboardManager.setPrimaryClip(ClipData.newPlainText(passwords[position].getPassword(), passwords[position].getPassword()));
                Toast.makeText(this.context, String.format("copied %s",passwords[position].getTitle()), Toast.LENGTH_SHORT)
                        .show();
            } catch (NullPointerException npe) {
                //TODO ignore for now
            }
        });

        title.setText(passwords[position].getTitle());
        username.setText(passwords[position].getUsername());
        String formattedDate = String.format("Created at: %s", DF.format(passwords[position].getCreateAt()));
        createDate.setText(formattedDate);
        return cardView;
    }


}
