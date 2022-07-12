package ar.edu.unicen.isistan.asistan.synchronizer.reports.userstate;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;

import ar.edu.unicen.isistan.asistan.storage.database.reports.userstate.UserState;
import ar.edu.unicen.isistan.asistan.synchronizer.reports.Reporter;

public class UserStateReportWork extends Worker {

    public static final String TAG = "ar.edu.unicen.isistan.asistan-reporter-user.state";
    public static final String NAME = "ar.edu.unicen.isistan.asistan-reporter-user.state-queue";

    public UserStateReportWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            UserState userState = UserStateReporter.get(this.getApplicationContext());

            if (userState == null || Reporter.report(getApplicationContext(), userState))
                return Result.success();

            return Result.retry();
        } catch (Exception e) {
            return Result.failure();
        }
    }

}
