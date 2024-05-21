package com.gcu.anniversary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.gcu.anniversary.firebase.FirebaseDatabaseManager;

import DTO.GiftData;

public class EditGiftFragment extends DialogFragment {
    private FirebaseDatabaseManager databaseManager;
    private EditText giftNameEditText;
    private EditText giftDateEditText;
    private GiftData giftData;
    private GiftEditedListener giftEditedListener;

    public EditGiftFragment(GiftData giftData, GiftEditedListener giftEditedListener) {
        this.giftData = giftData;
        this.giftEditedListener = giftEditedListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_gift, container, false);

        databaseManager = new FirebaseDatabaseManager();
        giftNameEditText = view.findViewById(R.id.giftNameEditText);
        giftDateEditText = view.findViewById(R.id.giftDateEditText);
        Button editGiftButton = view.findViewById(R.id.addGiftButton);

        giftNameEditText.setText(giftData.getGiftName());
        giftDateEditText.setText(giftData.getDate());

        editGiftButton.setText("Edit Gift");
        editGiftButton.setOnClickListener(v -> editGift());

        return view;
    }

    private void editGift() {
        String giftName = giftNameEditText.getText().toString().trim();
        String giftDate = giftDateEditText.getText().toString().trim();

        if (giftName.isEmpty() || giftDate.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        giftData.setGiftName(giftName);
        giftData.setDate(giftDate);

        databaseManager.getGiftsReference().child(giftData.getGiftID()).setValue(giftData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Gift updated successfully", Toast.LENGTH_SHORT).show();
                        dismiss();
                        if (giftEditedListener != null) {
                            giftEditedListener.onGiftEdited();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to update gift", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public interface GiftEditedListener {
        void onGiftEdited();
    }
}
