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
    private String friendListId;  // 친구 리스트 ID
    private GiftData giftData;  // 선물 데이터
    private Runnable onGiftAdded;  // 선물 추가 후 실행될 Runnable
    private EditText giftNameEditText;  // 선물 이름 입력 필드
    private EditText giftDateEditText;  // 선물 날짜 입력 필드
    private Button addGiftButton;  // 선물 추가 버튼
    private FirebaseDatabaseManager databaseManager;  // Firebase 데이터베이스 매니저
    private Calendar calendar;  // 날짜 선택을 위한 캘린더 객체
    private Context context;  // 컨텍스트 객체

    // 생성자: 친구 리스트 ID와 Runnable을 인자로 받음
    public AddGiftFragment(String friendListId, Runnable onGiftAdded) {
        this.friendListId = friendListId;
        this.onGiftAdded = onGiftAdded;
    }

    // 생성자: 친구 리스트 ID, Runnable, 선물 데이터를 인자로 받음
    public AddGiftFragment(String friendListId, Runnable onGiftAdded, GiftData giftData) {
        this.friendListId = friendListId;
        this.onGiftAdded = onGiftAdded;
        this.giftData = giftData;
    }

    // 프래그먼트가 컨텍스트에 연결될 때 호출됨
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    // 프래그먼트 뷰를 생성할 때 호출됨
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_gift, container, false);

        // 뷰 초기화
        giftNameEditText = view.findViewById(R.id.giftNameEditText);
        giftDateEditText = view.findViewById(R.id.giftDateEditText);
        addGiftButton = view.findViewById(R.id.addGiftButton);
        databaseManager = new FirebaseDatabaseManager();
        calendar = Calendar.getInstance();

        // 선물 데이터가 있을 경우, 해당 데이터를 입력 필드에 설정
        if (giftData != null) {
            giftNameEditText.setText(giftData.getGiftName());
            giftDateEditText.setText(giftData.getDate());
            addGiftButton.setText("Update Gift");
        }

        // 날짜 입력 필드를 클릭할 때 날짜 선택 다이얼로그를 표시
        giftDateEditText.setOnClickListener(v -> {
            new DatePickerDialog(getContext(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // 선물 추가/수정 버튼 클릭 리스너 설정
        addGiftButton.setOnClickListener(v -> addOrUpdateGift());

        return view;
    }

    // 선물 추가 또는 수정 메서드
    private void addOrUpdateGift() {
        String giftName = giftNameEditText.getText().toString().trim();
        String giftDate = giftDateEditText.getText().toString().trim();

        // 입력 필드가 비어있는 경우 에러 메시지 표시
        if (TextUtils.isEmpty(giftName) || TextUtils.isEmpty(giftDate)) {
            Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // 선물 데이터가 없을 경우 새로 생성
        if (giftData == null) {
            giftData = new GiftData();
            giftData.setFriendListID(friendListId);
        }

        // 선물 데이터 설정
        giftData.setGiftName(giftName);
        giftData.setDate(giftDate);

        // 선물 ID가 없을 경우 선물 추가
        if (giftData.getGiftID() == null) {
            databaseManager.addGift(giftData);
            Toast.makeText(context, "Gift added", Toast.LENGTH_SHORT).show();
        } else {
            // 선물 ID가 있을 경우 선물 업데이트
            databaseManager.updateGift(giftData, aVoid -> {
                Toast.makeText(context, "Gift updated", Toast.LENGTH_SHORT).show();
                dismiss();
                onGiftAdded.run();
            }, e -> Toast.makeText(context, "Failed to update gift", Toast.LENGTH_SHORT).show());
        }

        dismiss();
        onGiftAdded.run();
    }

    // 날짜 선택 리스너
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        }
    };

    // 날짜 라벨 업데이트 메서드
    private void updateDateLabel() {
        String myFormat = "yyyy-MM-dd"; // 날짜 포맷 설정
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        giftDateEditText.setText(sdf.format(calendar.getTime()));
    }
}
