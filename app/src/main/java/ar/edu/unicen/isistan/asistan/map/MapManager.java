package ar.edu.unicen.isistan.asistan.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicMask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import org.jetbrains.annotations.NotNull;

import ar.edu.unicen.isistan.asistan.map.works.DownloadCitiesWork;
import ar.edu.unicen.isistan.asistan.map.works.DownloadCityWork;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMBusLine;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMDao;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMPlace;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OverpassAPI;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMArea;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMCity;
import ar.edu.unicen.isistan.asistan.tracker.Tracker;
import ar.edu.unicen.isistan.asistan.utils.geo.bound.Bound;

public class MapManager {

    private static final String PREFERENCES = "ar.edu.unicen.isistan.asistan.tracker.map";
    private static final String CENTER = "center";

    private static final int DOWNLOAD_RADIUS = 250000;



    private static MapManager INSTANCE = null;

    @NotNull
    private final Database database;
    @NotNull
    private final SharedPreferences preferences;
    @NotNull
    private final Coordinate center;
    @NotNull
    private final WorkManager workManager;
    @NotNull
    private final ArrayList<MapContext> mapContexts;

    public static synchronized void prepare(@NotNull Context context) {
        if (INSTANCE == null)
            INSTANCE = new MapManager(context);
    }

    @NotNull
    public static MapManager getInstance() {
        return INSTANCE;
    }

    private MapManager(@NotNull Context context) {
        this.database = Database.getInstance();
        this.workManager = WorkManager.getInstance(context.getApplicationContext());
        this.preferences = context.getApplicationContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        this.center = new Coordinate();
        this.mapContexts = new ArrayList<>();


        Gson gson = new Gson();
        SharedPreferences preferences = context.getSharedPreferences(MapManager.PREFERENCES, Context.MODE_PRIVATE);
        String json = preferences.getString(MapManager.CENTER, null);
        Coordinate coordinate = gson.fromJson(json, Coordinate.class);
        if (coordinate != null) {
            this.center.init(coordinate);
        }
    }

    private void enqueueDownloadCities(@NotNull Coordinate coordinate) {
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data downloadCitiesData = new Data.Builder()
                .putString(DownloadCitiesWork.KEY, new Gson().toJson(coordinate))
                .build();

        OneTimeWorkRequest downloadCitiesRequest = new OneTimeWorkRequest.Builder(DownloadCitiesWork.class)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
                .addTag(DownloadCitiesWork.TAG)
                .setInputData(downloadCitiesData)
                .build();

        this.workManager.enqueueUniqueWork(DownloadCitiesWork.NAME, ExistingWorkPolicy.REPLACE, downloadCitiesRequest);
    }

    public boolean downloadCities(@NotNull Context context, @NotNull Coordinate coordinate) {
        context = context.getApplicationContext();

        GeodesicData data = Geodesic.WGS84.Inverse(coordinate.getLatitude(), coordinate.getLongitude(), coordinate.getLatitude(), coordinate.getLongitude() + 1, GeodesicMask.DISTANCE);
        double long_degree_to_meters = data.s12;

        double lat_degrees = (double) DOWNLOAD_RADIUS / (double) Coordinate.DEGREE_TO_METERS;
        double long_degrees = (double) DOWNLOAD_RADIUS / long_degree_to_meters;
        double south = Math.max(coordinate.getLatitude() - lat_degrees, Coordinate.MIN_LAT);
        double west = Math.max(coordinate.getLongitude() - long_degrees, Coordinate.MIN_LNG);
        double north = Math.min(coordinate.getLatitude() + lat_degrees, Coordinate.MAX_LAT);
        double east = Math.min(coordinate.getLongitude() + long_degrees, Coordinate.MAX_LNG);

        List<OSMCity> cities = OverpassAPI.queryCities(south, west, north, east);

        if (cities == null)
            return false;

        this.database.openStreetMap().insert(cities.toArray(new OSMCity[0]));

        this.center.init(coordinate);
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putString(CENTER, new Gson().toJson(this.center));
        editor.apply();
        this.notifyListeners();
        Tracker.mapUpdate(context);

        return true;
    }

    public void enqueueDownloadCity(@NotNull OSMCity city) {
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build();

        Data downloadCityData = new Data.Builder()
                .putLong(DownloadCityWork.KEY, city.getId())
                .build();

        OneTimeWorkRequest downloadCityRequest = new OneTimeWorkRequest.Builder(DownloadCityWork.class)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
                .addTag(DownloadCityWork.TAG)
                .setInputData(downloadCityData)
                .build();

        this.workManager.enqueueUniqueWork(DownloadCityWork.NAME, ExistingWorkPolicy.REPLACE, downloadCityRequest);
    }

    public boolean downloadCity(@NotNull OSMCity city) {

        if (city.isDetailed())
            return true;

        Pair<List<OSMPlace>, List<OSMArea>> pair = OverpassAPI.queryPlaces(city);
        if (pair == null)
            return false;

        List<OSMBusLine> busLines = OverpassAPI.queryBuses(city);
        if (busLines == null)
            return false;

        city.setDetailed(true);

        this.database.openStreetMap().insert(pair.first.toArray(new OSMPlace[0]));
        this.database.openStreetMap().insert(pair.second.toArray(new OSMArea[0]));
        this.database.openStreetMap().insert(busLines.toArray(new OSMBusLine[0]));
        this.database.openStreetMap().update(city);

        this.notifyListeners();

        return true;
    }

    public void checkDownload(@NotNull Coordinate location) {
        if (this.center.isEmpty() || this.center.distance(location) >= DOWNLOAD_RADIUS / 2.0)
            this.enqueueDownloadCities(location);
    }

    @NotNull
    public List<OSMPlace> getOSMPlaces(@NotNull Coordinate location, double threshold) {
        ArrayList<OSMPlace> places = new ArrayList<>();

        List<OSMPlace> osmPlaces = Database.getInstance().openStreetMap().nearPlaces(location, threshold);
        for (OSMPlace place : osmPlaces) {
            if (place.getLocation().distance(location) < threshold)
                places.add(place);
        }

        return places;
    }

    public OSMArea getOSMArea(@NotNull Coordinate location) {
        List<OSMArea> osmAreas = Database.getInstance().openStreetMap().nearAreas(location, 0.0);

        OSMArea insideOf = null;
        double minSurface = Double.MAX_VALUE;

        for (OSMArea osmArea : osmAreas) {
            if (osmArea.getArea().getSimplified().contains(location)) {
                double surface = osmArea.getArea().getSurface();
                if (surface < minSurface) {
                    insideOf = osmArea;
                    minSurface = surface;
                }
            }
        }

        return insideOf;
    }

    public List<OSMBusLine> getOSMBusLines(@NotNull Bound bound) {
        return Database.getInstance().openStreetMap().nearBusLines(bound);
    }

    public void addListener(@NotNull MapContext mapContext) {
        this.mapContexts.add(mapContext);
    }

    public void notifyChanges() {
        this.notifyListeners();
    }

    private void notifyListeners() {
        for (MapContext mapContext: this.mapContexts)
            mapContext.notifyChanges();
    }

}
