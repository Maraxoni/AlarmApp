package com.example.alarmapp.timer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.alarmapp.R;
import com.example.alarmapp.alarm.AlarmReceiver;

public class TimerFragment extends Fragment {

    private NumberPicker hourPicker, minutePicker, secondPicker;
    private Button startCountdownButton, cancelCountdownButton;
    private TextView countdownTextView;

    private Handler handler;
    private Runnable countdownRunnable;
    private long totalMilliseconds;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timer_tab_fragment, container, false);

        // Inicjalizacja widoków
        hourPicker = view.findViewById(R.id.hourPicker);
        minutePicker = view.findViewById(R.id.minutePicker);
        secondPicker = view.findViewById(R.id.secondPicker);
        startCountdownButton = view.findViewById(R.id.startCountdownButton);
        cancelCountdownButton = view.findViewById(R.id.cancelCountdownButton);
        countdownTextView = view.findViewById(R.id.countdownTextView);

        // Ustawienie zakresu dla NumberPickerów
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);

        // Inicjalizacja Handlera i Runnable
        handler = new Handler(Looper.getMainLooper());
        countdownRunnable = new Runnable() {
            @Override
            public void run() {
                if (totalMilliseconds <= 0) {
                    handleCountdownFinish();
                } else {
                    handler.postDelayed(this, 1000);
                }
                updateCountdown();
            }
        };

        // Ustawienie listenerów dla przycisków
        startCountdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCountdown();
            }
        });

        cancelCountdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelCountdown();
            }
        });

        return view;
    }

    private void startCountdown() {
        // Implementacja rozpoczęcia odliczania
        int hours = hourPicker.getValue();
        int minutes = minutePicker.getValue();
        int seconds = secondPicker.getValue();

        totalMilliseconds = (hours * 3600 + minutes * 60 + seconds) * 1000;

        if (totalMilliseconds <= 0) {
            Toast.makeText(getContext(), "Wybierz poprawny czas odliczania.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Uruchomienie odliczania przy użyciu Runnable i Handlera
        handler.post(countdownRunnable);

        // Wyświetlenie komunikatu o rozpoczęciu odliczania
        String message = String.format("Odliczanie rozpoczęte na %d godzin, %d minut, %d sekund.", hours, minutes, seconds);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

        // Zablokuj przycisk "Rozpocznij odliczanie"
        startCountdownButton.setEnabled(false);
    }

    private void updateCountdown() {
        // Aktualizacja TextView z liczbą pozostałych sekund
        long totalSeconds = totalMilliseconds / 1000;
        long displayedHours = totalSeconds / 3600;
        long displayedMinutes = (totalSeconds % 3600) / 60;
        long displayedSeconds = totalSeconds % 60;

        String countdownText = String.format("%02d:%02d:%02d", displayedHours, displayedMinutes, displayedSeconds);
        countdownTextView.setText(countdownText);

        if (totalMilliseconds > 0) {
            // Zmniejsz czas pozostały do zakończenia odliczania
            totalMilliseconds -= 1000;
        } else {
            // Obsługa zakończenia odliczania
            handleCountdownFinish();
        }
    }

    private void handleCountdownFinish() {
        // Obsługa zakończenia odliczania
        countdownTextView.setText("00:00:00");
        Toast.makeText(getContext(), "Odliczanie zakończone!", Toast.LENGTH_SHORT).show();

        // Odblokuj przycisk "Rozpocznij odliczanie"
        startCountdownButton.setEnabled(true);

        // Dodaj odpalanie budzika
        startAlarm();
    }
    private void startAlarm() {
        // Tworzenie Intent dla AlarmReceiver
        Intent alarmIntent = new Intent(getContext(), AlarmReceiver.class);

        // Tworzenie PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Ustawienie alarmu na określony czas po zakończeniu odliczania
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            long triggerTime = System.currentTimeMillis() + 5000; // Przykładowy czas uruchomienia alarmu po 5 sekundach (możesz dostosować)
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    private void cancelCountdown() {
        // Implementacja anulowania odliczania
        handler.removeCallbacks(countdownRunnable);
        countdownTextView.setText("00:00:00");
        Toast.makeText(getContext(), "Odliczanie anulowane!", Toast.LENGTH_SHORT).show();

        // Odblokuj przycisk "Rozpocznij odliczanie"
        startCountdownButton.setEnabled(true);
    }
}