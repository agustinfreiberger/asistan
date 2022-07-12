package ar.edu.unicen.isistan.asistan.utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class AsyncBroadcastReceiver extends BroadcastReceiver {


    private static final ExecutorService executorService;
    static { executorService = Executors.newCachedThreadPool(); }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context != null && intent != null) {
            PendingResult pendingResult = this.goAsync();

            Task<Object> task = Tasks.call(executorService,() -> {
                this.process(context, intent);
                return true;
            });

            Tasks.call(executorService, () -> {
                try {
                    Tasks.await(task,5, TimeUnit.SECONDS);
                    return true;
                } catch (Exception ignored) {
                    return true;
                } finally {
                    pendingResult.finish();
                }
            });

        }
    }

    public abstract void process(@NonNull Context context, @NonNull Intent intent);


}
