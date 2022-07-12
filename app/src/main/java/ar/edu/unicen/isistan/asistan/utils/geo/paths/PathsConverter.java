package ar.edu.unicen.isistan.asistan.utils.geo.paths;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PathsConverter {

    @TypeConverter
    public String toJson(ArrayList<Path> countryLang) {
        if (countryLang == null)
            return (null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Path>>() {}.getType();
        return gson.toJson(countryLang, type);
    }

    @TypeConverter
    public ArrayList<Path> fromJson(String countryLangString) {
        if (countryLangString == null)
            return null;
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Path>>() {}.getType();
        return gson.fromJson(countryLangString, type);
    }

}
