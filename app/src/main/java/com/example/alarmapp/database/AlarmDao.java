package com.example.alarmapp.database;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
@Dao
public interface AlarmDao {
    @Insert
    void insert(AlarmEntity alarm);

    @Query("SELECT * FROM alarms")
    List<AlarmEntity> getAllAlarms();
}
