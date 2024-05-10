package com.gcu.anniversary;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends AppCompatActivity {
    Fragment fragment[];
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 처음 화면
        getSupportFragmentManager().beginTransaction().add(R.id.content_layout, new HomeFragment()).commit();


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                
                if (id == R.id.navigation_bar_item_1) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, new HomeFragment()).commit();
                } else if (id == R.id.navigation_bar_item_2) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, new FriendFragment()).commit();
                } else if (id == R.id.navigation_bar_item_3) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, new CalenderFragment()).commit();
                } else if (id == R.id.navigation_bar_item_4) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, new AlertFragment()).commit();
                }

                return true;
            }
        });

    }
}