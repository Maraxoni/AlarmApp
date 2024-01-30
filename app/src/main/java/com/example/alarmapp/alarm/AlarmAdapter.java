package com.example.alarmapp.alarm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmapp.R;
import com.example.alarmapp.database.AlarmEntity;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private List<AlarmEntity> alarms;

    public void setAlarms(List<AlarmEntity> alarms) {
        this.alarms = alarms;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_list_item, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        if (alarms != null) {
            AlarmEntity currentAlarm = alarms.get(position);
            holder.bind(currentAlarm);
        }
    }

    @Override
    public int getItemCount() {
        return alarms != null ? alarms.size() : 0;
    }

    static class AlarmViewHolder extends RecyclerView.ViewHolder {

        private final TextView alarmTitleTextView;

        AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            alarmTitleTextView = itemView.findViewById(R.id.alarmTitleTextView);
        }

        void bind(AlarmEntity alarm) {
            alarmTitleTextView.setText(alarm.getDaysOfWeek().toString() + " at " +
                    String.format("%02d:%02d", alarm.getHour(), alarm.getMinute()));
        }
    }
}