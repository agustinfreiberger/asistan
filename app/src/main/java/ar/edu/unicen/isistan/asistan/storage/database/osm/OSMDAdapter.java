package ar.edu.unicen.isistan.asistan.storage.database.osm;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class OSMDAdapter implements JsonDeserializer<OSM>, JsonSerializer<OSM> {

    public OSM deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObj = json.getAsJsonObject();
        String type = jsonObj.get("type").getAsString();
        OSM.OSMType osmType = OSM.OSMType.valueOf(type);
        return context.deserialize(json, osmType.getOSMClass());
    }

    @Override
    public JsonElement serialize(OSM src, Type typeOfSrc, JsonSerializationContext context) {
        String json = new Gson().toJson(src);
        return new JsonParser().parse(json);
    }
}