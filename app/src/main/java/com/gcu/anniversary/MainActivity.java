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
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


// Single Activty Architecture 구조입니다
public class MainActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    Fragment fragment[];
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Fragment를 초기화합니다
        initializeFragments();


        // 하단의 네비게이션 바를 통해서 프레그먼트 전환을 관리합니다
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

    private void initializeFragments() {
        fragment = new Fragment[4];
        fragment[0] = new HomeFragment();
        fragment[1] = new FriendFragment();
        fragment[2] = new CalenderFragment();
        fragment[3] = new AlertFragment();

        fragmentManager.beginTransaction().add(R.id.content_layout, fragment[0]).commit();
    }
}