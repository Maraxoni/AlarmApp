package com.example.alarmapp.database;

import android.content.Context;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.List;

@Entity(tableName = "alarms")
public class AlarmEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private List<String> daysOfWeek; // Change here
    private int hour;
    private int minute;
    private boolean isActive;

    public AlarmEntity(int id, List<String> daysOfWeek, int hour, int minute, boolean isActive) {
        this.id = id;
        this.daysOfWeek = daysOfWeek;
        this.hour = hour;
        this.minute = minute;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public List<String> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(List<String> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public long getMillisUntilAlarm() {
        Calendar alarmTime = getAlarmCalendar();
        long currentTimeMillis = System.currentTimeMillis();

        if (alarmTime.getTimeInMillis() > currentTimeMillis) {
            return alarmTime.getTimeInMillis() - currentTimeMillis;
        } else {
            alarmTime.add(Calendar.DAY_OF_MONTH, 1);
            return alarmTime.getTimeInMillis() - currentTimeMillis;
        }
    }

    private Calendar getAlarmCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, getHour());
        calendar.set(Calendar.MINUTE, getMinute());

        int[] daysOfWeek = convertDaysOfWeekToIntArray(getDaysOfWeek());
        for (int day : daysOfWeek) {
            if (day >= calendar.get(Calendar.DAY_OF_WEEK)) {
                calendar.set(Calendar.DAY_OF_WEEK, day);
                break;
            }
        }

        return calendar;
    }

    private int[] convertDaysOfWeekToIntArray(List<String> daysOfWeek) {
        int[] daysArray = new int[daysOfWeek.size()];
        for (int i = 0; i < daysOfWeek.size(); i++) {
            switch (daysOfWeek.get(i)) {
                case "Monday":
                    daysArray[i] = Calendar.MONDAY;
                    break;
                case "Tuesday":
                    daysArray[i] = Calendar.TUESDAY;
                    break;
                case "Wednesday":
                    daysArray[i] = Calendar.WEDNESDAY;
                    break;
                case "Thursday":
                    daysArray[i] = Calendar.THURSDAY;
                    break;
                case "Friday":
                    daysArray[i] = Calendar.FRIDAY;
                    break;
                case "Saturday":
                    daysArray[i] = Calendar.SATURDAY;
                    break;
                case "Sunday":
                    daysArray[i] = Calendar.SUNDAY;
                    break;
            }
        }
        return daysArray;
    }
}