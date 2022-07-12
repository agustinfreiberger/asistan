package ar.edu.unicen.isistan.asistan.synchronizer.data.works;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import ar.edu.unicen.isistan.asistan.synchronizer.data.Synchronizer;

public class RawSyncWork extends Worker {

    public static final String NAME = "ar.edu.unicen.isistan.asistan-synchronizer-raw.data-queue";
    public static final String TAG = "ar.edu.unicen.isistan.asistan-synchronizer-raw.data";

    public RawSyncWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (Synchronizer.SyncRawData.syncRawData(getApplicationContext()))
            return Result.success();
        else
            return Result.retry();
    }

    public static class Builder {

        public synchronized static void createWork(@NonNull Context context) {
            context = context.getApplicationContext();

            Constraints syncConstraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.UNMETERED)
                    .build();

            OneTimeWorkRequest rawSyncWork = new OneTimeWorkRequest.Builder(RawSyncWork.class)
                    .setConstraints(syncConstraints)
                    .addTag(RawSyncWork.TAG)
                    .build();

            WorkManager.getInstance(context).enqueueUniqueWork(RawSyncWork.NAME, ExistingWorkPolicy.KEEP, rawSyncWork);
        }

        public synchronized static void cancel(@NonNull Context context) {
            context = context.getApplicationContext();
            WorkManager.getInstance(context).cancelAllWorkByTag(RawSyncWork.TAG);
        }
    }
}