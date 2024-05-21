package com.gcu.anniversary;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.gcu.anniversary.firebase.FirebaseDatabaseManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import DTO.AnniversaryData;

public class CalendarFragment extends Fragment implements AddAnniversaryFragment.OnAnniversaryUpdatedListener {
    private FirebaseDatabaseManager databaseManager; // Firebase 데이터베이스 매니저
    private ListView anniversaryListView; // 기념일 목록을 표시하는 ListView
    private AnniversaryAdapter anniversaryAdapter; // 기념일 목록 어댑터
    private List<AnniversaryData> anniversaryDataList; // 기념일 데이터 리스트
    private ValueEventListener anniversaryEventListener; // 기념일 이벤트 리스너

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // 초기화 작업
        databaseManager = new FirebaseDatabaseManager();
        anniversaryListView = view.findViewById(R.id.calendarListView);

        anniversaryDataList = new ArrayList<>();
        anniversaryAdapter = new AnniversaryAdapter(anniversaryDataList);
        anniversaryListView.setAdapter(anniversaryAdapter);

        fetchAnniversaries();

        // 툴바 설정 및 메뉴 아이템 클릭 리스너 설정
        Toolbar toolbar = view.findViewById(R.id.toolbar_anniversary);
        toolbar.inflateMenu(R.menu.anniversary_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.add_anniversary_button) {
                    showAddAnniversaryDialog();
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    // 기념일 데이터를 가져오는 메서드
    private void fetchAnniversaries() {
        String userId = getCurrentUserId();
        if (userId == null) {
            Log.e("CalendarFragment", "User ID is null");
            return;
        }

        // 이전 리스너 제거
        if (anniversaryEventListener != null) {
            databaseManager.getAnniversariesReference().removeEventListener(anniversaryEventListener);
        }

        // 새 리스너 설정
        anniversaryEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                anniversaryDataList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AnniversaryData anniversary = snapshot.getValue(AnniversaryData.class);
                    if (anniversary != null) {
                        anniversaryDataList.add(anniversary);
                    }
                }
                sortAnniversariesByDate();
                anniversaryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CalendarFragment", "Failed to fetch anniversaries: " + databaseError.getMessage());
            }
        };

        // 기념일 데이터를 사용자 ID로 정렬하여 가져옴
        databaseManager.getAnniversariesReference().orderByChild("userId").equalTo(userId)
                .addValueEventListener(anniversaryEventListener);
    }

    // 기념일 데이터를 날짜순으로 정렬하는 메서드
    private void sortAnniversariesByDate() {
        Collections.sort(anniversaryDataList, new Comparator<AnniversaryData>() {
            @Override
            public int compare(AnniversaryData o1, AnniversaryData o2) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    Date date1 = sdf.parse(o1.getDate());
                    Date date2 = sdf.parse(o2.getDate());
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    // 기념일 수정 다이얼로그를 표시하는 메서드
    private void showEditAnniversaryDialog(final AnniversaryData anniversary) {
        AddAnniversaryFragment addAnniversaryFragment = AddAnniversaryFragment.newInstance(anniversary, this);
        addAnniversaryFragment.show(getParentFragmentManager(), "AddAnniversaryFragment");
    }

    // 기념일 추가 다이얼로그를 표시하는 메서드
    private void showAddAnniversaryDialog() {
        AddAnniversaryFragment addAnniversaryFragment = AddAnniversaryFragment.newInstance(null, this);
        addAnniversaryFragment.show(getParentFragmentManager(), "AddAnniversaryFragment");
    }

    // 현재 사용자 ID를 가져오는 메서드
    private String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    // 기념일이 업데이트될 때 호출되는 메서드
    @Override
    public void onAnniversaryUpdated() {
        fetchAnniversaries();
    }

    // 기념일 목록 어댑터 클래스
    private class AnniversaryAdapter extends BaseAdapter {
        private List<AnniversaryData> anniversaryList;

        public AnniversaryAdapter(List<AnniversaryData> anniversaryList) {
            this.anniversaryList = anniversaryList;
        }

        @Override
        public int getCount() {
            return anniversaryList.size();
        }

        @Override
        public Object getItem(int position) {
            return anniversaryList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.anniversary_list_item, parent, false);
            }

            // 기념일 정보 설정
            TextView dateTextView = convertView.findViewById(R.id.dateTextView);
            TextView commentTextView = convertView.findViewById(R.id.commentTextView);
            ImageButton editButton = convertView.findViewById(R.id.editButton);
            ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

            AnniversaryData anniversary = anniversaryList.get(position);
            dateTextView.setText(anniversary.getDate());
            commentTextView.setText(anniversary.getComment());

            // 수정 버튼 클릭 리스너 설정
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditAnniversaryDialog(anniversary);
                }
            });

            // 삭제 버튼 클릭 리스너 설정
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteAnniversary(anniversary);
                }
            });

            return convertView;
        }

        // 기념일을 삭제하는 메서드
        private void deleteAnniversary(AnniversaryData anniversary) {
            databaseManager.deleteAnniversary(anniversary.getId(), aVoid -> {
                anniversaryList.remove(anniversary);
                notifyDataSetChanged();
                Toast.makeText(getContext(), "Anniversary deleted successfully", Toast.LENGTH_SHORT).show();
            }, e -> Toast.makeText(getContext(), "Failed to delete anniversary", Toast.LENGTH_SHORT).show());
        }
    }
}
