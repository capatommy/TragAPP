package com.example.tragapp.tragapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.tragapp.R;
import com.example.tragapp.tragapp.fragments.FragmentForecast;
import com.example.tragapp.tragapp.fragments.FragmentForecastNew;
import com.example.tragapp.tragapp.fragments.FragmentInfos;
import com.example.tragapp.tragapp.fragments.FragmentProfilo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private FragmentProfilo fragmentProfilo;
    private FragmentForecastNew fragmentForecast;
    private FragmentInfos fragmentInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);


        fragmentProfilo = new FragmentProfilo();
        fragmentForecast = new FragmentForecastNew();
        fragmentInfos = new FragmentInfos();

        bottomNav = findViewById(R.id.bottomnav);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentProfilo).commit();
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.menu_forecast:
                        selectedFragment = fragmentForecast;
                        break;
                    case R.id.menu_profile:
                        selectedFragment = fragmentProfilo;
                        break;
                    case R.id.menu_info:
                        selectedFragment = fragmentInfos;
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }
                return true;
            }
        });
    }
}
