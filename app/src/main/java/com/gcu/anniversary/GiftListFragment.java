package com.gcu.anniversary;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.gcu.anniversary.firebase.FirebaseDatabaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import DTO.GiftData;

public class GiftListFragment extends DialogFragment {
    private String friendListId; // 친구 목록 ID
    private FirebaseDatabaseManager databaseManager; // Firebase 데이터베이스 관리자
    private ListView giftListView; // 선물 리스트 뷰
    private List<GiftData> giftDataList; // 선물 데이터 리스트
    private GiftAdapter giftAdapter; // 선물 어댑터
    private ImageButton addGiftButton; // 선물 추가 버튼
    private ImageButton closeButton; // 닫기 버튼

    public GiftListFragment(String friendListId) {
        this.friendListId = friendListId; // 생성자에서 친구 목록 ID 설정
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gift_list, container, false); // 레이아웃 인플레이트

        databaseManager = new FirebaseDatabaseManager(); // Firebase 데이터베이스 관리자 초기화
        giftListView = view.findViewById(R.id.giftListView); // 선물 리스트 뷰 초기화
        addGiftButton = view.findViewById(R.id.addGiftButton); // 선물 추가 버튼 초기화
        closeButton = view.findViewById(R.id.closeButton); // 닫기 버튼 초기화

        giftDataList = new ArrayList<>(); // 선물 데이터 리스트 초기화
        giftAdapter = new GiftAdapter(getContext(), giftDataList); // 선물 어댑터 초기화
        giftListView.setAdapter(giftAdapter); // 선물 리스트 뷰에 어댑터 설정

        addGiftButton.setOnClickListener(v -> showAddGiftDialog()); // 선물 추가 버튼 클릭 리스너 설정
        closeButton.setOnClickListener(v -> dismiss()); // 닫기 버튼 클릭 리스너 설정

        fetchGifts(); // 선물 데이터 가져오기

        return view; // 뷰 반환
    }

    // Firebase에서 선물 데이터를 가져오는 메서드
    private void fetchGifts() {
        databaseManager.getGiftsReference().orderByChild("friendListID").equalTo(friendListId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        giftDataList.clear(); // 기존 데이터 초기화
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            GiftData gift = snapshot.getValue(GiftData.class); // 선물 데이터 파싱
                            if (gift != null) {
                                giftDataList.add(gift); // 선물 데이터 리스트에 추가
                            }
                        }
                        sortAndDisplayGifts(); // 선물 데이터를 정렬 및 표시
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("GiftListFragment", "Failed to fetch gifts: " + databaseError.getMessage());
                    }
                });
    }

    // 선물 데이터를 날짜 순으로 정렬하고 화면에 표시하는 메서드
    private void sortAndDisplayGifts() {
        Collections.sort(giftDataList, (gift1, gift2) -> gift2.getDate().compareTo(gift1.getDate())); // 날짜 내림차순 정렬
        giftAdapter.notifyDataSetChanged(); // 어댑터 갱신
    }

    // 선물 추가 다이얼로그를 표시하는 메서드
    private void showAddGiftDialog() {
        AddGiftFragment addGiftFragment = new AddGiftFragment(friendListId, this::fetchGifts);
        addGiftFragment.show(getParentFragmentManager(), "AddGiftFragment");
    }

    // 선물 어댑터 클래스
    private class GiftAdapter extends ArrayAdapter<GiftData> {
        private List<GiftData> gifts; // 선물 데이터 리스트

        public GiftAdapter(Context context, List<GiftData> gifts) {
            super(context, R.layout.gift_list_item, gifts);
            this.gifts = gifts; // 선물 데이터 리스트 초기화
        }

        @NonNull
        @Override
        public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.gift_list_item, parent, false);
            }

            GiftData gift = gifts.get(position); // 현재 위치의 선물 데이터 가져오기
            TextView giftInfo = convertView.findViewById(R.id.giftInfo); // 선물 정보 텍스트 뷰
            ImageButton editButton = convertView.findViewById(R.id.editButton); // 수정 버튼
            ImageButton deleteButton = convertView.findViewById(R.id.deleteButton); // 삭제 버튼

            giftInfo.setText(gift.getDate() + " - " + gift.getGiftName()); // 선물 정보 설정

            editButton.setOnClickListener(v -> showEditGiftDialog(gift)); // 수정 버튼 클릭 리스너 설정
            deleteButton.setOnClickListener(v -> deleteGift(gift.getGiftID())); // 삭제 버튼 클릭 리스너 설정

            return convertView; // 뷰 반환
        }

        // 선물 수정 다이얼로그를 표시하는 메서드
        private void showEditGiftDialog(GiftData gift) {
            AddGiftFragment addGiftFragment = new AddGiftFragment(friendListId, GiftListFragment.this::fetchGifts, gift);
            addGiftFragment.show(GiftListFragment.this.getParentFragmentManager(), "EditGiftFragment");
        }

        // 선물을 삭제하는 메서드
        private void deleteGift(String giftID) {
            databaseManager.deleteGift(giftID, aVoid -> {
                Toast.makeText(getContext(), "Gift deleted", Toast.LENGTH_SHORT).show();
                fetchGifts(); // 선물 데이터 갱신
            }, e -> Toast.makeText(getContext(), "Failed to delete gift", Toast.LENGTH_SHORT).show());
        }
    }
}
