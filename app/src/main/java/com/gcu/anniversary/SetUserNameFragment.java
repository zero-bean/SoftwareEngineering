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
    private static final String ARG_IS_UPDATE = "is_update";

    private EditText userNameEditText;
    private Button saveButton;
    private DatabaseReference userRef;
    private boolean isUpdate;

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
            isUpdate = getArguments().getBoolean(ARG_IS_UPDATE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_username, container, false);

        userNameEditText = view.findViewById(R.id.userNameEditText);
        saveButton = view.findViewById(R.id.saveButton);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        if (isUpdate) {
            fetchCurrentUserName();
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameEditText.getText().toString().trim();
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(getContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
                } else {
                    checkUserNameExists(userName);
                }
            }
        });

        return view;
    }

    private void fetchCurrentUserName() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String currentUserName = dataSnapshot.child("userName").getValue(String.class);
                if (currentUserName != null) {
                    userNameEditText.setText(currentUserName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch current username", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserNameExists(String userName) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("userName").equalTo(userName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(getContext(), "Username already exists. Please choose another name.", Toast.LENGTH_SHORT).show();
                        } else {
                            saveUserName(userName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Failed to check username", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserName(String userName) {
        userRef.child("userName").setValue(userName)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Username saved successfully", Toast.LENGTH_SHORT).show();
                        navigateToHomeFragment();
                    } else {
                        Toast.makeText(getContext(), "Failed to save username", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToHomeFragment() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new HomeFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
