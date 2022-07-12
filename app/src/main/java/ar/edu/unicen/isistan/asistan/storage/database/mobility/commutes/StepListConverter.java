package ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class StepListConverter {

    @TypeConverter
    @Nullable
    public String toJson(ArrayList<Step> countryLang) {
        if (countryLang == null)
            return null;
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Step>>() {}.getType();
        return gson.toJson(countryLang, type);
    }

    @TypeConverter
    @Nullable
    public ArrayList<Step> fromJson(String countryLangString) {
        if (countryLangString == null)
            return null;
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Step>>() {}.getType();
        return gson.fromJson(countryLangString, type);
    }

}
