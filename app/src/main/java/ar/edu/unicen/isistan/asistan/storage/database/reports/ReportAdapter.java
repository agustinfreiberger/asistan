package ar.edu.unicen.isistan.asistan.storage.database.reports;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class ReportAdapter implements JsonDeserializer<Report> {

    @Nullable
    public Report deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObj = json.getAsJsonObject();
        String type = jsonObj.get("type").getAsString();

        try {
            Report.ReportType reportType = Report.ReportType.valueOf(type);
            return context.deserialize(json, reportType.getReportTypeClass());
        } catch (Exception e) {
            return null;
        }
    }

}
