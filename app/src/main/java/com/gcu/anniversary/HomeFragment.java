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
import androidx.core.view.ViewCompat;
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
    private final String TAG ="HomeFragment";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
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
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        profileImageView = rootView.findViewById(R.id.myProfileImage);
        profileNameTextView = rootView.findViewById(R.id.profileNameTextView);
        logoutBtn = rootView.findViewById(R.id.logoutButton);

        // Initialize profile image, name, and logout button click listener
        initializeUI();

        // Apply window insets to the root view
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            int systemBarsTop = insets.getSystemWindowInsetTop();
            int systemBarsBottom = insets.getSystemWindowInsetBottom();
            v.setPadding(insets.getSystemWindowInsetLeft(), systemBarsTop,
                    insets.getSystemWindowInsetRight(), systemBarsBottom);
            return insets.consumeSystemWindowInsets();
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userUID = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userUID);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d(TAG, "FIREBASE 유저 데이터 갱신 시작");
                        UserData userData = dataSnapshot.getValue(UserData.class);
                        updateUserProfile(userData);
                    } else {
                        Log.d(TAG, "FIREBASE 유저 데이터 초기화 시작");
                        initializeUserData();
                        updateUserProfile(myUserData);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "FIREBASE 유저 데이터 갱신 실패: ", databaseError.toException());
                }
            });
        }
    }

    private void initializeUI() {
        // Set logout button click listener
        logoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            mAuth.signOut();
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void initializeUserData() {
        if (currentUser != null) {
            String userUID = currentUser.getUid();
            String displayName = currentUser.getDisplayName();
            String photoUrl = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;
            myUserData = new UserData(userUID, displayName, photoUrl);

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userUID);
            userRef.setValue(myUserData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "유저 정보 초기화를 성공하였습니다!");
                        } else {
                            Log.w(TAG, "유저 정보 초기화를 실패하였습니다: ", task.getException());
                        }
                    });
        }
    }

    private void updateUserProfile(UserData userData) {
        if (userData != null) {
            Log.d(TAG, "유저 이름 갱신 성공, name:" + userData.getNickName());
            profileNameTextView.setText(userData.getNickName());

            String photoUrl = userData.getImageURL();
            if (photoUrl != null) {
                Log.d(TAG, "유저 프로필 이미지 갱신 성공, 이미지 URL:" + photoUrl);
                Glide.with(requireContext()).load(photoUrl).into(profileImageView);
            } else {
                Log.d(TAG, "유저 프로필 이미지 갱신 실패, 이미지 URL: NULL");
            }
        }
    }
}