package com.gcu.anniversary;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Random;

import DTO.RoomData;
import DTO.UserData;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference myRef = database.getReference();
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private UserData myUserData;

    private CircularImageView profileImageView;
    private TextView profileNameTextView;
    private Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        profileImageView = findViewById(R.id.profileCircularImageView);
        profileNameTextView = findViewById(R.id.profileNameTextView);
        logoutBtn = findViewById(R.id.logoutButton);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                mAuth.signOut();
                startActivity(intent);
                finish();
            }
        });

    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        String userUID = currentUser.getUid();
        DatabaseReference userRef = myRef.child("users").child(userUID);

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

    // [END on_start_check_user]

    private void initializeUserData() {
        String userUID = currentUser.getUid();
        String displayName = currentUser.getDisplayName();
        String photoUrl = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;
        myUserData = new UserData(userUID, displayName, photoUrl);

        DatabaseReference userRef = myRef.child("users").child(userUID);
        userRef.setValue(myUserData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "유저 정보 초기화를 성공하였습니다!");
                        } else {
                            Log.w(TAG, "유저 정보 초기화를 실패하였습니다: ", task.getException());
                        }
                    }
                });
    }

    private void updateUserProfile(UserData userData) {
        myUserData = userData;

        Log.d(TAG, "유저 이름 갱신 성공, name:" + myUserData.getNickName());
        profileNameTextView.setText(myUserData.getNickName());

        String photoUrl = myUserData.getImageURL();
        if (photoUrl != null) {
            Log.d(TAG, "유저 프로필 이미지 갱신 성공, 이미지 URL:" + photoUrl);
            Glide.with(this).load(photoUrl).into(profileImageView);
        } else {
            Log.d(TAG, "유저 프로필 이미지 갱신 실패, 이미지 URL: NULL");
        }
    }

    private boolean roomExists(DatabaseReference roomsRef, int roomNumber) {
        final boolean[] exists = {false};
        roomsRef.child(Integer.toString(roomNumber)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                exists[0] = dataSnapshot.exists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // ...
            }
        });
        return exists[0];
    }
}