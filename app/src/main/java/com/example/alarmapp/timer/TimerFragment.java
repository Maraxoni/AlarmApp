package com.example.alarmapp.timer;

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

        // Uruchomienie odliczania przy użyciu Runnable i Handlera
        handler.post(countdownRunnable);

        // Wyświetlenie komunikatu o rozpoczęciu odliczania
        String message = String.format("Odliczanie rozpoczęte na %d godzin, %d minut, %d sekund.", hours, minutes, seconds);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
    }

    private void cancelCountdown() {
        // Implementacja anulowania odliczania
        handler.removeCallbacks(countdownRunnable);
        countdownTextView.setText("00:00:00");  // Zresetowanie TextView
        Toast.makeText(getContext(), "Odliczanie anulowane!", Toast.LENGTH_SHORT).show();
    }
}