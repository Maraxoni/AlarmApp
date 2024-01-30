package com.example.alarmapp.database;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "alarms")
public class AlarmEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private List<String> daysOfWeek;  // Przechowuje dni tygodnia, np. ["Monday", "Wednesday"]
    private int hour;
    private int minute;

    public AlarmEntity(int id, List<String> daysOfWeek, int hour, int minute) {
        this.id = id;
        this.daysOfWeek = daysOfWeek;
        this.hour = hour;
        this.minute = minute;
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
}
