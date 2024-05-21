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
    private FirebaseDatabaseManager databaseManager; // Firebase 데이터베이스 매니저
    private EditText giftNameEditText; // 선물 이름 입력 필드
    private EditText giftDateEditText; // 선물 날짜 입력 필드
    private GiftData giftData; // 수정할 선물 데이터
    private GiftEditedListener giftEditedListener; // 선물 수정 완료 리스너

    // 생성자: 수정할 선물 데이터와 리스너를 받아옴
    public EditGiftFragment(GiftData giftData, GiftEditedListener giftEditedListener) {
        this.giftData = giftData;
        this.giftEditedListener = giftEditedListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_gift, container, false);

        // 데이터베이스 매니저 초기화
        databaseManager = new FirebaseDatabaseManager();
        giftNameEditText = view.findViewById(R.id.giftNameEditText);
        giftDateEditText = view.findViewById(R.id.giftDateEditText);
        Button editGiftButton = view.findViewById(R.id.addGiftButton);

        // 기존 선물 데이터 설정
        giftNameEditText.setText(giftData.getGiftName());
        giftDateEditText.setText(giftData.getDate());

        // 버튼 텍스트를 'Edit Gift'로 설정
        editGiftButton.setText("Edit Gift");
        // 버튼 클릭 리스너 설정
        editGiftButton.setOnClickListener(v -> editGift());

        return view;
    }

    // 선물 정보를 수정하는 메서드
    private void editGift() {
        String giftName = giftNameEditText.getText().toString().trim();
        String giftDate = giftDateEditText.getText().toString().trim();

        // 입력 필드가 비어 있는지 확인
        if (giftName.isEmpty() || giftDate.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // 선물 데이터 업데이트
        giftData.setGiftName(giftName);
        giftData.setDate(giftDate);

        // Firebase 데이터베이스에 선물 데이터 저장
        databaseManager.getGiftsReference().child(giftData.getGiftID()).setValue(giftData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // 성공 메시지 표시 및 다이얼로그 닫기
                        Toast.makeText(getContext(), "Gift updated successfully", Toast.LENGTH_SHORT).show();
                        dismiss();
                        // 리스너 호출
                        if (giftEditedListener != null) {
                            giftEditedListener.onGiftEdited();
                        }
                    } else {
                        // 실패 메시지 표시
                        Toast.makeText(getContext(), "Failed to update gift", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 선물 수정 완료 리스너 인터페이스
    public interface GiftEditedListener {
        void onGiftEdited();
    }
}
