package ar.edu.unicen.isistan.asistan.synchronizer.reports;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import ar.edu.unicen.isistan.asistan.storage.database.reports.Report;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.User;
import ar.edu.unicen.isistan.asistan.synchronizer.AsistanAPI;

public class Reporter {

    public static boolean report(@NotNull Context context, @NotNull Report report) {
        context = context.getApplicationContext();

        User user = AsistanAPI.getUser(context);
        if (user == null)
            return false;
        String token = user.getToken(context);
        return (token != null && AsistanAPI.postReport(report,token));
    }

}
