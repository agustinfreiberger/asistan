package ar.edu.unicen.isistan.asistan.application;

import android.content.Context;
import android.content.Intent;

import org.jetbrains.annotations.NotNull;

import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.phone.PhoneEvent;
import ar.edu.unicen.isistan.asistan.synchronizer.data.Synchronizer;
import ar.edu.unicen.isistan.asistan.utils.receivers.AsyncBroadcastReceiver;
import ar.edu.unicen.isistan.asistan.tracker.Tracker;

public class BootReceiver extends AsyncBroadcastReceiver {

    @Override
    public void process(@NotNull Context context, @NotNull Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            AlarmReceiver.createAlarm(context);
            Tracker.bootCompleted(context);
            Database.getInstance().phoneEvent().insert(new PhoneEvent(Intent.ACTION_BOOT_COMPLETED));
        }
    }

}
