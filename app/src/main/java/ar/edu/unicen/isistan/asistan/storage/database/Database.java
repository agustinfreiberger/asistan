package ar.edu.unicen.isistan.asistan.storage.database;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import androidx.annotation.NonNull;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;

import ar.edu.unicen.isistan.asistan.storage.database.activity.Activity;
import ar.edu.unicen.isistan.asistan.storage.database.activity.ActivityDao;
import ar.edu.unicen.isistan.asistan.storage.database.asistan.AsistanEventDao;
import ar.edu.unicen.isistan.asistan.storage.database.asistan.AsistanEvent;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.GeoLocation;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.GeoLocationDao;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMDao;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.MobilityDao;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Commute;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.EventDao;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OverpassAPI;
import ar.edu.unicen.isistan.asistan.storage.database.phone.PhoneEvent;
import ar.edu.unicen.isistan.asistan.storage.database.phone.PhoneEventDao;
import ar.edu.unicen.isistan.asistan.storage.database.sensor.PhoneSensorEvent;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMArea;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMBusLine;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMCity;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMPlace;
import ar.edu.unicen.isistan.asistan.storage.database.wifi.WiFiDao;
import ar.edu.unicen.isistan.asistan.storage.database.wifi.WiFiScan;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Area;
import ar.edu.unicen.isistan.asistan.utils.geo.bound.Bound;

//agregar las entities acá
@androidx.room.Database(entities = {
        AsistanEvent.class, PhoneEvent.class, WiFiScan.class,
        GeoLocation.class, Activity.class,
        Event.class,
        Commute.class, Visit.class, Place.class,
        PhoneSensorEvent.class,
        OSMCity.class, OSMPlace.class, OSMArea.class, OSMBusLine.class},
        version = 17)
public abstract class Database extends RoomDatabase {

    private static final String DB_NAME = "database.db";

    private static volatile Database INSTANCE = null;

    private static final String TANDIL_AMENITIES_QUERY = "[timeout:30][out:json]; (node['amenity'~'pub|cafe|restaurant'](-37.35160114495477,-59.163780212402344,-37.29754420029534,-59.08927917480469);); out body;";
    private static final String TANDIL_TOURISM_QUERY = "[timeout:30][out:json]; (nwr['tourism'~'attraction|gallery|zoo'](-37.35160114495477,-59.163780212402344,-37.29754420029534,-59.08927917480469);); out body;";

