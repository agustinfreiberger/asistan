package ar.edu.unicen.isistan.asistan.storage.database.activity;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ActivityConverter {

    private static final Gson GSON = new Gson();

    @TypeConverter
    public List<Activity.ProbableActivity> fromJson(String json) {
        if (json == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Activity.ProbableActivity>>(){}.getType();

        return GSON.fromJson(json, listType);
    }

    @TypeConverter
    public String toJson(List<Activity.ProbableActivity> list) {
        return GSON.toJson(list);
    }

}
