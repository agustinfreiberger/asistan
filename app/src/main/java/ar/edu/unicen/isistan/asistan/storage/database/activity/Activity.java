package ar.edu.unicen.isistan.asistan.storage.database.activity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import ar.edu.unicen.isistan.asistan.utils.queues.Reusable;

@Entity(tableName = Activity.TABLE_NAME)
public class Activity implements Reusable<Activity> {

    public static final String TABLE_NAME = "activity";

    private final static int NO_DATA = -1;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private long id;
    @ColumnInfo(name = "time")
    private long time;
    @ColumnInfo(name = "elapsed_time")
    private long elapsedTime;
    @ColumnInfo(name = "activities")
    @TypeConverters(ActivityConverter.class)
    private final List<ProbableActivity> activities;

    public Activity() {
        this.activities = new ArrayList<>();
        this.init();
    }

    public Activity(@NotNull ActivityRecognitionResult recognition) {
        this();
        this.time = recognition.getTime();
        this.elapsedTime = recognition.getElapsedRealtimeMillis();
        for (DetectedActivity activity: recognition.getProbableActivities())
            this.activities.add(new ProbableActivity(activity));
    }

    @Override
    public void init() {
        this.id = 0;
        this.time = NO_DATA;
        this.elapsedTime = NO_DATA;
        this.activities.clear();
    }

    @Override
    public void init(@Nullable Activity activity) {
        if (activity != null) {
            this.id = activity.getId();
            this.time = activity.getTime();
            this.elapsedTime = activity.getElapsedTime();
            this.activities.clear();
            this.activities.addAll(activity.getActivities());
        } else {
            this.init();
        }
    }

    public void init(@NonNull ActivityRecognitionResult recognition) {
        this.init();
        this.time = recognition.getTime();
        this.elapsedTime = recognition.getElapsedRealtimeMillis();
        for (DetectedActivity activity: recognition.getProbableActivities())
            this.activities.add(new ProbableActivity(activity));
    }

    @Override
    public boolean isEmpty() {
        return this.getTime() == NO_DATA;
    }

    @Override
    @NotNull
    public Activity copy() {
        Activity activity = new Activity();
        activity.init(this);
        return activity;
    }

    @NotNull
    private static String getName(int type) {
        switch (type) {
            case DetectedActivity.IN_VEHICLE:
                return "IN_VEHICLE";
            case DetectedActivity.ON_BICYCLE:
                return "ON_BICYCLE";
            case DetectedActivity.ON_FOOT:
                return "ON_FOOT";
            case DetectedActivity.STILL:
                return "STILL";
            case DetectedActivity.TILTING:
                return "TILTING";
            case DetectedActivity.WALKING:
                return "WALKING";
            case DetectedActivity.RUNNING:
                return "RUNNING";
            default:
                return "UNKNOWN";
        }
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @NotNull
    public List<ProbableActivity> getActivities() {
        return activities;
    }

    public void setActivities(@NotNull List<ProbableActivity> activities) {
        this.activities.clear();
        this.activities.addAll(activities);
    }

    public int getType() {
        if (this.activities.get(0).getType() == DetectedActivity.ON_FOOT) {
            for (int index = 1; index < this.activities.size(); index++) {
                int type = this.activities.get(index).getType();
                if (type == DetectedActivity.WALKING || type == DetectedActivity.RUNNING)
                    return type;
            }
            return this.activities.get(0).getType();
        } else {
            return this.activities.get(0).getType();
        }
    }

    public int getConfidence() {
        int type = this.getType();
        for (int index = 0; index < this.activities.size(); index++) {
            if (this.activities.get(index).getType() == type)
                return this.activities.get(index).getConfidence();
        }
        return this.activities.get(0).getConfidence();
    }

    @NotNull
    public String getName() {
        return this.activities.get(0).getName();
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public class ProbableActivity {

        private int type;
        @NotNull
        private String name;
        private int confidence;

        private ProbableActivity(@NotNull DetectedActivity activity) {
            this.type = activity.getType();
            this.name = Activity.getName(this.type);
            this.confidence = activity.getConfidence();
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        @NotNull
        public String getName() {
            return name;
        }

        public void setName(@NotNull String name) {
            this.name = name;
        }

        public int getConfidence() {
            return confidence;
        }

        public void setConfidence(int confidence) {
            this.confidence = confidence;
        }
    }

    public boolean equals(Object object) {
        if (!(object instanceof Activity))
            return false;
        if (object == this)
            return true;
        Activity aux = (Activity) object;
        return (aux.getType() == this.getType());
    }

    @NotNull
    public String toString() {
        return new Gson().toJson(this);
    }
}
