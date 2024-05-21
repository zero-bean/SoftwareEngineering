package com.gcu.anniversary;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Parcelable;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import DTO.AnniversaryData;

public class EditAnniversaryFragment extends DialogFragment {
    private static final String ARG_ANNIVERSARY = "anniversary";

    private AnniversaryData anniversaryData;
    private EditText commentEditText;
    private Button dateButton;
    private Button saveButton;
    private Button deleteButton;
    private String selectedDate;

    public static EditAnniversaryFragment newInstance(AnniversaryData anniversary) {
        EditAnniversaryFragment fragment = new EditAnniversaryFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ANNIVERSARY, (Parcelable) anniversary);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_anniversary, container, false);

        if (getArguments() != null) {
            anniversaryData = getArguments().getParcelable(ARG_ANNIVERSARY);
        }

        commentEditText = view.findViewById(R.id.commentEditText);
        dateButton = view.findViewById(R.id.dateButton);
        saveButton = view.findViewById(R.id.saveButton);
        deleteButton = view.findViewById(R.id.deleteButton);

        if (anniversaryData != null) {
            commentEditText.setText(anniversaryData.getComment());
            selectedDate = anniversaryData.getDate();
            dateButton.setText(selectedDate);
        }

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAnniversary();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAnniversary();
            }
        });

        return view;
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                dateButton.setText(selectedDate);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void saveAnniversary() {
        String comment = commentEditText.getText().toString().trim();
        if (comment.isEmpty()) {
            commentEditText.setError("Enter a comment");
            return;
        }

        if (selectedDate == null) {
            Toast.makeText(getContext(), "Select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference anniversaryRef = FirebaseDatabase.getInstance().getReference("anniversaries").child(anniversaryData.getId());
        anniversaryData.setComment(comment);
        anniversaryData.setDate(selectedDate);
        anniversaryRef.setValue(anniversaryData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Anniversary updated successfully", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed to update anniversary", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteAnniversary() {
        DatabaseReference anniversaryRef = FirebaseDatabase.getInstance().getReference("anniversaries").child(anniversaryData.getId());
        anniversaryRef.removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Anniversary deleted successfully", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed to delete anniversary", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
