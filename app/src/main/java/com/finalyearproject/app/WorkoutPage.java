package com.finalyearproject.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class WorkoutPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_page);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WorkoutFragment()).commit();

    }

    private NavigationBarView.OnItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;
        switch (item.getItemId()) {
            case R.id.workoutFragment:
                selectedFragment = new WorkoutFragment();
                break;
            case R.id.historyFragment:
                selectedFragment = new HistoryFragment();
                break;
            case R.id.exercisesFragment:
                selectedFragment = new ExercisesFragment();
                break;
            case R.id.settingsFragment:
                selectedFragment = new SettingsFragment();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        return true;
    };
}