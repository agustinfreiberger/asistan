package ar.edu.unicen.isistan.asistan.map.works;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import ar.edu.unicen.isistan.asistan.map.MapManager;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.asistan.AsistanEvent;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMCity;

public class DownloadCityWork extends Worker {

    private static final String CLASS_TAG = "DownloadCityWork";

    private static final String DOWNLOADING = "DOWNLOADING";
    private static final String DOWNLOAD_ERROR = "DOWNLOAD_ERROR";
    private static final String DOWNLOAD_COMPLETE = "DOWNLOAD_COMPLETE";

    public static final String NAME = "ar.edu.unicen.isistan.asistan-map-download.city.work-queue";
    public static final String TAG = "ar.edu.unicen.isistan.asistan-map-download.city.work";
    public static final String KEY = "cityId";

    public DownloadCityWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        long id = this.getInputData().getLong(KEY,-1);

        if (id == -1)
            return Result.failure();

        Database database = Database.getInstance();
        OSMCity city = database.openStreetMap().select(id);

        if (city == null)
            return Result.failure();

        if (city.isDetailed())
            return Result.success();

        MapManager mapManager = MapManager.getInstance();

        database.asistan().insert(new AsistanEvent(CLASS_TAG, DOWNLOADING));

        if (mapManager.downloadCity(city)) {
            database.asistan().insert(new AsistanEvent(CLASS_TAG, DOWNLOAD_COMPLETE));
            return Result.success();
        } else {
            database.asistan().insert(new AsistanEvent(CLASS_TAG, DOWNLOAD_ERROR));
            return Result.retry();
        }

    }

}
