package ar.edu.unicen.isistan.asistan.synchronizer.data;

import android.content.Context;
import android.content.SharedPreferences;
import org.jetbrains.annotations.NotNull;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.User;
import ar.edu.unicen.isistan.asistan.synchronizer.AsistanAPI;

public class Synchronizer {

    private static final String PREFERENCE = "ar.edu.unicen.isistan.asistan.synchronizer.syncadapter";
    private static final String LAST_RAW_SYNC = "last_sync";

    private static final long ONE_HOUR = 3600000;

    // Sync profile data

    public static class SyncProfile {

        public synchronized static boolean syncProfile(@NotNull Context context) {
            try {
                context = context.getApplicationContext();

                User user = AsistanAPI.getUser(context);
                if (user == null)
                    return false;

                String token = user.getToken(context);
                if (token != null)
                    return AsistanAPI.putProfile(user, token);

                return false;
            } catch (Exception ignored) {
                return false;
            }
        }

    }

    // Sync raw data

    public static class SyncRawData {

        public synchronized static boolean syncRawData(@NotNull Context context) {
            try {
                context = context.getApplicationContext();

                User user = AsistanAPI.getUser(context);
                if (user == null)
                    return false;

                String token = user.getToken(context);
                if (token != null) {
                    SharedPreferences preferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
                    long since = preferences.getLong(LAST_RAW_SYNC, 0);

                    long installed = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).firstInstallTime;

                    if (since < installed)
                        since = installed;

                    Event event = Database.getInstance().event().last();
                    if (event == null)
                        return true;

                    long now = event.getTime() - 1L;

                    long until = since;

                    Data rawData = new Data();
                    rawData.setUser(user);

                    while (until < now) {
                        until += ONE_HOUR;

                        if (until > now)
                            until = now;

                        loadRawData(rawData, until);

                        if (AsistanAPI.postData(rawData, token)) {
                            deleteRawData(until);
                            since = until;
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putLong(LAST_RAW_SYNC, since);
                            editor.apply();
                        } else {
                            return false;
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            } catch (Exception ignored) {
                return false;
            }
        }

        private static void loadRawData(@NotNull Data data, long until) {
            Database database = Database.getInstance();
            data.setGeolocations(database.geoLocation().selectUntil(until));
            data.setActivities(database.activity().selectUntil(until));
            data.setAsistanEvents(database.asistan().selectUntil(until));
            data.setPhoneEvents(database.phoneEvent().selectUntil(until));
            data.setEvents(database.event().selectUntil(until));
            data.setWifiScans(database.wifi().selectUntil(until));
        }

        private static void deleteRawData(long until) {
            Database database = Database.getInstance();
            database.activity().deleteUntil(until);
            database.geoLocation().deleteUntil(until);
            database.asistan().deleteUntil(until);
            database.phoneEvent().deleteUntil(until);
            database.event().deleteUntil(until);
            database.wifi().deleteUntil(until);
        }
    }

    // Sync mobility data

    public static class SyncMobility {

        public synchronized static boolean synchMobility(@NotNull Context context) {
            context = context.getApplicationContext();

            User user = AsistanAPI.getUser(context);
            if (user == null)
                return false;

            String token = user.getToken(context);
            if (token != null) {
                Data mobilityData = new Data();
                mobilityData.setUser(user);
                loadMobilityData(mobilityData);
                if (AsistanAPI.postData(mobilityData, token)) {
                    deleteMobilityData();
                    return true;
                }
            }

            return false;
        }

        private static void loadMobilityData(Data data) {
            Database database = Database.getInstance();
            data.setPlaces(database.mobility().selectPlacesToUpload());
            data.setVisits(database.mobility().selectVisitsToUpload());
            data.setCommutes(database.mobility().selectCommutesToUpload());
        }

        private static void deleteMobilityData() {
            Database database = Database.getInstance();
            database.mobility().markMobilityAsUploaded();
            database.mobility().deleteMobilityUploaded();
        }
    }

}
