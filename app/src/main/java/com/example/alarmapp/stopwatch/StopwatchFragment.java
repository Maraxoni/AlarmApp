package com.example.alarmapp.stopwatch;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.example.alarmapp.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.alarmapp.R;

public class StopwatchFragment extends Fragment {

    private TextView stopwatchTextView;
    private Button startStopButton, resetButton;

    private boolean isRunning = false;
    private long startTime = 0;
    private long elapsedTime = 0;

    private Handler handler;
    private Runnable stopwatchRunnable;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stopwatch_tab_fragment, container, false);

        stopwatchTextView = view.findViewById(R.id.countdownTextView);
        startStopButton = view.findViewById(R.id.button_stopwatch);
        resetButton = view.findViewById(R.id.button_stopwatch_reset);

        handler = new Handler(Looper.getMainLooper());

        stopwatchRunnable = new Runnable() {
            @Override
            public void run() {
                updateStopwatch();
                handler.postDelayed(this, 1000);
            }
        };

        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleStartStop();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetStopwatch();
            }
        });

        return view;
    }

    private void toggleStartStop() {
        if (isRunning) {
            stopStopwatch();
        } else {
            startStopwatch();
        }
    }

    private void startStopwatch() {
        isRunning = true;
        startTime = System.currentTimeMillis() - elapsedTime;
        handler.post(stopwatchRunnable);
        startStopButton.setText("Stop");
    }

    private void stopStopwatch() {
        isRunning = false;
        handler.removeCallbacks(stopwatchRunnable);
        elapsedTime = System.currentTimeMillis() - startTime;
        startStopButton.setText("Start");
    }

    private void resetStopwatch() {
        isRunning = false;
        handler.removeCallbacks(stopwatchRunnable);
        elapsedTime = 0;
        updateStopwatch();
        startStopButton.setText("Start");
    }

    private void updateStopwatch() {
        long currentTime = System.currentTimeMillis();
        long updatedTime = currentTime - startTime + elapsedTime;

        int seconds = (int) (updatedTime / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;

        String timeString = String.format("%02d:%02d:%02d", hours % 24, minutes % 60, seconds % 60);
        stopwatchTextView.setText(timeString);
    }
}