package com.example.alarmapp.alarm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.alarmapp.MainActivity;
import com.example.alarmapp.R;
import com.example.alarmapp.database.AlarmEntity;

import java.util.ArrayList;
import java.util.List;

public class AddEditAlarmActivity extends AppCompatActivity {

    public static final String EXTRA_ALARM_ID = "1";
    private EditText labelEditText;
    private TimePicker timePicker;
    private Button saveButton;
    private AlarmEntity existingAlarm;

    // CheckBoxes for each day of the week
    private CheckBox mondayCheckBox, tuesdayCheckBox, wednesdayCheckBox,
            thursdayCheckBox, fridayCheckBox, saturdayCheckBox, sundayCheckBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_alarm);

        labelEditText = findViewById(R.id.editTextLabel);
        timePicker = findViewById(R.id.timePicker);
        saveButton = findViewById(R.id.buttonSave);

        // Initialize CheckBoxes for each day of the week
        mondayCheckBox = findViewById(R.id.mondayCheckBox);
        tuesdayCheckBox = findViewById(R.id.tuesdayCheckBox);
        wednesdayCheckBox = findViewById(R.id.wednesdayCheckBox);
        thursdayCheckBox = findViewById(R.id.thursdayCheckBox);
        fridayCheckBox = findViewById(R.id.fridayCheckBox);
        saturdayCheckBox = findViewById(R.id.saturdayCheckBox);
        sundayCheckBox = findViewById(R.id.sundayCheckBox);

        if (getIntent().hasExtra(EXTRA_ALARM_ID)) {
            int alarmId = getIntent().getIntExtra(EXTRA_ALARM_ID, -1);
            new LoadAlarmTask(this).execute(alarmId);
        } else {
            setTitle("Add Alarm");
        }

        saveButton.setOnClickListener(v -> saveAlarm());
    }

    private void populateFields(AlarmEntity alarm) {
        timePicker.setHour(alarm.getHour());
        timePicker.setMinute(alarm.getMinute());

        // Check the corresponding CheckBoxes for each selected day
        List<String> daysOfWeek = alarm.getDaysOfWeek();
        mondayCheckBox.setChecked(daysOfWeek.contains("Monday"));
        tuesdayCheckBox.setChecked(daysOfWeek.contains("Tuesday"));
        wednesdayCheckBox.setChecked(daysOfWeek.contains("Wednesday"));
        thursdayCheckBox.setChecked(daysOfWeek.contains("Thursday"));
        fridayCheckBox.setChecked(daysOfWeek.contains("Friday"));
        saturdayCheckBox.setChecked(daysOfWeek.contains("Saturday"));
        sundayCheckBox.setChecked(daysOfWeek.contains("Sunday"));
    }

    private void saveAlarm() {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        List<String> daysOfWeek = new ArrayList<>();

        // Check which days are selected and add them to the list
        if (mondayCheckBox.isChecked()) daysOfWeek.add("Monday");
        if (tuesdayCheckBox.isChecked()) daysOfWeek.add("Tuesday");
        if (wednesdayCheckBox.isChecked()) daysOfWeek.add("Wednesday");
        if (thursdayCheckBox.isChecked()) daysOfWeek.add("Thursday");
        if (fridayCheckBox.isChecked()) daysOfWeek.add("Friday");
        if (saturdayCheckBox.isChecked()) daysOfWeek.add("Saturday");
        if (sundayCheckBox.isChecked()) daysOfWeek.add("Sunday");

        if (existingAlarm != null) {
            existingAlarm.setHour(hour);
            existingAlarm.setMinute(minute);
            existingAlarm.setDaysOfWeek(daysOfWeek);  // Update days of the week
            new UpdateAlarmTask().execute(existingAlarm);
        } else {
            AlarmEntity newAlarm = new AlarmEntity(0, daysOfWeek, hour, minute, true);
            new InsertAlarmTask().execute(newAlarm);
        }

        setResult(RESULT_OK);
        finish();
    }

    private static class InsertAlarmTask extends AsyncTask<AlarmEntity, Void, Void> {
        @Override
        protected Void doInBackground(AlarmEntity... alarms) {
            MainActivity.getAlarmDatabase().alarmDao().insert(alarms[0]);
            return null;
        }
    }

    private static class UpdateAlarmTask extends AsyncTask<AlarmEntity, Void, Void> {
        @Override
        protected Void doInBackground(AlarmEntity... alarms) {
            MainActivity.getAlarmDatabase().alarmDao().updateAlarm(alarms[0]);
            return null;
        }
    }

    private static class LoadAlarmTask extends AsyncTask<Integer, Void, AlarmEntity> {
        private AddEditAlarmActivity activity;

        public LoadAlarmTask(AddEditAlarmActivity activity) {
            this.activity = activity;
        }

        @Override
        protected AlarmEntity doInBackground(Integer... params) {
            if (params.length > 0) {
                int alarmId = params[0];
                return MainActivity.getAlarmDatabase().alarmDao().getAlarmById(alarmId);
            }
            return null;
        }

        @Override
        protected void onPostExecute(AlarmEntity alarm) {
            if (alarm != null) {
                activity.existingAlarm = alarm;
                activity.runOnUiThread(() -> {
                    activity.setTitle("Edit Alarm");
                    activity.populateFields(activity.existingAlarm);
                });
            }
        }
    }
}