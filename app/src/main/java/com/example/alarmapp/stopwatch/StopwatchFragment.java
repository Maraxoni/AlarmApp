package com.example.alarmapp.stopwatch;

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
    private TextView pauseCountdownTextView; // Add this line
    private Button startStopButton, resetButton;

    private boolean isRunning = false;
    private boolean isPaused = false;
    private long startTime = 0;
    private long elapsedTime = 0;

    private Handler handler;
    private Runnable stopwatchRunnable;
    private Runnable pauseCountdownRunnable; // Add this line

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

        pauseCountdownRunnable = new Runnable() { // Add this block
            @Override
            public void run() {
                updatePauseCountdown();
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
        if (!isRunning) {
            isRunning = true;
            if (isPaused) {
                // Resume from paused time
                startTime = System.currentTimeMillis() - elapsedTime;
                handler.post(pauseCountdownRunnable); // Start pause countdown
            } else {
                // Start from zero
                startTime = System.currentTimeMillis();
            }
            isPaused = false;
            handler.post(stopwatchRunnable);
            startStopButton.setText("Stop");
        }
    }

    private void stopStopwatch() {
        if (isRunning) {
            isRunning = false;
            isPaused = true;
            handler.removeCallbacks(stopwatchRunnable);
            handler.removeCallbacks(pauseCountdownRunnable); // Stop pause countdown
            elapsedTime = System.currentTimeMillis() - startTime;
            startStopButton.setText("Start");
        }
    }

    private void resetStopwatch() {
        isRunning = false;
        isPaused = false;
        handler.removeCallbacks(stopwatchRunnable);
        handler.removeCallbacks(pauseCountdownRunnable); // Stop pause countdown
        elapsedTime = 0;
        updateStopwatch();
        updatePauseCountdown(); // Reset pause countdown
        startStopButton.setText("Start");
    }

    private void updateStopwatch() {
        if (isRunning && !isPaused) {
            long currentTime = System.currentTimeMillis();
            long updatedTime = currentTime - startTime + elapsedTime;

            int seconds = (int) (updatedTime / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;

            String timeString = String.format("%02d:%02d:%02d", hours % 24, minutes % 60, seconds % 60);
            stopwatchTextView.setText(timeString);
        }
    }

    private void updatePauseCountdown() {
        if (isPaused) {
            long pauseTime = System.currentTimeMillis() - startTime;
            int seconds = (int) (pauseTime / 1000);
            int minutes = seconds / 60;

            String pauseCountdown = String.format("Pause: %02d:%02d", minutes % 60, seconds % 60);
            pauseCountdownTextView.setText(pauseCountdown);
        }
    }
}