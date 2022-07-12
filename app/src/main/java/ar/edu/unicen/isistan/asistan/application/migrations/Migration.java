package ar.edu.unicen.isistan.asistan.application.migrations;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.work.WorkManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.Configuration;
import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.ConfigurationManager;
import ar.edu.unicen.isistan.asistan.views.map.MapController;

public abstract class Migration {

    protected String version;

    public Migration(@NotNull String version) {
        this.version = version;
    }

    public abstract void migrate(@NotNull Context context);

    public boolean shouldMigrate(@NotNull String previous, @NotNull String next) {
        return after(previous,this.version) && !after(next,this.version);
    }

    private boolean after(@NotNull String previous, @NotNull String next) {
        try {
            String[] prevValues = previous.split("\\.");
            String[] nextValues = next.split("\\.");

            for (int index = 0; index < 3; index++) {
                int prevValue = Integer.parseInt(prevValues[index]);
                int nextValue = Integer.parseInt(nextValues[index]);
                if (prevValue < nextValue)
                    return true;
                else if (prevValue > nextValue)
                    return false;
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static final Migration MIGRATION_1_7_6 = new Migration("1.7.6") {

        @Override
        public void migrate(@NotNull Context context) {
            Configuration config = ConfigurationManager.load(context);
            config.setMapView(MapController.Map.GOOGLE_MAPS.getCode());
            ConfigurationManager.store(context, config);
        }

    };

    private static final Migration MIGRATION_1_8_1 = new Migration("1.8.1") {

        @Override
        public void migrate(@NotNull Context context) {
            WorkManager.getInstance(context).cancelAllWorkByTag("ar.edu.unicen.isistan.asistan.synchronizer.sync_work");
            WorkManager.getInstance(context).cancelAllWorkByTag("ar.edu.unicen.isistan.asistan.storage.database.map.osm.map_match_work");
        }

    };

    private static final ArrayList<Migration> MIGRATIONS = new ArrayList<Migration>() {{
        add(MIGRATION_1_7_6);
        add(MIGRATION_1_8_1);
    }};

    public static void migrate(@NotNull Context context, @NotNull String previous, @NotNull String next) {
        for (Migration migration: MIGRATIONS)
            if (migration.shouldMigrate(previous,next))
                migration.migrate(context);
    }

}
