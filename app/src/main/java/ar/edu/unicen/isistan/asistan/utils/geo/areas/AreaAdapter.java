package ar.edu.unicen.isistan.asistan.utils.geo.areas;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class AreaAdapter implements JsonDeserializer<Area>, JsonSerializer<Area> {

    public Area deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObj = json.getAsJsonObject();
        String type = jsonObj.get("type").getAsString();
        Area.AreaType areaType = Area.AreaType.valueOf(type);
        return context.deserialize(json, areaType.getAreaClass());
    }

    @Override
    public JsonElement serialize(Area src, Type typeOfSrc, JsonSerializationContext context) {
        Area.AreaType areaType = src.getType();
        return context.serialize(src,areaType.getAreaClass());
    }

}