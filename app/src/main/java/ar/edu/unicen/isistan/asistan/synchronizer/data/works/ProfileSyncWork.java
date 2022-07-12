package ar.edu.unicen.isistan.asistan.synchronizer.data.works;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.ListenableWorker;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import ar.edu.unicen.isistan.asistan.synchronizer.data.Synchronizer;

public class ProfileSyncWork extends Worker {

    public static final String NAME = "ar.edu.unicen.isistan.asistan-synchronizer-profile-queue";
    public static final String TAG = "ar.edu.unicen.isistan.asistan-synchronizer-profile";

    public ProfileSyncWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        if (Synchronizer.SyncProfile.syncProfile(getApplicationContext()))
            return ListenableWorker.Result.success();
        else
            return ListenableWorker.Result.retry();
    }

    public synchronized static void createWork(@NonNull Context context) {
        context = context.getApplicationContext();

        Constraints syncConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest oneTimeSyncWork = new OneTimeWorkRequest.Builder(ProfileSyncWork.class)
                .setConstraints(syncConstraints)
                .addTag(ProfileSyncWork.TAG)
                .build();

        WorkManager.getInstance(context).enqueueUniqueWork(ProfileSyncWork.NAME, ExistingWorkPolicy.REPLACE, oneTimeSyncWork);
    }

}
