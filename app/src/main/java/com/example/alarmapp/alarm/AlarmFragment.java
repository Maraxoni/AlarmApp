package com.example.alarmapp.alarm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarm_tab_fragment, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up the Adapter
        alarmAdapter = new AlarmAdapter();
        recyclerView.setAdapter(alarmAdapter);

        // Retrieve alarms from the database and update the UI
        loadAlarms();

        return view;
    }

    private void loadAlarms() {
        new LoadAlarmsAsyncTask().execute();
    }

    private static class LoadAlarmsAsyncTask extends AsyncTask<Void, Void, List<AlarmEntity>> {
        @Override
        protected List<AlarmEntity> doInBackground(Void... voids) {
            // Tutaj wykonaj operacje bazodanowe, takie jak pobieranie danych
            AlarmDatabase appDatabase = MainActivity.getAlarmDatabase();
            AlarmDao alarmDao = appDatabase.alarmDao();
            return alarmDao.getAllAlarms();
        }

        @Override
        protected void onPostExecute(List<AlarmEntity> alarms) {
            // Tutaj zaktualizuj UI po zakończeniu operacji bazodanowych
            alarmAdapter.setAlarms(alarms);
        }
    }
}