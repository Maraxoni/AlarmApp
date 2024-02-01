package com.example.alarmapp.alarm;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.alarmapp.database.AlarmDao;
import com.example.alarmapp.database.AlarmDatabase;
import com.example.alarmapp.database.AlarmEntity;

import java.util.List;

public class AlarmFragment extends Fragment {

    private RecyclerView recyclerView;
    private static AlarmAdapter alarmAdapter;
    private Button addAlarmButton;
    private static final int ADD_EDIT_ALARM_REQUEST_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarm_tab_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        addAlarmButton = view.findViewById(R.id.addAlarmButton);

        initRecyclerView();

        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddAlarmActivity();
            }
        });

        loadAlarms();

        return view;
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
            // Jeśli wynik jest OK, wczytaj ponownie listę alarmów
            loadAlarms();
        }
    }

    private void loadAlarms() {
        new LoadAlarmsAsyncTask(getContext()).execute();
    }

    private static class LoadAlarmsAsyncTask extends AsyncTask<Void, Void, List<AlarmEntity>> {
        private Context context;

        LoadAlarmsAsyncTask(Context context) {
            this.context = context;
        }

        // Konstruktor bezargumentowy
        LoadAlarmsAsyncTask() {
            // Pusty konstruktor
        }

        @Override
        protected List<AlarmEntity> doInBackground(Void... voids) {
            AlarmDatabase appDatabase = MainActivity.getAlarmDatabase();
            AlarmDao alarmDao = appDatabase.alarmDao();
            List<AlarmEntity> alarms = alarmDao.getAllAlarms();

            // Sprawdzanie, czy każdy alarm powinien być aktywowany
            for (AlarmEntity alarm : alarms) {
                if (shouldActivateAlarm(alarm)) {
                    // Tworzenie nowego Intentu AlarmActivity, jeśli alarm powinien być aktywowany
                    Intent alarmActivityIntent = new Intent(context, AlarmActivity.class);
                    alarmActivityIntent.putExtra("ALARM_ID", alarm.getId());
                    alarmActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(alarmActivityIntent);
                }
            }

            return alarms;
        }

        @Override
        protected void onPostExecute(List<AlarmEntity> alarms) {
            alarmAdapter.setAlarms(alarms);
            alarmAdapter.notifyDataSetChanged();
        }

        private boolean shouldActivateAlarm(AlarmEntity alarm) {
            // Tu wpisz logikę sprawdzającą, czy alarm powinien zostać aktywowany
            // Możesz użyć kodu z poprzedniej odpowiedzi, czyli zawierającego logikę czasową, dni tygodnia itp.
            return alarm.isActive();  // Na potrzeby przykładu, zawsze aktywuj alarm
        }
    }

    private void deleteAlarm(AlarmEntity alarm) {
        new DeleteAlarmAsyncTask().execute(alarm);
    }

    private static class DeleteAlarmAsyncTask extends AsyncTask<AlarmEntity, Void, Void> {
        @Override
        protected Void doInBackground(AlarmEntity... alarms) {
            AlarmDatabase appDatabase = MainActivity.getAlarmDatabase();
            AlarmDao alarmDao = appDatabase.alarmDao();
            alarmDao.deleteAlarm(alarms[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new LoadAlarmsAsyncTask().execute();
        }
    }

    private void updateAlarmStatus(AlarmEntity alarm, boolean isActive) {
        alarm.setActive(isActive);
        new UpdateAlarmStatusAsyncTask().execute(alarm);
    }

    private static class UpdateAlarmStatusAsyncTask extends AsyncTask<AlarmEntity, Void, Void> {
        @Override
        protected Void doInBackground(AlarmEntity... alarms) {
            AlarmDatabase appDatabase = MainActivity.getAlarmDatabase();
            AlarmDao alarmDao = appDatabase.alarmDao();
            alarmDao.updateAlarm(alarms[0]);
            return null;
        }
    }
}