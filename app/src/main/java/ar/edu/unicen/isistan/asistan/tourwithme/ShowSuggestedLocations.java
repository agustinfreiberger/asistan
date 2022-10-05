package ar.edu.unicen.isistan.asistan.tourwithme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;

import ar.edu.unicen.isistan.asistan.R;

public class ShowSuggestedLocations extends Activity {

    private ProfileGenerator profileGenerator;
    private ListView lv_suggestedLocations;
    private Button btn_getRecommendations;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_suggestedlocations);
        lv_suggestedLocations = findViewById(R.id.lv_suggestedLocations);
        btn_getRecommendations = findViewById(R.id.btn_getRecommendations);

        profileGenerator = new ProfileGenerator(); //al crearse ejecuta la generación del perfil
        profileGenerator.execute();

        btn_getRecommendations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lv_suggestedLocations.setAdapter((ListAdapter) profileGenerator.getUserPoiPreferences());
            }
        });
    }


}
