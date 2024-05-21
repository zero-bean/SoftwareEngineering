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
import androidx.fragment.app.FragmentTransaction;

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
    private final String TAG = "HomeFragment";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference userRef;
    private UserData myUserData;
    private ImageView profileImageView;
    private TextView profileNameTextView;
    private Button changeProfileButton;
    private Button logoutBtn;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(currentUser.getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        profileImageView = rootView.findViewById(R.id.myProfileImage);
        profileNameTextView = rootView.findViewById(R.id.profileNameTextView);
        changeProfileButton = rootView.findViewById(R.id.changeProfileButton);
        logoutBtn = rootView.findViewById(R.id.logoutButton);

        changeProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSetUserNameFragment();
            }
        });

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
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d(TAG, "FIREBASE 유저 데이터 갱신 시작");
                        UserData userData = dataSnapshot.getValue(UserData.class);
                        if (userData != null && userData.getUserName() != null) {
                            updateUserProfile(userData);
                        } else {
                            navigateToSetUserNameFragment();
                        }
                    } else {
                        Log.d(TAG, "FIREBASE 유저 데이터 초기화 시작");
                        initializeUserData();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "FIREBASE 유저 데이터 갱신 실패: ", databaseError.toException());
                }
            });
        }
    }

    private void logout() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        mAuth.signOut();
        startActivity(intent);
        requireActivity().finish();
    }

    private void initializeUserData() {
        String userUID = currentUser.getUid();
        String displayName = currentUser.getDisplayName();
        String photoUrl = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;

        UserData tmp = new UserData(userUID, displayName, photoUrl, null);
        userRef.setValue(tmp);
        navigateToSetUserNameFragment();
    }

    private void updateUserProfile(UserData userData) {
        if (userData != null) {
            Log.d(TAG, "유저 이름 갱신 성공, name:" + userData.getUserName());
            profileNameTextView.setText(userData.getUserName());

            String photoUrl = userData.getImageURL();
            Glide.with(requireContext()).load(photoUrl).into(profileImageView);
        }
    }

    private void navigateToSetUserNameFragment() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        SetUserNameFragment setUserNameFragment = SetUserNameFragment.newInstance(true);
        transaction.replace(R.id.fragment_container, setUserNameFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
