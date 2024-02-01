package com.example.alarmapp.alarm;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.example.alarmapp.MainActivity;
import com.example.alarmapp.R;
import com.example.alarmapp.alarm.AlarmActivity;
import com.example.alarmapp.database.AlarmDatabase;
import com.example.alarmapp.database.AlarmEntity;

import java.util.Calendar;
import java.util.List;

public class AlarmReceiver extends JobIntentService {

    private static final String TAG = "AlarmReceiver";
    public static final String ALARM_TRIGGER_ACTION = "2";

    private static final int JOB_ID = 123;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, AlarmReceiver.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG, "JobIntentService started");

        try {
            int alarmId = intent.getIntExtra("ALARM_ID", -1);
            Log.d(TAG, "Received alarm with ID: " + alarmId);

            // Pobierz informacje o alarmie z bazy danych
            AlarmEntity alarmEntity = getAlarmFromDatabase(this, alarmId);

            if (alarmEntity != null) {
                // Sprawdź, czy alarm powinien zostać aktywowany w danym momencie
                if (shouldActivateAlarm(alarmEntity)) {
                    // Wywołaj powiadomienie
                    showNotification(this, alarmEntity);

                    // Uruchom AlarmActivity
                    Intent alarmActivityIntent = new Intent(this, AlarmActivity.class);
                    alarmActivityIntent.putExtra("ALARM_ID", alarmEntity.getId());
                    alarmActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(alarmActivityIntent);

                    // Ustaw budzik za pomocą AlarmManager
                    setAlarmUsingAlarmManager(this, alarmEntity);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onHandleWork: " + e.getMessage());
        }
    }

    private AlarmEntity getAlarmFromDatabase(Context context, int alarmId) {
        AlarmDatabase appDatabase = MainActivity.getAlarmDatabase();
        return appDatabase.alarmDao().getAlarmById(alarmId);
    }

    private boolean shouldActivateAlarm(AlarmEntity alarmEntity) {
        // Pobierz obecny czas
        Calendar now = Calendar.getInstance();

        // Pobierz godzinę, minutę i dzień tygodnia z obiektu AlarmEntity
        int alarmHour = alarmEntity.getHour();
        int alarmMinute = alarmEntity.getMinute();
        int[] alarmDaysOfWeek = convertDaysOfWeekToIntArray(alarmEntity.getDaysOfWeek());

        // Sprawdź, czy obecny czas pasuje do ustawień alarmu (godzina, minuta, dni tygodnia)
        boolean isCorrectTime = now.get(Calendar.HOUR_OF_DAY) == alarmHour && now.get(Calendar.MINUTE) == alarmMinute;
        boolean isCorrectDayOfWeek = isDayOfWeekMatch(alarmDaysOfWeek, now.get(Calendar.DAY_OF_WEEK));

        // Zwróć true, jeśli alarm powinien być aktywowany
        return alarmEntity.isActive() && isCorrectTime && isCorrectDayOfWeek;
    }

    

    private boolean isDayOfWeekMatch(int[] alarmDaysOfWeek, int currentDayOfWeek) {
        for (int alarmDay : alarmDaysOfWeek) {
            if (alarmDay == currentDayOfWeek) {
                return true;
            }
        }
        return false;
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

    private void showNotification(Context context, AlarmEntity alarmEntity) {
        // Tutaj możesz zaimplementować powiadomienie
        // Utwórz kanał powiadomień (wymagane od Android Oreo)
        createNotificationChannel(context);

        // Utwórz intencję dla powiadomienia
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Utwórz PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Utwórz powiadomienie
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "alarm_channel")
                .setContentTitle("Budzik")
                .setContentText("Czas na budzenie się!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Pobierz menedżera powiadomień
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Wyświetl powiadomienie
        notificationManager.notify(alarmEntity.getId(), builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alarm Channel";
            String description = "Channel for alarm notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("alarm_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setAlarmUsingAlarmManager(Context context, AlarmEntity alarmEntity) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.setAction(ALARM_TRIGGER_ACTION);
        alarmIntent.putExtra("ALARM_ID", alarmEntity.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmEntity.getId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Ustaw alarm za pomocą AlarmManager
        // Tutaj określ, kiedy alarm ma zostać aktywowany, na przykład używając alarmEntity.getMillisUntilAlarm()
        long triggerTime = getTriggerTime(alarmEntity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    private long getTriggerTime(AlarmEntity alarmEntity) {
        // Calculate the trigger time based on the alarm settings
        Calendar triggerTime = Calendar.getInstance();
        triggerTime.set(Calendar.HOUR_OF_DAY, alarmEntity.getHour());
        triggerTime.set(Calendar.MINUTE, alarmEntity.getMinute());
        triggerTime.set(Calendar.SECOND, 0);
        triggerTime.set(Calendar.MILLISECOND, 0);

        // If the trigger time is in the past, add one day
        if (triggerTime.before(Calendar.getInstance())) {
            triggerTime.add(Calendar.DAY_OF_YEAR, 1);
        }

        return triggerTime.getTimeInMillis();
    }
}
