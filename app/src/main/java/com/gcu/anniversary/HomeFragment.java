package com.gcu.anniversary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import DTO.UserData;


public class HomeFragment extends Fragment {
    // 디버깅을 위한 로그용 태그
    private final String TAG ="HomeFragment";
    // 파이어베이스 인증
    private FirebaseAuth mAuth;
    // 현재 로그인한 구글 계정 사용자를 의미합니다
    private FirebaseUser currentUser;
    // 파이어베이스의 특정 데이터를 수정하기 위해서 필요합니다
    private DatabaseReference userRef;
    private UserData myUserData;
    private ImageView profileImageView;
    private TextView profileNameTextView;
    private Button logoutBtn;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 인증 서비스를 사용하기 위해서 인스턴스를 생성합니다.
        mAuth = FirebaseAuth.getInstance();
        // 현재 로그인한 사용자의 계정 정보를 가져옵니다.
        currentUser = mAuth.getCurrentUser();
        // 파이어베이스의 users 경로에 있는 본인의 데이터 경로에 접근합니다.
        userRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(currentUser.getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        profileImageView = rootView.findViewById(R.id.myProfileImage);
        profileNameTextView = rootView.findViewById(R.id.profileNameTextView);
        logoutBtn = rootView.findViewById(R.id.logoutButton);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (currentUser != null) {
            // 현재 사용자의 정보를 일회성 리스너를 통해서 가져옵니다.
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // 사용자의 정보가 존재한다면
                    if (dataSnapshot.exists()) {
                        Log.d(TAG, "FIREBASE 유저 데이터 갱신 시작");
                        // 파이어베이스에 저장된 사용자의 정보를 가져옵니다.
                        UserData userData = dataSnapshot.getValue(UserData.class);
                        // 사용자의 정보를 화면에 보여줍니다.
                        updateUserProfile(userData);
                    }
                    // 사용자의 정보가 존재하지 않는다면
                    else {
                        Log.d(TAG, "FIREBASE 유저 데이터 초기화 시작");
                        // 파이어베이스에 사용자 정보를 등록합니다.
                        initializeUserData();
                    }
                }

                // 특정 원인으로 인하여 데이터를 가져오는 것을 실패한다면, 실패했다는 로그를 출력합니다.
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "FIREBASE 유저 데이터 갱신 실패: ", databaseError.toException());
                }
            });
        }
    }

    // 로그아웃 버튼을 누르면 로그아웃합니다.
    private void logout() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        mAuth.signOut();
        startActivity(intent);
        requireActivity().finish();
    }

    // 신규 사용자의 정보를 파이어베이스에 등록하고 프로필 화면에 정보를 전송합니다.
    private void initializeUserData() {
        // 구글 계정에 저장된 유저의 정보를 가져와 초기화합니다.
        String userUID = currentUser.getUid();
        String displayName = currentUser.getDisplayName();
        String photoUrl = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;

        UserData tmp = new UserData(userUID, displayName, photoUrl);
        userRef.setValue(tmp);
        updateUserProfile(tmp);
    }

    private void updateUserProfile(UserData userData) {
        if (userData != null) {
            Log.d(TAG, "유저 이름 갱신 성공, name:" + userData.getNickName());
            profileNameTextView.setText(userData.getNickName());

            String photoUrl = userData.getImageURL();
            Glide.with(requireContext()).load(photoUrl).into(profileImageView);
        }
    }
}