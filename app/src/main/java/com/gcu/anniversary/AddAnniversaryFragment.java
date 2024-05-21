package com.gcu.anniversary;

import android.app.DatePickerDialog;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;

import DTO.AnniversaryData;

public class AddAnniversaryFragment extends DialogFragment {
    private FirebaseDatabaseManager databaseManager;
    private EditText commentEditText;
    private Button dateButton;
    private Button addButton;
    private String selectedDate;
    private static final String ARG_ANNIVERSARY = "anniversary";
    private AnniversaryData anniversaryData;
    private OnAnniversaryUpdatedListener onAnniversaryUpdatedListener;

    // 기념일 업데이트 리스너 인터페이스 정의
    public interface OnAnniversaryUpdatedListener {
        void onAnniversaryUpdated();
    }

    // AddAnniversaryFragment 인스턴스 생성 메서드
    public static AddAnniversaryFragment newInstance(AnniversaryData anniversary, OnAnniversaryUpdatedListener listener) {
        AddAnniversaryFragment fragment = new AddAnniversaryFragment();
        fragment.setOnAnniversaryUpdatedListener(listener);
        Bundle args = new Bundle();
        args.putSerializable(ARG_ANNIVERSARY, anniversary);
        fragment.setArguments(args);
        return fragment;
    }

    // 기념일 업데이트 리스너 설정 메서드
    public void setOnAnniversaryUpdatedListener(OnAnniversaryUpdatedListener listener) {
        this.onAnniversaryUpdatedListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_anniversary, container, false);

        // 데이터베이스 매니저 및 뷰 초기화
        databaseManager = new FirebaseDatabaseManager();
        commentEditText = view.findViewById(R.id.commentEditText);
        dateButton = view.findViewById(R.id.dateButton);
        addButton = view.findViewById(R.id.addButton);

        // 전달된 기념일 데이터가 있는 경우 뷰에 설정
        if (getArguments() != null) {
            anniversaryData = (AnniversaryData) getArguments().getSerializable(ARG_ANNIVERSARY);
            if (anniversaryData != null) {
                commentEditText.setText(anniversaryData.getComment());
                selectedDate = anniversaryData.getDate();
                dateButton.setText(selectedDate);
                addButton.setText("Update Anniversary");
            }
        }

        // 날짜 버튼 클릭 시 날짜 선택 다이얼로그 표시
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // 추가/업데이트 버튼 클릭 시 기념일 추가 또는 업데이트
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (anniversaryData == null) {
                    addAnniversary();
                } else {
                    updateAnniversary();
                }
            }
        });

        return view;
    }

    // 날짜 선택 다이얼로그 표시 메서드
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

    // 기념일 추가 메서드
    private void addAnniversary() {
        String comment = commentEditText.getText().toString().trim();
        if (comment.isEmpty()) {
            commentEditText.setError("Enter a comment");
            return;
        }

        if (selectedDate == null) {
            Toast.makeText(getContext(), "Select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId == null) {
            return;
        }

        DatabaseReference anniversariesRef = databaseManager.getAnniversariesReference();
        String anniversaryId = anniversariesRef.push().getKey();
        if (anniversaryId == null) {
            return;
        }

        AnniversaryData anniversaryData = new AnniversaryData(anniversaryId, userId, selectedDate, comment);
        anniversariesRef.child(anniversaryId).setValue(anniversaryData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Anniversary added successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                    if (onAnniversaryUpdatedListener != null) {
                        onAnniversaryUpdatedListener.onAnniversaryUpdated();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add anniversary", Toast.LENGTH_SHORT).show());
    }

    // 기념일 업데이트 메서드
    private void updateAnniversary() {
        String comment = commentEditText.getText().toString().trim();
        if (comment.isEmpty()) {
            commentEditText.setError("Enter a comment");
            return;
        }

        if (selectedDate == null) {
            Toast.makeText(getContext(), "Select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        anniversaryData.setDate(selectedDate);
        anniversaryData.setComment(comment);

        databaseManager.updateAnniversary(anniversaryData, aVoid -> {
            Toast.makeText(getContext(), "Anniversary updated successfully", Toast.LENGTH_SHORT).show();
            dismiss();
            if (onAnniversaryUpdatedListener != null) {
                onAnniversaryUpdatedListener.onAnniversaryUpdated();
            }
        }, e -> Toast.makeText(getContext(), "Failed to update anniversary", Toast.LENGTH_SHORT).show());
    }
}
