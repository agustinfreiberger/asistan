package ar.edu.unicen.isistan.asistan.storage.database.sensor;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class FloatArrayConverter {

    @TypeConverter
    public String toJson(float[] values) {
        if (values == null)
            return (null);
        Gson gson = new Gson();
        Type type = new TypeToken<float[]>() {}.getType();
        return gson.toJson(values, type);
    }

    @TypeConverter
    public float[] fromJson(String countryLangString) {
        if (countryLangString == null)
            return null;
        Gson gson = new Gson();
        Type type = new TypeToken<float[]>() {}.getType();
        return gson.fromJson(countryLangString, type);
    }

}
