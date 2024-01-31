package com.example.alarmapp.alarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmapp.R;
import com.example.alarmapp.database.AlarmEntity;

import java.util.List;
import java.util.Locale;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private List<AlarmEntity> alarms;
    private OnAlarmClickListener onAlarmClickListener;
    private Context context;

    public AlarmAdapter(Context context, OnAlarmClickListener onAlarmClickListener) {
        this.context = context;
        this.onAlarmClickListener = onAlarmClickListener;
    }

    public void setAlarms(List<AlarmEntity> alarms) {
        this.alarms = alarms;
        notifyDataSetChanged();
    }

    public interface OnAlarmClickListener {
        void onEditClick(AlarmEntity alarm);
        void onDeleteClick(AlarmEntity alarm);
        void onSwitchToggle(AlarmEntity alarm, boolean isActive);
    }

    public void setOnAlarmClickListener(OnAlarmClickListener listener) {
        this.onAlarmClickListener = listener;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_list_item, parent, false);
        return new AlarmViewHolder(view, onAlarmClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        if (alarms != null) {
            AlarmEntity currentAlarm = alarms.get(position);

            // Extract hour and minute from the currentAlarm
            int hour = currentAlarm.getHour();
            int minute = currentAlarm.getMinute();

            // Display hour and minute in your desired format
            String timeString = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            holder.alarmTitleTextView.setText(timeString);

            // Extract days of the week and display the first letter of each day with commas
            List<String> daysOfWeek = currentAlarm.getDaysOfWeek();
            StringBuilder daysString = new StringBuilder();
            for (int i = 0; i < daysOfWeek.size(); i++) {
                if (i > 0) {
                    daysString.append(", ");
                }
                String day = daysOfWeek.get(i);
                if (!day.isEmpty()) {
                    daysString.append(day.charAt(0));
                }
            }
            holder.alarmWeekTextView.setText(daysString.toString());

            // Set the state of the switch
            holder.alarmIsActiveSwitch.setChecked(currentAlarm.isActive());
        }
    }

    @Override
    public int getItemCount() {
        return alarms != null ? alarms.size() : 0;
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder {
        private TextView alarmTitleTextView;
        private TextView alarmWeekTextView;
        private Switch alarmIsActiveSwitch;

        public AlarmViewHolder(@NonNull View itemView, OnAlarmClickListener listener) {
            super(itemView);
            alarmTitleTextView = itemView.findViewById(R.id.alarmTitleTextView);
            alarmWeekTextView = itemView.findViewById(R.id.alarmWeekTextView);
            alarmIsActiveSwitch = itemView.findViewById(R.id.alarmIsActiveSwitch);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onAlarmClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onAlarmClickListener.onEditClick(alarms.get(position));
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onAlarmClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onAlarmClickListener.onDeleteClick(alarms.get(position));
                            return true;
                        }
                    }
                    return false;
                }
            });

            // Obsługa przełączania statusu alarmu
            alarmIsActiveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (onAlarmClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onAlarmClickListener.onSwitchToggle(alarms.get(position), isChecked);
                        }
                    }
                }
            });
        }
    }
}