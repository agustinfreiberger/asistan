package ar.edu.unicen.isistan.asistan.application;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;


import ar.edu.unicen.isistan.asistan.synchronizer.data.works.MobilitySyncWork;
import ar.edu.unicen.isistan.asistan.synchronizer.data.works.RawSyncWork;
import ar.edu.unicen.isistan.asistan.tracker.Tracker;
import ar.edu.unicen.isistan.asistan.utils.receivers.AsyncBroadcastReceiver;

public class AlarmReceiver extends AsyncBroadcastReceiver {

    private static final String PREFERENCES = "ar.edu.unicen.isistan.asistan-alarm";
    private static final String LAST_ALARM= "last_alarm";
    private static final long TWELVE_HOURS = 43200000L;
    private static final long ONE_HOUR = 3600000;
    @Override
    public void process(@NonNull Context context, @NonNull Intent intent) {
        MobilitySyncWork.Builder.createWork(context);
        RawSyncWork.Builder.createWork(context);
        Tracker.checkForRefresh(context);
    }

    public synchronized static void createAlarm(@NonNull Context context) {
        context = context.getApplicationContext();

        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES,Context.MODE_PRIVATE);
        long lastTime = preferences.getLong(LAST_ALARM,0L);
        long now = System.currentTimeMillis();

        if (now - lastTime > TWELVE_HOURS) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (alarmManager != null) {
                alarmManager.cancel(alarmIntent);
                try {
                    alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 0L, ONE_HOUR * 4, alarmIntent);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putLong(LAST_ALARM,now);
                    editor.apply();
                } catch (Exception ignored) { }
            }


        }
    }

    public synchronized static void cancel(@NonNull Context context) {
        context = context.getApplicationContext();

        AlarmManager alarmManager =  (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager != null) {
            alarmManager.cancel(alarmIntent);
        }
    }


}
