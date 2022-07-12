package ar.edu.unicen.isistan.asistan.application;

import android.content.Context;
import android.content.Intent;

import org.jetbrains.annotations.NotNull;

import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.phone.PhoneEvent;
import ar.edu.unicen.isistan.asistan.utils.receivers.AsyncBroadcastReceiver;
import ar.edu.unicen.isistan.asistan.tracker.Tracker;

public class PackageReplacedReceiver extends AsyncBroadcastReceiver {

    @Override
    public void process(@NotNull Context context, @NotNull Intent intent) {
        if (Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
            AlarmReceiver.createAlarm(context);
            Tracker.replacedPackage(context);
            Database.getInstance().phoneEvent().insert(new PhoneEvent(Intent.ACTION_MY_PACKAGE_REPLACED));
        }
    }

}
