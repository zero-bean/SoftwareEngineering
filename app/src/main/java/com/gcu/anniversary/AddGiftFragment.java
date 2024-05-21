package com.gcu.anniversary;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.gcu.anniversary.firebase.FirebaseDatabaseManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import DTO.GiftData;

public class AddGiftFragment extends DialogFragment {
    private String friendListId;
    private GiftData giftData;
    private Runnable onGiftAdded;
    private EditText giftNameEditText;
    private EditText giftDateEditText;
    private Button addGiftButton;
    private FirebaseDatabaseManager databaseManager;
    private Calendar calendar;
    private Context context;

    public AddGiftFragment(String friendListId, Runnable onGiftAdded) {
        this.friendListId = friendListId;
        this.onGiftAdded = onGiftAdded;
    }

    public AddGiftFragment(String friendListId, Runnable onGiftAdded, GiftData giftData) {
        this.friendListId = friendListId;
        this.onGiftAdded = onGiftAdded;
        this.giftData = giftData;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_gift, container, false);

        giftNameEditText = view.findViewById(R.id.giftNameEditText);
        giftDateEditText = view.findViewById(R.id.giftDateEditText);
        addGiftButton = view.findViewById(R.id.addGiftButton);
        databaseManager = new FirebaseDatabaseManager();
        calendar = Calendar.getInstance();

        if (giftData != null) {
            giftNameEditText.setText(giftData.getGiftName());
            giftDateEditText.setText(giftData.getDate());
            addGiftButton.setText("Update Gift");
        }

        giftDateEditText.setOnClickListener(v -> {
            new DatePickerDialog(getContext(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        addGiftButton.setOnClickListener(v -> addOrUpdateGift());

        return view;
    }

    private void addOrUpdateGift() {
        String giftName = giftNameEditText.getText().toString().trim();
        String giftDate = giftDateEditText.getText().toString().trim();

        if (TextUtils.isEmpty(giftName) || TextUtils.isEmpty(giftDate)) {
            Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (giftData == null) {
            giftData = new GiftData();
            giftData.setFriendListID(friendListId);
        }

        giftData.setGiftName(giftName);
        giftData.setDate(giftDate);

        if (giftData.getGiftID() == null) {
            databaseManager.addGift(giftData);
            Toast.makeText(context, "Gift added", Toast.LENGTH_SHORT).show();
        } else {
            databaseManager.updateGift(giftData, aVoid -> {
                Toast.makeText(context, "Gift updated", Toast.LENGTH_SHORT).show();
                dismiss();
                onGiftAdded.run();
            }, e -> Toast.makeText(context, "Failed to update gift", Toast.LENGTH_SHORT).show());
        }

        dismiss();
        onGiftAdded.run();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        }
    };

    private void updateDateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        giftDateEditText.setText(sdf.format(calendar.getTime()));
    }
}
