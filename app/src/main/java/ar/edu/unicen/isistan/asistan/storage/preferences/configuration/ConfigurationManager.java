package ar.edu.unicen.isistan.asistan.storage.preferences.configuration;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

public class ConfigurationManager {

    private static final String PREFERENCE = "ar.edu.unicen.isistan.asistan.storage.preferences.configuration";

    private static final String KEY = "value";

    @NotNull
    public synchronized static Configuration load(@NotNull Context context) {
        context = context.getApplicationContext();

        SharedPreferences preferences = context.getSharedPreferences(ConfigurationManager.PREFERENCE,Context.MODE_PRIVATE);
        String json = preferences.getString(ConfigurationManager.KEY, null);
        if (json == null) {
            Configuration configuration = new Configuration();
            store(context,configuration);
            return configuration;
        } else {
            return new Gson().fromJson(json, Configuration.class);
        }
    }

    public synchronized static void store(@NotNull Context context, @NotNull Configuration configuration) {
        context = context.getApplicationContext();

        Gson gson = new Gson();
        String json = gson.toJson(configuration);
        SharedPreferences preferences = context.getSharedPreferences(ConfigurationManager.PREFERENCE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ConfigurationManager.KEY,json);
        editor.apply();
    }

    public static void subscribe(@NotNull Context context, @NotNull SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences preferences = context.getSharedPreferences(ConfigurationManager.PREFERENCE,Context.MODE_PRIVATE);
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unsubscribe(@NotNull Context context, @NotNull SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences preferences = context.getSharedPreferences(ConfigurationManager.PREFERENCE,Context.MODE_PRIVATE);
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

}
