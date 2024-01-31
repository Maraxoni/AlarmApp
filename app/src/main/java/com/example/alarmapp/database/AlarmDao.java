package com.example.alarmapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;

import java.util.List;

import java.util.List;
@Dao
public interface AlarmDao {
    @Insert
    void insert(AlarmEntity alarm);

    @Query("SELECT * FROM alarms")
    List<AlarmEntity> getAllAlarms();

    @Query("SELECT * FROM alarms WHERE id = :alarmId LIMIT 1")
    AlarmEntity getAlarmById(int alarmId);

    @Delete
    void deleteAlarm(AlarmEntity alarm);

    @Update
    void updateAlarm(AlarmEntity alarm);
}