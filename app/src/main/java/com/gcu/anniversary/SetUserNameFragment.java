package com.gcu.anniversary;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SetUserNameFragment extends Fragment {
    private static final String ARG_IS_UPDATE = "is_update"; // 업데이트 여부를 나타내는 상수

    private EditText userNameEditText; // 사용자 이름 입력 필드
    private Button saveButton; // 저장 버튼
    private DatabaseReference userRef; // Firebase 데이터베이스 참조
    private boolean isUpdate; // 업데이트 여부

    // 새로운 인스턴스를 생성하는 메서드
    public static SetUserNameFragment newInstance(boolean isUpdate) {
        SetUserNameFragment fragment = new SetUserNameFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_UPDATE, isUpdate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isUpdate = getArguments().getBoolean(ARG_IS_UPDATE); // 인자에서 업데이트 여부를 가져옴
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_username, container, false); // 레이아웃 인플레이트

        userNameEditText = view.findViewById(R.id.userNameEditText); // 사용자 이름 입력 필드 초기화
        saveButton = view.findViewById(R.id.saveButton); // 저장 버튼 초기화

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 현재 사용자 ID 가져오기
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId); // 사용자 데이터베이스 참조 초기화

        if (isUpdate) {
            fetchCurrentUserName(); // 업데이트 모드일 경우 현재 사용자 이름 가져오기
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameEditText.getText().toString().trim();
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(getContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
                } else {
                    checkUserNameExists(userName); // 사용자 이름 존재 여부 확인
                }
            }
        });

        return view; // 뷰 반환
    }

    // 현재 사용자 이름을 가져오는 메서드
    private void fetchCurrentUserName() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String currentUserName = dataSnapshot.child("userName").getValue(String.class);
                if (currentUserName != null) {
                    userNameEditText.setText(currentUserName); // 현재 사용자 이름 설정
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch current username", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 사용자 이름이 존재하는지 확인하는 메서드
    private void checkUserNameExists(String userName) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("userName").equalTo(userName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(getContext(), "Username already exists. Please choose another name.", Toast.LENGTH_SHORT).show();
                        } else {
                            saveUserName(userName); // 사용자 이름 저장
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Failed to check username", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 사용자 이름을 저장하는 메서드
    private void saveUserName(String userName) {
        userRef.child("userName").setValue(userName)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Username saved successfully", Toast.LENGTH_SHORT).show();
                        navigateToHomeFragment(); // 저장 성공 시 홈 프래그먼트로 이동
                    } else {
                        Toast.makeText(getContext(), "Failed to save username", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 홈 프래그먼트로 이동하는 메서드
    private void navigateToHomeFragment() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new HomeFragment()); // 홈 프래그먼트로 교체
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
