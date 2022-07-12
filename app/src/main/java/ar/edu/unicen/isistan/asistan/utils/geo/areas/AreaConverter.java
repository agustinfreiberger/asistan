package ar.edu.unicen.isistan.asistan.utils.geo.areas;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

public class AreaConverter {

    @TypeConverter
    public String toJson(Area area) {
        if (area == null)
            return null;
        return new Gson().toJson(area);
    }

    @TypeConverter
    public Area fromJson(String json) {
        if (json == null)
            return null;
        return new Gson().fromJson(json, Area.class);
    }

}
