package ar.edu.unicen.isistan.asistan.storage.database.mobility.labels;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import androidx.room.TypeConverter;

public class LabelListConverter {

    @TypeConverter
    public String toJson(ArrayList<Integer> countryLang) {
        if (countryLang == null)
            return (null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
        return gson.toJson(countryLang, type);
    }

    @TypeConverter
    public ArrayList<Integer> fromJson(String countryLangString) {
        if (countryLangString == null)
            return null;
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
        return gson.fromJson(countryLangString, type);
    }

}
