package ar.edu.unicen.isistan.asistan.tourwithme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.UserManager;

public class ShowSuggestedLocations extends Activity{

    private ProfileGenerator profileGenerator;
    private ListView lv_suggestedLocations;
    private Button btn_getRecommendations;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_suggestedlocations);
        lv_suggestedLocations = findViewById(R.id.lv_suggestedLocations);
        btn_getRecommendations = findViewById(R.id.btn_getRecommendations);

        profileGenerator = new ProfileGenerator();
        profileGenerator.execute();

        btn_getRecommendations.setOnClickListener(view ->
                lv_suggestedLocations.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, profileGenerator.getUserPoiPreferences()))
        );
    }

    //Método que toma la lista de preferencias de pois y genera la recomendación
    private void generateTour() {
        ArrayList<String> aux = new ArrayList<>();
    }

    private void savePreferences(){
        String uri = "mongodb+srv://mongouser:slipknot-123@cluster0.bjwlp.mongodb.net/?retryWrites=true&w=majority";

        try (MongoClient mongoClient = MongoClients.create(uri))
        {
            MongoDatabase database = mongoClient.getDatabase("AsistanDB");
            Document document = new Document();
            document.append("name", UserManager.loadProfile(this).getName());
            document.append("preferences", profileGenerator.getUserPoiPreferences());
            //Inserting the document into the collection
            database.getCollection("UserPreferences").insertOne(document);
            System.out.println("Document inserted successfully");
        }
        catch(Exception e)
        {

        }
    }
}
