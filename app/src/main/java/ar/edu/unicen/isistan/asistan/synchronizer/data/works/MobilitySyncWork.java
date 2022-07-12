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

public class MobilitySyncWork extends Worker {

    public static final String NAME = "ar.edu.unicen.isistan.asistan-synchronizer-mobility.data-queue";
    public static final String TAG = "ar.edu.unicen.isistan.asistan-synchronizer-mobility.data";

    public MobilitySyncWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (Synchronizer.SyncMobility.synchMobility(getApplicationContext()))
            return Result.success();
        else
            return Result.retry();
    }

    public static class Builder {

        public static synchronized void createWork(@NonNull Context context) {
            context = context.getApplicationContext();

            Constraints syncConstraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            OneTimeWorkRequest oneTimeSyncWork = new OneTimeWorkRequest.Builder(MobilitySyncWork.class)
                    .setConstraints(syncConstraints)
                    .addTag(MobilitySyncWork.TAG)
                    .build();

            WorkManager.getInstance(context).enqueueUniqueWork(MobilitySyncWork.NAME, ExistingWorkPolicy.KEEP, oneTimeSyncWork);
        }

        public static synchronized void cancel(@NonNull Context context) {
            context = context.getApplicationContext();
            WorkManager.getInstance(context).cancelAllWorkByTag(MobilitySyncWork.TAG);
        }
    }

}