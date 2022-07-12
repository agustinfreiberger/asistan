package ar.edu.unicen.isistan.asistan.synchronizer.reports.userstate;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.BatteryManager;

import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.Movement;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Commute;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.storage.database.phone.PhoneEvent;
import ar.edu.unicen.isistan.asistan.storage.database.reports.userstate.BatteryStatus;
import ar.edu.unicen.isistan.asistan.storage.database.reports.userstate.PhoneUsage;
import ar.edu.unicen.isistan.asistan.storage.database.reports.userstate.RingMode;
import ar.edu.unicen.isistan.asistan.storage.database.reports.userstate.UserState;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.User;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.UserManager;

public class UserStateReporter {

    private static final long FIVE_MINUTES = 300000L;
    private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HUNDRED = 100;

    private static final String PREFERENCES = "ar.edu.unicen.isistan.asistan-reporters.userState";
    private static final String STATE_KEY = "state";

    private static UserState userStateInstance;

    private static UserState load(@NotNull Context context) {
        if (UserStateReporter.userStateInstance == null) {
            SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            String json = preferences.getString(STATE_KEY, null);
            if (json != null) {
                try {
                    UserStateReporter.userStateInstance = new Gson().fromJson(json, UserState.class);
                } catch (Exception ignored) {
                }
            }
            User user = UserManager.loadProfile(context);
            UserStateReporter.userStateInstance = new UserState();
            if (user != null)
                UserStateReporter.userStateInstance.setUser(user);
        }

        return UserStateReporter.userStateInstance;
    }

    private static void save(@NotNull Context context, @NotNull UserState userState) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(STATE_KEY,new Gson().toJson(userState));
        editor.apply();
    }

    private static void update(@NotNull Context context, @NotNull UserState userState) {
        userState.setRingMode(getCurrentRingMode((context)));
        userState.setBatteryStatus(getCurrentBatteryStatus(context));
        userState.updatePhoneUsage(getPhoneUsage());
        userState.setReportTime(System.currentTimeMillis());
        createWork(context);
    }

    private static void createWork(@NotNull Context context) {
        Constraints syncConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest reportStateWork = new OneTimeWorkRequest.Builder(UserStateReportWork.class)
                .setConstraints(syncConstraints)
                .build();

        WorkManager.getInstance(context).enqueueUniqueWork(UserStateReportWork.NAME, ExistingWorkPolicy.REPLACE, reportStateWork);
    }

    @NotNull
    private static PhoneUsage getPhoneUsage() {
        PhoneUsage phoneUsage = new PhoneUsage();
        PhoneEvent event = Database.getInstance().phoneEvent().last(Intent.ACTION_USER_PRESENT);
        if (event != null)
            phoneUsage.setLastUserPresent(event.getTime());
        event = Database.getInstance().phoneEvent().last(Intent.ACTION_SCREEN_ON);
        if (event != null)
            phoneUsage.setLastScreenOn(event.getTime());
        event = Database.getInstance().phoneEvent().last(Intent.ACTION_SCREEN_OFF);
        if (event != null)
            phoneUsage.setLastScreenOff(event.getTime());
        return phoneUsage;
    }

    @Nullable
    private static RingMode getCurrentRingMode(@NotNull Context context) {

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (audioManager != null) {
            RingMode ringMode = new RingMode();
            switch (audioManager.getRingerMode()) {
                case AudioManager.RINGER_MODE_SILENT:
                    ringMode.setRingModeType(RingMode.RingModeType.SILENT);
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    ringMode.setRingModeType(RingMode.RingModeType.VIBRATE);
                    break;
                case AudioManager.RINGER_MODE_NORMAL:
                    ringMode.setRingModeType(RingMode.RingModeType.SOUND);
                    int volume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
                    int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
                    if (max != 0)
                        ringMode.setLevel((float)volume/(float)max);
                    break;
            }
            return ringMode;
        }

        return null;
    }

    @Nullable
    private static BatteryStatus getCurrentBatteryStatus(@NotNull Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatusIntent = context.registerReceiver(null, ifilter);

        if (batteryStatusIntent != null) {
            boolean charging = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) != 0;
            int level = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryLevel = level * 100 / (float )scale;
            return new BatteryStatus(batteryLevel, charging);
        }

        return null;
    }

    public synchronized static void update(@NotNull Context context, @NotNull Event event) {
        context = context.getApplicationContext();

        UserState userState = load(context);

        boolean shouldUpdate = (
            (event.getLocation().distance(userState.getLocation()) >= ONE_HUNDRED) ||
            ((event.getTime() - userState.getReportTime()) > FIVE_MINUTES)
        );

        if (shouldUpdate) {
            userState.setCurrentMovement(null);
            userState.setLocation(event.getLocation());
            update(context, userState);
            save(context,userState);
        }
    }

    public synchronized static void update(@NotNull Context context, @NotNull Event event, @NotNull Commute commute) {
        context = context.getApplicationContext();

        UserState userState = load(context);

        boolean shouldUpdate = (
            (userState.getCurrentMovement() == null) ||
            (!userState.getCurrentMovement().getType().equals(Movement.MovementType.COMMUTE)) ||
            (event.getLocation().distance(userState.getLocation()) >= ONE_HUNDRED) ||
            ((event.getTime() - userState.getReportTime()) > ONE_MINUTE)
        );

        if (shouldUpdate) {
            userState.setCurrentMovement(commute.simplify());
            userState.setLocation(event.getLocation());
            update(context, userState);
            save(context, userState);
        }
    }

    public synchronized static void update(@NotNull Context context, @NotNull Event event, @NotNull Visit visit) {
        context = context.getApplicationContext();

        UserState userState = load(context);

        boolean shouldUpdate = (
            (userState.getCurrentMovement() == null) ||
            (!userState.getCurrentMovement().getType().equals(Movement.MovementType.VISIT)) ||
            (event.getTime() - userState.getReportTime() >= FIVE_MINUTES)
        );

        if (shouldUpdate) {
            userState.setCurrentMovement(visit.simplify());
            userState.setLocation(event.getLocation());
            update(context, userState);
            save(context, userState);
        }
    }

    @Nullable
    public synchronized static UserState get(@NotNull Context context) {
        context = context.getApplicationContext();
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        String json = preferences.getString(STATE_KEY, null);
        if (json != null) {
            try {
               return new Gson().fromJson(json, UserState.class);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

}
