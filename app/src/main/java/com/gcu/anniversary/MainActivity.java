package com.gcu.anniversary;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import DTO.UserData;

public class MainActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    Fragment fragment[];
    BottomNavigationView bottomNavigationView;
    FirebaseUser currentUser;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserData userData = dataSnapshot.getValue(UserData.class);
                    if (userData != null && (userData.getUserName() == null || userData.getUserName().isEmpty())) {
                        // If username is not set, navigate to SetUserNameFragment
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SetUserNameFragment()).commit();
                    } else {
                        // If username is set, proceed with normal flow
                        initializeFragments();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        } else {
            // Handle case where user is not authenticated
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.navigation_bar_item_1) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                } else if (id == R.id.navigation_bar_item_2) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FriendFragment()).commit();
                } else if (id == R.id.navigation_bar_item_3) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CalendarFragment()).commit();
                } else if (id == R.id.navigation_bar_item_4) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AlertFragment()).commit();
                }
                return true;
            }
        });
    }

    private void initializeFragments() {
        fragmentManager = getSupportFragmentManager();
        fragment = new Fragment[4];
        fragment[0] = new HomeFragment();
        fragment[1] = new FriendFragment();
        fragment[2] = new CalendarFragment();
        fragment[3] = new AlertFragment();

        fragmentManager.beginTransaction().add(R.id.fragment_container, fragment[0]).commit();
    }
}