    private static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE visit ADD COLUMN 'labels' TEXT");
            database.execSQL("ALTER TABLE commute ADD COLUMN 'labels' TEXT");
            database.execSQL("UPDATE visit SET labels = '[]'");
            database.execSQL("UPDATE commute SET labels = '[]'");
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE app_event");
            database.execSQL("DROP TABLE communication");
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(3,4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE osm_area ADD COLUMN 'north' REAL");
            database.execSQL("ALTER TABLE osm_area ADD COLUMN 'south' REAL");
            database.execSQL("ALTER TABLE osm_area ADD COLUMN 'east' REAL");
            database.execSQL("ALTER TABLE osm_area ADD COLUMN 'west' REAL");
        }
    };

    private static final Migration MIGRATION_4_5 = new Migration(4,5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };

    private static final Migration MIGRATION_5_6 = new Migration(5,6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Cursor cursor = database.query("SELECT * FROM osm_area");
            while(cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex("_id");
                int areaIndex = cursor.getColumnIndex("area");
                Area area = new Gson().fromJson(cursor.getString(areaIndex),Area.class);
                Bound bound = area.getBound();
                double north = bound.getNorth();
                double south = bound.getSouth();
                double west = bound.getWest();
                double east = bound.getEast();
                String id = cursor.getString(idIndex);
                database.execSQL("UPDATE osm_area SET north = ?, south = ?, east = ?, west = ? WHERE _id = ?",new Object[] {north,south,east,west,id});
            }
            cursor.close();
        }
    };

    private static final Migration MIGRATION_6_7 = new Migration(6,7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE place ADD COLUMN 'north' REAL");
            database.execSQL("ALTER TABLE place ADD COLUMN 'south' REAL");
            database.execSQL("ALTER TABLE place ADD COLUMN 'east' REAL");
            database.execSQL("ALTER TABLE place ADD COLUMN 'west' REAL");

            Cursor cursor = database.query("SELECT * FROM place");
            while(cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex("_id");
                int areaIndex = cursor.getColumnIndex("area");
                Area area = new Gson().fromJson(cursor.getString(areaIndex),Area.class);
                Bound bound = area.getBound();
                double north = bound.getNorth();
                double south = bound.getSouth();
                double west = bound.getWest();
                double east = bound.getEast();
                long id = cursor.getLong(idIndex);
                database.execSQL("UPDATE place SET north = ?, south = ?, east = ?, west = ? WHERE _id = ?",new Object[] {north,south,east,west,id});
            }
            cursor.close();
        }
    };

    private static final Migration MIGRATION_7_8 = new Migration(7,8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Cursor cursor = database.query("SELECT * FROM osm_area");
            while(cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex("_id");
                int areaIndex = cursor.getColumnIndex("area");
                Area area = new Gson().fromJson(cursor.getString(areaIndex),Area.class);
                area.getSimplified();
                String id = cursor.getString(idIndex);
                database.execSQL("UPDATE osm_area SET area = ? WHERE _id = ?",new Object[] {new Gson().toJson(area),id});
            }
            cursor.close();

            cursor = database.query("SELECT * FROM place");
            while(cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex("_id");
                int areaIndex = cursor.getColumnIndex("area");
                Area area = new Gson().fromJson(cursor.getString(areaIndex),Area.class);
                area.getSimplified();
                String id = cursor.getString(idIndex);
                database.execSQL("UPDATE place SET area = ?, upload = 1 WHERE _id = ?",new Object[] {new Gson().toJson(area),id});
            }
            cursor.close();
        }
    };

    private static final Migration MIGRATION_8_9 = new Migration(8,9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("UPDATE visit SET upload = 1");
            database.execSQL("UPDATE commute SET upload = 1");

        }
    };

    private static final Migration MIGRATION_9_10 = new Migration(9,10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE osm_place RENAME TO osm_place_old;");
            database.execSQL("CREATE TABLE osm_place (_id TEXT NOT NULL PRIMARY KEY, name TEXT, category INTEGER NOT NULL, building INTEGER NOT NULL, latitude REAL, longitude REAL);");
            database.execSQL("INSERT INTO osm_place(_id,name,category,building,latitude,longitude) SELECT _id,name,category,building,latitude,longitude FROM osm_place_old;");
            database.execSQL("DROP TABLE osm_place_old;");

            database.execSQL("ALTER TABLE osm_area RENAME TO osm_area_old;");
            database.execSQL("CREATE TABLE osm_area (_id TEXT NOT NULL PRIMARY KEY, name TEXT, area TEXT, east REAL, south REAL, north REAL, west REAL, category INTEGER NOT NULL, building INTEGER NOT NULL);");
            database.execSQL("INSERT INTO osm_area(_id,name,area,east,south,north,west,category,building) SELECT _id,name,area,east,south,north,west,category,building FROM osm_area_old;");
            database.execSQL("DROP TABLE osm_area_old;");

            database.execSQL("ALTER TABLE bus_line RENAME TO bus_line_old;");
            database.execSQL("CREATE TABLE bus_line (id INTEGER NOT NULL PRIMARY KEY, line TEXT, paths TEXT, east REAL, south REAL, north REAL, west REAL);");
            database.execSQL("DROP TABLE bus_line_old;");

            database.execSQL("UPDATE osm_city SET detailed = 0;");
            database.execSQL("UPDATE osm_city SET radius = 10000 WHERE radius = 15000;");
            database.execSQL("UPDATE osm_city SET radius = 5000 WHERE radius = 7500;");
        }
    };

    private static final Migration MIGRATION_10_11 = new Migration(10,11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DELETE FROM place WHERE _id NOT IN (SELECT place_id FROM visit)");
        }
    };

    private static final Migration MIGRATION_11_12 = new Migration(11,12) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `wifi_scan` (`time` INTEGER NOT NULL, `elapsed_time` INTEGER NOT NULL, `wifis` TEXT NOT NULL, PRIMARY KEY(`time`))");
        }
    };

    private static final Migration MIGRATION_12_13 = new Migration(12,13) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE `wifi_scan`");
            database.execSQL("DELETE FROM `place` WHERE NOT EXISTS (SELECT 1 FROM visit WHERE place_id == place._id)" );
        }
    };

    private static final Migration MIGRATION_13_14 = new Migration(13,14) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("UPDATE `place` SET upload = 1");
            database.execSQL("UPDATE `visit` SET upload = 1");
        }
    };

    private static final Migration MIGRATION_14_15 = new Migration(14,15) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("UPDATE `phone_event` SET type = 'android.net.wifi.SCAN_RESULTS' WHERE type IS NULL");
            database.execSQL("UPDATE `place` SET place_category = -3 WHERE NOT EXISTS (SELECT 1 FROM visit WHERE place_id == place._id)" );
        }
    };

    private static final Migration MIGRATION_15_16 = new Migration(15,16) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("UPDATE place SET place_category = -3, upload = 1 WHERE place_category IS NOT -3 AND _id NOT IN (SELECT DISTINCT(place_id) FROM visit)");
        }
    };

    private static final Migration MIGRATION_16_17 = new Migration(16,17) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `wifi_scan` (`_id` INTEGER NOT NULL PRIMARY KEY, `time` INTEGER NOT NULL, `elapsed_time` INTEGER NOT NULL, `scan` TEXT NOT NULL)");
        }
    };

    private static final Migration MIGRATION_17_18 = new Migration(17,18) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DELETE FROM `osm_place`");
        }
    };


    public static synchronized void prepare(Context context) {
        if (INSTANCE == null)
            INSTANCE = create(context);
    }

    public static Database getInstance() {
        return INSTANCE;
    }

    @NonNull
    private static Database create(Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                Database.class,
                DB_NAME)
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5,MIGRATION_5_6,MIGRATION_6_7,MIGRATION_7_8,MIGRATION_8_9,MIGRATION_9_10,MIGRATION_10_11,MIGRATION_11_12, MIGRATION_12_13, MIGRATION_13_14, MIGRATION_14_15, MIGRATION_15_16, MIGRATION_16_17, MIGRATION_17_18)
                .build();
    }

    public abstract GeoLocationDao geoLocation();

    public abstract ActivityDao activity();

    public abstract AsistanEventDao asistan();

    public abstract PhoneEventDao phoneEvent();

    public abstract MobilityDao mobility();

    public abstract EventDao event();

    public abstract OSMDao openStreetMap();

    public abstract WiFiDao wifi();

    //si creo un dao añadirlo

}

