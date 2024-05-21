package com.gcu.anniversary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.gcu.anniversary.firebase.FirebaseDatabaseManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DTO.FriendData;
import DTO.UserData;

public class AddFriendFragment extends DialogFragment {
    private FirebaseDatabaseManager databaseManager;
    private EditText searchEditText;
    private Button searchButton;
    private ListView searchListView;
    private List<String> searchResults;
    private Map<String, String> searchResultsMap; // 사용자 이름과 UID를 매핑하는 Map
    private ArrayAdapter<String> searchAdapter;
    private TextView noResultsTextView;
    private FriendAddedListener friendAddedListener;

    // 생성자: 친구 추가 리스너를 인자로 받음
    public AddFriendFragment(FriendAddedListener friendAddedListener) {
        this.friendAddedListener = friendAddedListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);

        // 뷰 및 데이터베이스 매니저 초기화
        databaseManager = new FirebaseDatabaseManager();
        searchEditText = view.findViewById(R.id.searchEditText);
        searchButton = view.findViewById(R.id.searchButton);
        searchListView = view.findViewById(R.id.searchListView);
        noResultsTextView = view.findViewById(R.id.noResultsTextView);

        searchResults = new ArrayList<>();
        searchResultsMap = new HashMap<>();
        searchAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, searchResults);
        searchListView.setAdapter(searchAdapter);

        // 검색 버튼 클릭 리스너 설정
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFriends();
            }
        });

        // 검색 결과 아이템 클릭 리스너 설정
        searchListView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedUserName = searchResults.get(position);
            String friendUID = searchResultsMap.get(selectedUserName);
            if (friendUID != null) {
                addFriend(friendUID);
            } else {
                Toast.makeText(getContext(), "Invalid user selection", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // 친구 검색 메서드
    private void searchFriends() {
        String userName = searchEditText.getText().toString().trim();
        if (userName.isEmpty()) {
            searchEditText.setError("Enter a username to search");
            return;
        }

        // Firebase에서 사용자 이름으로 검색
        databaseManager.getUsersReference().orderByChild("userName").equalTo(userName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        searchResults.clear();
                        searchResultsMap.clear();
                        if (dataSnapshot.exists()) {
                            noResultsTextView.setVisibility(View.GONE);
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                UserData user = snapshot.getValue(UserData.class);
                                if (user != null) {
                                    searchResults.add(user.getUserName());
                                    searchResultsMap.put(user.getUserName(), user.getUID());
                                }
                            }
                        } else {
                            noResultsTextView.setVisibility(View.VISIBLE);
                        }
                        searchAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // 에러 처리
                    }
                });
    }

    // 친구 추가 메서드
    private void addFriend(String friendUID) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (currentUserId == null) {
            return;
        }

        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("friends");

        // 현재 사용자와 이미 친구 관계인지 확인
        friendsRef.orderByChild("friendId1").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean alreadyFriend = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            FriendData friendData = snapshot.getValue(FriendData.class);
                            if (friendData != null && friendData.getFriendId2().equals(friendUID)) {
                                alreadyFriend = true;
                                break;
                            }
                        }

                        if (alreadyFriend) {
                            Toast.makeText(getContext(), "This user is already your friend", Toast.LENGTH_SHORT).show();
                        } else {
                            String friendListId = friendsRef.push().getKey();
                            if (friendListId == null) {
                                return;
                            }

                            // 새로운 친구 데이터 생성 및 Firebase에 추가
                            FriendData friendData = new FriendData(friendListId, currentUserId, friendUID, false);
                            friendsRef.child(friendListId).setValue(friendData)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Friend added successfully", Toast.LENGTH_SHORT).show();
                                            dismiss();
                                            if (friendAddedListener != null) {
                                                friendAddedListener.onFriendAdded();
                                            }
                                        } else {
                                            Toast.makeText(getContext(), "Failed to add friend", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Failed to check friend list", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 친구 추가 리스너 인터페이스 정의
    public interface FriendAddedListener {
        void onFriendAdded();
    }
}
