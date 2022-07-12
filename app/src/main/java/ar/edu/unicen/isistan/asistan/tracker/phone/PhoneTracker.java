package ar.edu.unicen.isistan.asistan.tracker.phone;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Build;

import org.jetbrains.annotations.NotNull;

import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.asistan.AsistanEvent;
import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.ConfigurationManager;

public class PhoneTracker {

    private static final String CLASS_TAG = "PhoneTracker";

    private final static String RECEIVER_REGISTERED = "RECEIVER_REGISTERED";
    private final static String RECEIVER_UNREGISTERED = "RECEIVER_UNREGISTERED";

    private static PhoneEventReceiver phoneEventsReceiverInstance;

    private static IntentFilter getFilter() {
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(Intent.ACTION_BATTERY_LOW);
        intentFilter.addAction(Intent.ACTION_BATTERY_OKAY);
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_REBOOT);
        intentFilter.addAction(Intent.ACTION_SHUTDOWN);

        intentFilter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);

        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intentFilter.addAction(Intent.ACTION_LOCKED_BOOT_COMPLETED);
            intentFilter.addAction(Intent.ACTION_USER_UNLOCKED);
        }

        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);

        return intentFilter;
    }

    public synchronized static void start(@NotNull Context context) {
        context = context.getApplicationContext();

        if (PhoneTracker.phoneEventsReceiverInstance == null) {
            PhoneTracker.phoneEventsReceiverInstance = new PhoneEventReceiver();
            context.registerReceiver(PhoneTracker.phoneEventsReceiverInstance, getFilter());
            Database.getInstance().asistan().insert(new AsistanEvent(CLASS_TAG, RECEIVER_REGISTERED));
        }
    }

    public synchronized static void stop(@NotNull Context context) {
        context = context.getApplicationContext();

        if (PhoneTracker.phoneEventsReceiverInstance != null) {
            try {
                context.unregisterReceiver(PhoneTracker.phoneEventsReceiverInstance);
            } catch (Exception ignored) {}
            PhoneTracker.phoneEventsReceiverInstance = null;
            Database.getInstance().asistan().insert(new AsistanEvent(CLASS_TAG, RECEIVER_UNREGISTERED));
        }

        clean();
    }

    private static void clean() {
        PhoneTracker.phoneEventsReceiverInstance = null;
    }

    private static void refresh(@NotNull Context context) {
        stop(context);
        if (ConfigurationManager.load(context).isRunning())
            start(context);
    }

    public synchronized static void bootCompleted(@NotNull Context context) {
        context = context.getApplicationContext();
        refresh(context);
    }

    public synchronized static void replacedPackage(@NotNull Context context) {
        context = context.getApplicationContext();
        refresh(context);
    }

    public synchronized static void appOpened(Context context) {
        context = context.getApplicationContext();
        refresh(context);
    }

}
