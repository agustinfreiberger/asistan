package ar.edu.unicen.isistan.asistan.tourwithme;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.annotation.Nullable;
import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.R;

public class ShowSuggestedLocations extends Activity{

    private ProfileGenerator profileGenerator;
    private ListView lv_suggestedLocations;
    private Button btn_getRecommendations;
    private Button btn_savePreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_suggestedlocations);
        lv_suggestedLocations = findViewById(R.id.lv_suggestedLocations);
        btn_getRecommendations = findViewById(R.id.btn_getRecommendations);
        btn_savePreferences = findViewById(R.id.btn_savePreferences);

        profileGenerator = new ProfileGenerator();
        profileGenerator.execute();

        btn_getRecommendations.setOnClickListener(view ->
                lv_suggestedLocations.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, profileGenerator.getUserPoiPreferences()))
        );

        btn_savePreferences.setOnClickListener(view ->
                getTouristsAround()
        );
    }

    //Método que toma la lista de preferencias de pois y genera la recomendación
    private void generateTour() {
        ArrayList<String> aux = new ArrayList<>();
    }

    private void getTouristsAround(){

    }
}
