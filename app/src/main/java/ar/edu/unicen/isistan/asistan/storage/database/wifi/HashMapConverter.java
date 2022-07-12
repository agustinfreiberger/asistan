package ar.edu.unicen.isistan.asistan.storage.database.wifi;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class HashMapConverter {

    @TypeConverter
    public String toJson(HashMap<String,WiFiData> countryLang) {
        if (countryLang == null)
            return null;
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String,WiFiData>>() {}.getType();
        return gson.toJson(countryLang, type);
    }

    @TypeConverter
    public HashMap<String,WiFiData> fromJson(String countryLangString) {
        if (countryLangString == null)
            return null;
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String,WiFiData>>() {}.getType();
        return gson.fromJson(countryLangString, type);
    }

}
