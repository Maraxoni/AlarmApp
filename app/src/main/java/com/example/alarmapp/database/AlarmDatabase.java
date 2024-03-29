package com.example.alarmapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.alarmapp.database.Converters;
import com.example.alarmapp.database.AlarmDao;
import com.example.alarmapp.database.AlarmEntity;

@Database(entities = {AlarmEntity.class}, version = 1, exportSchema = false)
@TypeConverters(Converters.class) // Add this line to include the TypeConverter
public abstract class AlarmDatabase extends RoomDatabase {

    public abstract AlarmDao alarmDao();
    public void clearAllTables() {
        getOpenHelper().getWritableDatabase().execSQL("DELETE FROM alarms");
    }

    // Singleton instance for the database

}