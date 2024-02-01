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
    private List<String> daysOfWeek;
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
        Calendar alarmTime = getAlarmCalendar(); // Załóżmy, że masz taką metodę zwracającą godzinę alarmu jako obiekt Calendar
        long currentTimeMillis = System.currentTimeMillis();

        if (alarmTime.getTimeInMillis() > currentTimeMillis) {
            return alarmTime.getTimeInMillis() - currentTimeMillis;
        } else {
            // Jeśli alarm ma być aktywowany na następny dzień, oblicz czas do następnego wystąpienia alarmu
            alarmTime.add(Calendar.DAY_OF_MONTH, 1);
            return alarmTime.getTimeInMillis() - currentTimeMillis;
        }
    }

    private Calendar getAlarmCalendar() {
        // Tutaj należy zaimplementować logikę uzyskiwania daty i godziny alarmu
        // W przykładzie zakładam, że masz getHour(), getMinute() i getDaysOfWeek() jako metody zwracające odpowiednie wartości
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, getHour());
        calendar.set(Calendar.MINUTE, getMinute());

        // Dodaj obsługę dni tygodnia
        // ...

        return calendar;
    }
}