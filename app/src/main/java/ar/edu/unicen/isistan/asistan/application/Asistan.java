package ar.edu.unicen.isistan.asistan.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.application.migrations.Migration;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.map.MapManager;
import ar.edu.unicen.isistan.asistan.tracker.Tracker;

public class Asistan extends Application {

    public static final String PREFERENCE = "ar.edu.unicen.isistan.asistan.application.preferences";
    public static final String LAST_RUN = "last_run";

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences = this.getApplicationContext().getSharedPreferences(Asistan.PREFERENCE, Context.MODE_PRIVATE);
        String lastRun = preferences.getString(Asistan.LAST_RUN, null);
        String currentRun = this.getApplicationContext().getResources().getString(R.string.app_version);

        this.prepareStorage();

        if (lastRun == null) {
            this.storeVersion(preferences, currentRun);
        } else if (!lastRun.equals(currentRun)) {
            Migration.migrate(this, lastRun, currentRun);
            this.storeVersion(preferences, currentRun);
        }

        AlarmReceiver.createAlarm(this);
        AsyncTask.execute(() -> Tracker.appOpened(this));
    }

    private void storeVersion(SharedPreferences preferences, String currentRun) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Asistan.LAST_RUN, currentRun);
        editor.apply();
    }

    private void prepareStorage() {
        Database.prepare(this.getApplicationContext());
        MapManager.prepare(this.getApplicationContext());
    }

}
