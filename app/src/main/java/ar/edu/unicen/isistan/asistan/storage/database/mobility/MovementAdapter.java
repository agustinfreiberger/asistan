package ar.edu.unicen.isistan.asistan.storage.database.mobility;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Commute;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;

public class MovementAdapter implements JsonDeserializer<Movement> {

    @Nullable
    public Movement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObj = json.getAsJsonObject();
        String typeName = jsonObj.get("type").getAsString();

        try {
            Movement.MovementType type = Movement.MovementType.valueOf(typeName);
            if (type.equals(Movement.MovementType.COMMUTE))
                return context.deserialize(json, Commute.class);
            if (type.equals(Movement.MovementType.VISIT))
                return context.deserialize(json, Visit.class);
        } catch (Exception e) {
            return null;
        }

        return null;
    }

}
