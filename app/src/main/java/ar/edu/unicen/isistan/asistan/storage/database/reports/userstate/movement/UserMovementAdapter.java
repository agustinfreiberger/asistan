package ar.edu.unicen.isistan.asistan.storage.database.reports.userstate.movement;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

import ar.edu.unicen.isistan.asistan.storage.database.mobility.Movement;

public class UserMovementAdapter implements JsonDeserializer<UserMovement>, JsonSerializer<UserMovement> {

    @Nullable
    public UserMovement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObj = json.getAsJsonObject();
        String typeName = jsonObj.get("type").getAsString();

        try {
            Movement.MovementType type = Movement.MovementType.valueOf(typeName);
            if (type.equals(Movement.MovementType.COMMUTE))
                return context.deserialize(json, UserCommute.class);
            if (type.equals(Movement.MovementType.VISIT))
                return context.deserialize(json, UserVisit.class);
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    @Override
    @Nullable
    public JsonElement serialize(UserMovement src, Type typeOfSrc, JsonSerializationContext context) {
       try {
           if (src.getType().equals(Movement.MovementType.COMMUTE)) {
               UserCommute commute = (UserCommute) src;
               return JsonParser.parseString(new Gson().toJson(commute));
           }
           if (src.getType().equals(Movement.MovementType.VISIT)) {
               UserVisit visit = (UserVisit) src;
               return JsonParser.parseString(new Gson().toJson(visit));
           }

           return null;
       } catch (Exception e) {
           return null;
       }
    }
}
