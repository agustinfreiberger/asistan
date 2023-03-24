package ar.edu.unicen.isistan.asistan.tourwithme;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import ar.edu.unicen.isistan.asistan.R;

public class ShowSuggestedLocations extends Activity{

    private ProfileGenerator profileGenerator;
    private TourGenerator tourGenerator;
    private ListView lv_suggestedLocations;
    private Button btn_getRecommendations;

    @RequiresApi(api = Build.VERSION_CODES.N)
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

}
