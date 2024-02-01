package com.example.alarmapp.alarm;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alarmapp.MainActivity;
import com.example.alarmapp.R;
import com.example.alarmapp.database.AlarmDatabase;
import com.example.alarmapp.database.AlarmEntity;

public class AlarmActivity extends AppCompatActivity {

    private static Ringtone ringtone;
    private static Uri alarmSoundUri;  // Zmienna Uri jest teraz statyczna

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // Pobierz informacje o alarmie z Intent
        Intent intent = getIntent();
        int alarmId = intent.getIntExtra("ALARM_ID", -1);

        // Pobierz informacje o alarmie z bazy danych using AsyncTask
        new GetAlarmAsyncTask().execute(alarmId);
    }

    private void playAlarmSound(Context context) {
        // Tutaj możesz zaimplementować odtwarzanie dźwięku budzika
        // Utwórz obiekt Uri z ciągu znaków
        alarmSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.trixie_es);
        ringtone = RingtoneManager.getRingtone(context, alarmSoundUri);

        // Odtwórz dźwięk
        ringtone.play();
    }

    private void stopAlarmSound() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }

    private class GetAlarmAsyncTask extends AsyncTask<Integer, Void, AlarmEntity> {
        @Override
        protected AlarmEntity doInBackground(Integer... params) {
            AlarmDatabase appDatabase = MainActivity.getAlarmDatabase();
            return appDatabase.alarmDao().getAlarmById(params[0]);
        }

        @Override
        protected void onPostExecute(AlarmEntity alarmEntity) {
            // Handle the result on the main thread
            if (alarmEntity != null) {
                // Odtwórz dźwięk budzika po pobraniu informacji z bazy danych
                playAlarmSound(AlarmActivity.this);

                // Dodaj przycisk "Anuluj" do obsługi przerwania dźwięku
                Button cancelButton = findViewById(R.id.cancelButton);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopAlarmSound();
                        finish(); // Zamknij aktywność po anulowaniu
                    }
                });
            }
        }
    }
}