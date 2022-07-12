package ar.edu.unicen.isistan.asistan.map.works;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;

import ar.edu.unicen.isistan.asistan.map.MapManager;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.asistan.AsistanEvent;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;

public class DownloadCitiesWork extends Worker {

    private static final String CLASS_TAG = "DownloadCitiesWork";

    private static final String DOWNLOADING = "DOWNLOADING";
    private static final String DOWNLOAD_ERROR = "DOWNLOAD_ERROR";
    private static final String DOWNLOAD_COMPLETE = "DOWNLOAD_COMPLETE";

    public static final String NAME = "ar.edu.unicen.isistan.asistan-map-download.cities.work-queue";
    public static final String TAG = "ar.edu.unicen.isistan.asistan-map-download.cities.work";
    public static final String KEY = "coordinate";

    public DownloadCitiesWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        String json = this.getInputData().getString(KEY);
        Coordinate coordinate = new Gson().fromJson(json,Coordinate.class);

        if (coordinate != null) {
            MapManager mapManager = MapManager.getInstance();

            Database database = Database.getInstance();
            database.asistan().insert(new AsistanEvent(CLASS_TAG, DOWNLOADING));

            if (mapManager.downloadCities(this.getApplicationContext(), coordinate)) {
                database.asistan().insert(new AsistanEvent(CLASS_TAG, DOWNLOAD_COMPLETE));
                return Result.success();
            } else {
                database.asistan().insert(new AsistanEvent(CLASS_TAG, DOWNLOAD_ERROR));
                return Result.retry();
            }
        } else {
            return Result.failure();
        }

    }
}
