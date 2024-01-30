package com.example.alarmapp;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.example.alarmapp.alarm.AlarmFragment;
import com.example.alarmapp.database.AlarmDao;
import com.example.alarmapp.database.AlarmDatabase;
import com.example.alarmapp.database.AlarmEntity;
import com.example.alarmapp.settings.SettingsFragment;
import com.example.alarmapp.stopwatch.StopwatchFragment;
import com.example.alarmapp.timer.TimerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static AlarmDatabase alarmDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmDatabase = Room.databaseBuilder(getApplicationContext(),
                AlarmDatabase.class, "alarm-database").build();
        addSampleAlarms();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);

        // Dodaj Fragment A jako domyślny
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, new HomeFragment())
                .commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.action_alarm) {
                    replaceFragment(new AlarmFragment());
                    return true;
                } else if (itemId == R.id.action_timer) {
                    replaceFragment(new TimerFragment());
                    return true;
                } else if (itemId == R.id.action_home) {
                    replaceFragment(new HomeFragment());
                    return true;
                } else if (itemId == R.id.action_stopwatch) {
                    replaceFragment(new StopwatchFragment());
                    return true;
                } else if (itemId == R.id.action_settings) {
                    replaceFragment(new SettingsFragment());
                    return true;
                }

                return false;
            }
        });
    }

    // Metoda do dynamicznej zamiany fragmentów
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void addSampleAlarms() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AlarmDao alarmDao = alarmDatabase.alarmDao();

                // Przykładowe dni tygodnia (poniedziałek i środa)
                List<String> sampleDaysOfWeek = new ArrayList<>();
                sampleDaysOfWeek.add("Monday");
                sampleDaysOfWeek.add("Wednesday");

                // Dodanie kilku przykładowych alarmów
                alarmDao.insert(new AlarmEntity(0, sampleDaysOfWeek, 8, 0)); // Alarm o 8:00 w poniedziałki i środy
                alarmDao.insert(new AlarmEntity(0, sampleDaysOfWeek, 12, 30)); // Alarm o 12:30 w poniedziałki i środy
                // Dodaj więcej alarmów według potrzeb

                // Aktualizacja UI, jeśli jest to potrzebne
                // Możesz użyć metody runOnUiThread() w przypadku, gdy aktualizacja dotyczy interfejsu użytkownika
            }
        }).start();
    }

    public static AlarmDatabase getAlarmDatabase() {
        return alarmDatabase;
    }

}
