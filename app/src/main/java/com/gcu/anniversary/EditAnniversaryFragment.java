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
    private static final String ARG_ANNIVERSARY = "anniversary"; // 인텐트에서 anniversary 데이터를 가져오기 위한 키

    private AnniversaryData anniversaryData; // 기념일 데이터
    private EditText commentEditText; // 기념일 코멘트 입력 필드
    private Button dateButton; // 날짜 선택 버튼
    private Button saveButton; // 저장 버튼
    private Button deleteButton; // 삭제 버튼
    private String selectedDate; // 선택된 날짜

    // 인스턴스 생성 메서드, AnniversaryData 객체를 번들에 담아 전달
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
            anniversaryData = getArguments().getParcelable(ARG_ANNIVERSARY); // 번들에서 기념일 데이터 가져오기
        }

        commentEditText = view.findViewById(R.id.commentEditText); // 코멘트 입력 필드 초기화
        dateButton = view.findViewById(R.id.dateButton); // 날짜 버튼 초기화
        saveButton = view.findViewById(R.id.saveButton); // 저장 버튼 초기화
        deleteButton = view.findViewById(R.id.deleteButton); // 삭제 버튼 초기화

        if (anniversaryData != null) {
            commentEditText.setText(anniversaryData.getComment()); // 코멘트 설정
            selectedDate = anniversaryData.getDate(); // 날짜 설정
            dateButton.setText(selectedDate); // 날짜 버튼 텍스트 설정
        }

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(); // 날짜 선택 다이얼로그 표시
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAnniversary(); // 기념일 저장
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAnniversary(); // 기념일 삭제
            }
        });

        return view;
    }

    // 날짜 선택 다이얼로그를 표시하는 메서드
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                dateButton.setText(selectedDate); // 선택된 날짜를 버튼에 표시
            }
        }, year, month, day);
        datePickerDialog.show(); // 다이얼로그 표시
    }

    // 기념일을 저장하는 메서드
    private void saveAnniversary() {
        String comment = commentEditText.getText().toString().trim(); // 코멘트 가져오기
        if (comment.isEmpty()) {
            commentEditText.setError("Enter a comment"); // 코멘트가 비어있을 경우 에러 메시지 표시
            return;
        }

        if (selectedDate == null) {
            Toast.makeText(getContext(), "Select a date", Toast.LENGTH_SHORT).show(); // 날짜가 선택되지 않았을 경우 토스트 메시지 표시
            return;
        }

        // Firebase 데이터베이스 참조
        DatabaseReference anniversaryRef = FirebaseDatabase.getInstance().getReference("anniversaries").child(anniversaryData.getId());
        anniversaryData.setComment(comment); // 코멘트 설정
        anniversaryData.setDate(selectedDate); // 날짜 설정
        anniversaryRef.setValue(anniversaryData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Anniversary updated successfully", Toast.LENGTH_SHORT).show(); // 성공 메시지 표시
                        dismiss(); // 다이얼로그 닫기
                    } else {
                        Toast.makeText(getContext(), "Failed to update anniversary", Toast.LENGTH_SHORT).show(); // 실패 메시지 표시
                    }
                });
    }

    // 기념일을 삭제하는 메서드
    private void deleteAnniversary() {
        // Firebase 데이터베이스 참조
        DatabaseReference anniversaryRef = FirebaseDatabase.getInstance().getReference("anniversaries").child(anniversaryData.getId());
        anniversaryRef.removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Anniversary deleted successfully", Toast.LENGTH_SHORT).show(); // 성공 메시지 표시
                        dismiss(); // 다이얼로그 닫기
                    } else {
                        Toast.makeText(getContext(), "Failed to delete anniversary", Toast.LENGTH_SHORT).show(); // 실패 메시지 표시
                    }
                });
    }
}
