package ar.edu.unicen.isistan.asistan.tracker.wifi;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

import org.jetbrains.annotations.NotNull;

import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.asistan.AsistanEvent;
import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.ConfigurationManager;

public class WiFiTracker {

    private static final String CLASS_TAG = "WiFiTracker";

    private final static String RECEIVER_REGISTERED = "RECEIVER_REGISTERED";
    private final static String RECEIVER_UNREGISTERED = "RECEIVER_UNREGISTERED";

    private static WiFiReceiver wiFiReceiverInstance;

    private static IntentFilter getFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        return intentFilter;
    }

    public synchronized static void start(@NotNull Context context) {
        context = context.getApplicationContext();

        if (WiFiTracker.wiFiReceiverInstance == null) {
            WiFiTracker.wiFiReceiverInstance = new WiFiReceiver();
            context.registerReceiver(WiFiTracker.wiFiReceiverInstance, getFilter());
            Database.getInstance().asistan().insert(new AsistanEvent(CLASS_TAG, RECEIVER_REGISTERED));
        }
    }

    public synchronized static void stop(@NotNull Context context) {
        context = context.getApplicationContext();

        if (WiFiTracker.wiFiReceiverInstance != null) {
            try {
                context.unregisterReceiver(WiFiTracker.wiFiReceiverInstance);
            } catch (Exception ignored) {}
            WiFiTracker.wiFiReceiverInstance = null;
            Database.getInstance().asistan().insert(new AsistanEvent(CLASS_TAG, RECEIVER_UNREGISTERED));
        }

        clean();
    }

    private static void clean() {
        WiFiTracker.wiFiReceiverInstance = null;
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
