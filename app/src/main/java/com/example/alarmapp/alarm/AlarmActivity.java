package com.example.alarmapp.alarm;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alarmapp.MainActivity;
import com.example.alarmapp.R;
import com.example.alarmapp.database.AlarmDatabase;
import com.example.alarmapp.database.AlarmEntity;

public class AlarmActivity extends AppCompatActivity implements SensorEventListener {

    private static Ringtone ringtone;
    private static Uri alarmSoundUri;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isAlarmUnlocked = false;

    private TextView accelerometerValuesTextView;
    private TextView alarmDescTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Intent intent = getIntent();
        int alarmId = intent.getIntExtra("ALARM_ID", -1);

        new GetAlarmAsyncTask().execute(alarmId);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }

        accelerometerValuesTextView = findViewById(R.id.accelerometerValuesTextView);
        alarmDescTextView = findViewById(R.id.alarmDescTextView);

        configureCancelButton();
    }

    private void playAlarmSound(Context context) {
        alarmSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.trixie_es);
        ringtone = RingtoneManager.getRingtone(context, alarmSoundUri);
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
            if (alarmEntity != null) {
                playAlarmSound(AlarmActivity.this);
            }
        }
    }

    private void configureCancelButton() {
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAlarmUnlocked) {
                    stopAlarmSound();
                    finish();
                } else {
                    // Dodatkowe działania lub komunikat, jeśli alarm nie jest odblokowany
                    // Możesz dodać kod obsługujący ten przypadek
                }
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        if (Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]) > 20) {
            isAlarmUnlocked = true;
            updateAlarmDescText();
        }

        accelerometerValuesTextView.setText("Acceleration: X = " + values[0] + ", Y = " + values[1] + ", Z = " + values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Nie jest potrzebne dla tego przykładu
    }

    private void updateAlarmDescText() {
        if (isAlarmUnlocked) {
            alarmDescTextView.setText("Alarm Unlocked!");
        } else {
            alarmDescTextView.setText("Make a move with phone to unlock");
        }
    }
}