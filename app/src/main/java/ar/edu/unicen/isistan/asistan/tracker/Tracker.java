package ar.edu.unicen.isistan.asistan.tracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import ar.edu.unicen.isistan.asistan.storage.preferences.user.UserManager;
import ar.edu.unicen.isistan.asistan.tracker.mobility.MobilityTracker;
import ar.edu.unicen.isistan.asistan.tracker.phone.PhoneTracker;
import ar.edu.unicen.isistan.asistan.tracker.wifi.WiFiTracker;

public class Tracker {

    public synchronized static void start(@NonNull Context context) {
        context = context.getApplicationContext();

        if (Tracker.canRun(context)) {
            MobilityTracker.start(context);
            PhoneTracker.start(context);
            WiFiTracker.start(context);
        }
    }

    public synchronized static void stop(@NonNull Context context) {
        context = context.getApplicationContext();

        MobilityTracker.stop(context);
        PhoneTracker.stop(context);
        WiFiTracker.stop(context);
    }

    public synchronized static void appOpened(@NonNull Context context) {
        context = context.getApplicationContext();

        if (Tracker.canRun(context)) {
            MobilityTracker.appOpened(context);
            PhoneTracker.appOpened(context);
            WiFiTracker.appOpened(context);
        } else {
            stop(context);
        }
    }

    public synchronized static void bootCompleted(@NonNull Context context) {
        context = context.getApplicationContext();

        if (Tracker.canRun(context)) {

            MobilityTracker.bootCompleted(context);
            PhoneTracker.bootCompleted(context);
            WiFiTracker.bootCompleted(context);
        }
    }

    public synchronized static void replacedPackage(@NonNull  Context context) {
        context = context.getApplicationContext();

        if (Tracker.canRun(context)) {
            MobilityTracker.replacedPackage(context);
            PhoneTracker.replacedPackage(context);
            WiFiTracker.replacedPackage(context);
        }
    }

    public static void mapUpdate(@NonNull Context context) {
        context = context.getApplicationContext();

        if (Tracker.canRun(context)) {
            MobilityTracker.mapUpdate(context);
        }
    }

    public static void checkForRefresh(@NonNull Context context) {
        context = context.getApplicationContext();

        if (Tracker.canRun(context)) {
            MobilityTracker.checkForRefresh(context);
        }
    }

    public static boolean hasRequiredPermissions(@NonNull Context context) {
        context = context.getApplicationContext();

        if (UserManager.loadProfile(context) == null)
            return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return ((ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED));
            } else {
                return (ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
            }
        } else {
            return true;
        }
    }

    private static boolean canRun(@NonNull Context context) {
        context = context.getApplicationContext();

        return hasRequiredPermissions(context) && MobilityTracker.isLocationEnabled(context);
    }
}
