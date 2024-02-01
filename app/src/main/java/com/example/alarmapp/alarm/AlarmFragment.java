package com.example.alarmapp.alarm;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmapp.MainActivity;
import com.example.alarmapp.R;
import com.example.alarmapp.alarm.AddEditAlarmActivity;
import com.example.alarmapp.alarm.AlarmAdapter;
import com.example.alarmapp.database.AlarmDao;
import com.example.alarmapp.database.AlarmDatabase;
import com.example.alarmapp.database.AlarmEntity;

import java.util.Calendar;
import java.util.List;

public class AlarmFragment extends Fragment {

    private RecyclerView recyclerView;
    private static AlarmAdapter alarmAdapter;
    private Button addAlarmButton;
    private static final int ADD_EDIT_ALARM_REQUEST_CODE = 1;
    private static final String TAG = "AlarmFragment";

    private static final long CHECK_INTERVAL = 60 * 1000; // 1 minute
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable checkAlarmsRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarm_tab_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        addAlarmButton = view.findViewById(R.id.addAlarmButton);
        Button simulateAlarmButton = view.findViewById(R.id.simulateAlarmButton); // Dodano przycisk simulateAlarmButton

        initRecyclerView();

        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddAlarmActivity();
            }
        });

        // Dodano obsługę kliknięcia dla simulateAlarmButton
        simulateAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tutaj wywołaj funkcję lub metodę, która otwiera AlarmActivity
                openAlarmActivityForSimulation();
            }
        });

        loadAlarms();

        // Initialize the Runnable to check alarms every minute
        checkAlarmsRunnable = new Runnable() {
            @Override
            public void run() {
                checkAlarms();
                handler.postDelayed(this, CHECK_INTERVAL);
            }
        };

        // Start checking alarms
        handler.postDelayed(checkAlarmsRunnable, CHECK_INTERVAL);

        return view;
    }

    // Dodano nową metodę do obsługi kliknięcia simulateAlarmButton
    private void openAlarmActivityForSimulation() {
        // Tutaj utwórz Intent i uruchom AlarmActivity tak, jak w przypadku innych aktywności
        Intent intent = new Intent(getContext(), AlarmActivity.class);
        // Dodaj dodatkowe dane, jeśli są potrzebne
        // intent.putExtra("KEY", value);
        startActivity(intent);
    }
    private void checkAlarms() {
        // Check alarms in the background to avoid blocking the main thread
        new CheckAlarmAsyncTask(getContext()).execute();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        alarmAdapter = new AlarmAdapter(getContext(), new AlarmAdapter.OnAlarmClickListener() {
            @Override
            public void onEditClick(AlarmEntity alarm) {
                openEditAlarmActivity(alarm);
            }

            @Override
            public void onDeleteClick(AlarmEntity alarm) {
                deleteAlarm(alarm);
            }

            @Override
            public void onSwitchToggle(AlarmEntity alarm, boolean isActive) {
                updateAlarmStatus(alarm, isActive);
            }
        });

        recyclerView.setAdapter(alarmAdapter);
    }

    private void openAddAlarmActivity() {
        Intent intent = new Intent(getContext(), AddEditAlarmActivity.class);
        startActivityForResult(intent, ADD_EDIT_ALARM_REQUEST_CODE);
    }

    private void openEditAlarmActivity(AlarmEntity alarm) {
        Intent intent = new Intent(getContext(), AddEditAlarmActivity.class);
        intent.putExtra(AddEditAlarmActivity.EXTRA_ALARM_ID, alarm.getId());
        startActivityForResult(intent, ADD_EDIT_ALARM_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_EDIT_ALARM_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            // If the result is OK, reload the list of alarms
            loadAlarms();
        }
    }

    private void loadAlarms() {
        // Load alarms in the background to avoid blocking the main thread
        new LoadAlarmsAsyncTask(getContext()).execute();
    }

    private static class LoadAlarmsAsyncTask extends AsyncTask<Void, Void, List<AlarmEntity>> {
        private Context context;

        LoadAlarmsAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected List<AlarmEntity> doInBackground(Void... voids) {
            // Load alarms from the database in the background
            AlarmDatabase appDatabase = MainActivity.getAlarmDatabase();
            AlarmDao alarmDao = appDatabase.alarmDao();
            return alarmDao.getAllAlarms();
        }

        @Override
        protected void onPostExecute(List<AlarmEntity> alarms) {
            // Update the UI with the loaded alarms
            alarmAdapter.setAlarms(alarms);
            alarmAdapter.notifyDataSetChanged();

            // Added alarm checking
            for (AlarmEntity alarm : alarms) {
                new CheckAlarmAsyncTask(context).execute(alarm);
            }
        }

        private static boolean shouldActivateAlarm(AlarmEntity alarm, Calendar now) {
            Log.d(TAG, "Checking alarm - ID: " + alarm.getId() + ", Time: " + alarm.getHour() + ":" + alarm.getMinute() +
                    ", Days: " + alarm.getDaysOfWeek() + ", IsActive: " + alarm.isActive());

            boolean isCorrectHourAndMinute = now.get(Calendar.HOUR_OF_DAY) == alarm.getHour() &&
                    now.get(Calendar.MINUTE) == alarm.getMinute();

            boolean isCorrectDayOfWeek = isDayOfWeekMatch(alarm.getDaysOfWeek(), now.get(Calendar.DAY_OF_WEEK));

            Log.d(TAG, "isCorrectHourAndMinute: " + isCorrectHourAndMinute + ", isCorrectDayOfWeek: " + isCorrectDayOfWeek);

            return alarm.isActive() && isCorrectHourAndMinute && isCorrectDayOfWeek;
        }

        private static boolean isCorrectTime(Calendar now, int alarmHour, int alarmMinute, List<String> alarmDaysOfWeek) {
            // Check if the current time matches the alarm settings (hour, minute, day of the week)
            boolean isCorrectHourAndMinute = now.get(Calendar.HOUR_OF_DAY) == alarmHour && now.get(Calendar.MINUTE) == alarmMinute;
            boolean isCorrectDayOfWeek = isDayOfWeekMatch(alarmDaysOfWeek, now.get(Calendar.DAY_OF_WEEK));

            return isCorrectHourAndMinute && isCorrectDayOfWeek;
        }

        private static boolean isDayOfWeekMatch(List<String> alarmDaysOfWeek, int currentDayOfWeek) {
            // Conversion from string to integer for comparison
            String[] daysOfWeekArray = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
            int alarmDayIndex = daysOfWeekArray.length - 1; // Default to Saturday if not found
            for (int i = 0; i < daysOfWeekArray.length; i++) {
                if (alarmDaysOfWeek.contains(daysOfWeekArray[i])) {
                    alarmDayIndex = i;
                    break;
                }
            }

            return alarmDayIndex == currentDayOfWeek - 1;
        }
    }

    private void deleteAlarm(AlarmEntity alarm) {
        // Delete alarm in the background to avoid blocking the main thread
        new DeleteAlarmAsyncTask().execute(alarm);
    }

    private static class DeleteAlarmAsyncTask extends AsyncTask<AlarmEntity, Void, List<AlarmEntity>> {
        @Override
        protected List<AlarmEntity> doInBackground(AlarmEntity... alarms) {
            // Delete alarm from the database in the background
            AlarmDatabase appDatabase = MainActivity.getAlarmDatabase();
            AlarmDao alarmDao = appDatabase.alarmDao();
            alarmDao.deleteAlarm(alarms[0]);
            return alarmDao.getAllAlarms();
        }

        @Override
        protected void onPostExecute(List<AlarmEntity> alarms) {
            // Update the UI with the remaining alarms
            alarmAdapter.setAlarms(alarms);
            alarmAdapter.notifyDataSetChanged();
        }
    }

    private void updateAlarmStatus(AlarmEntity alarm, boolean isActive) {
        // Update alarm status in the background to avoid blocking the main thread
        alarm.setActive(isActive);
        new UpdateAlarmStatusAsyncTask().execute(alarm);
    }

    private static class UpdateAlarmStatusAsyncTask extends AsyncTask<AlarmEntity, Void, Void> {
        @Override
        protected Void doInBackground(AlarmEntity... alarms) {
            // Update alarm status in the database in the background
            AlarmDatabase appDatabase = MainActivity.getAlarmDatabase();
            AlarmDao alarmDao = appDatabase.alarmDao();
            alarmDao.updateAlarm(alarms[0]);
            return null;
        }
    }

    private static class CheckAlarmAsyncTask extends AsyncTask<AlarmEntity, Void, Void> {
        private Context context;

        CheckAlarmAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(AlarmEntity... alarms) {
            // Check if alarms should be activated in the background
            Calendar now = Calendar.getInstance();
            Log.d(TAG, "Checking if the alarm should be activated: ");
            for (AlarmEntity alarm : alarms) {
                if (shouldActivateAlarm(alarm, now)) {
                    // The alarm should be activated, create an Intent for AlarmActivity
                    Intent alarmActivityIntent = new Intent(context, AlarmActivity.class);
                    alarmActivityIntent.putExtra("ALARM_ID", alarm.getId());
                    alarmActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(alarmActivityIntent);

                    // You can add additional actions related to alarm activation here
                    Log.d(TAG, "Alarm with ID " + alarm.getId() + " should be activated.");
                }
            }

            return null;
        }

        private static boolean shouldActivateAlarm(AlarmEntity alarm, Calendar now) {
            return alarm.isActive() && isCorrectTime(now, alarm.getHour(), alarm.getMinute(), alarm.getDaysOfWeek());
        }

        private static boolean isCorrectTime(Calendar now, int alarmHour, int alarmMinute, List<String> alarmDaysOfWeek) {
            // Check if the current time matches the alarm settings (hour, minute, day of the week)
            boolean isCorrectHourAndMinute = now.get(Calendar.HOUR_OF_DAY) == alarmHour && now.get(Calendar.MINUTE) == alarmMinute;
            boolean isCorrectDayOfWeek = isDayOfWeekMatch(alarmDaysOfWeek, now.get(Calendar.DAY_OF_WEEK));

            return isCorrectHourAndMinute && isCorrectDayOfWeek;
        }

        private static boolean isDayOfWeekMatch(List<String> alarmDaysOfWeek, int currentDayOfWeek) {
            // Conversion from string to integer for comparison
            String[] daysOfWeekArray = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
            int alarmDayIndex = daysOfWeekArray.length - 1; // Default to Saturday if not found
            for (int i = 0; i < daysOfWeekArray.length; i++) {
                if (alarmDaysOfWeek.contains(daysOfWeekArray[i])) {
                    alarmDayIndex = i;
                    break;
                }
            }

            return alarmDayIndex == currentDayOfWeek - 1;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove the callback to stop checking alarms when the fragment is destroyed
        handler.removeCallbacks(checkAlarmsRunnable);
    }
}
