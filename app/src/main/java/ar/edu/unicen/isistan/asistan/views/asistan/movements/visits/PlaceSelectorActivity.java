package ar.edu.unicen.isistan.asistan.views.asistan.movements.visits;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSM;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Circle;
import ar.edu.unicen.isistan.asistan.views.asistan.places.edit.PlaceActivity;

public class PlaceSelectorActivity extends AppCompatActivity {

    private static final int PLACE_EDIT_REQUEST_CODE = 0;

    public static final String PARAMETER = "coordinate";
    public static final String PARAMETER_NEW_PLACE = "new_place";
    public static final String KEY = "place";

    private RecyclerView recyclerViewPlaces;
    private RecyclerView recyclerViewOSM;
    private Coordinate coordinate;
    private View loading;
    private View layout;
    private Place newPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_selector);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("Seleccion de lugar");

        this.recyclerViewPlaces = this.findViewById(R.id.places_list);
        this.recyclerViewPlaces.setLayoutManager(new GridLayoutManager(this,3));

        this.recyclerViewOSM = this.findViewById(R.id.osm_list);
        this.recyclerViewOSM.setLayoutManager(new GridLayoutManager(this,3));

        FloatingActionButton newPlaceButton = this.findViewById(R.id.new_place);

        if (getIntent().getExtras() != null) {
            Gson gson = new Gson();
            String json = getIntent().getExtras().getString(PARAMETER);
            this.coordinate = gson.fromJson(json, Coordinate.class);
            String newPlaceJson = getIntent().getExtras().getString(PARAMETER_NEW_PLACE);
            if (newPlaceJson != null) {
                this.newPlace = gson.fromJson(newPlaceJson, Place.class);
            } else {
                this.newPlace = new Place();
                this.newPlace.setArea(new Circle(PlaceSelectorActivity.this.coordinate,30));
            }

            newPlaceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PlaceSelectorActivity.this, PlaceActivity.class);
                    intent.putExtra(PlaceActivity.PARAMETER, new Gson().toJson(PlaceSelectorActivity.this.newPlace));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PlaceSelectorActivity.this.startActivityForResult(intent,PLACE_EDIT_REQUEST_CODE);
                }
            });
        } else {
            newPlaceButton.setVisibility(View.GONE);
        }

        this.layout = this.findViewById(R.id.layout);
        this.loading = this.findViewById(R.id.loading);
        this.loading.setVisibility(View.VISIBLE);
        this.layout.setVisibility(View.GONE);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Place> nearPlaces = Database.getInstance().mobility().near(PlaceSelectorActivity.this.coordinate,100D);
                final List<Place> places = new ArrayList<>();
                for (Place place: nearPlaces)
                    if (place.getName() != null && (PlaceSelectorActivity.this.newPlace == null || PlaceSelectorActivity.this.newPlace.getId() != place.getId()))
                        places.add(place);
                List<OSM> aux = Database.getInstance().openStreetMap().near(PlaceSelectorActivity.this.coordinate,100D);
                final List<OSM> osms = new ArrayList<>();
                for (OSM osm: aux)
                    if (osm.getName() != null && !alreadyContained(osm,nearPlaces))
                        osms.add(osm);

                PlaceSelectorActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PlaceSelectorAdapter placesAdapter = new PlaceSelectorAdapter(PlaceSelectorActivity.this, places);
                        PlaceSelectorActivity.this.recyclerViewPlaces.setAdapter(placesAdapter);
                        OSMSelectorAdapter osmAdapter = new OSMSelectorAdapter(PlaceSelectorActivity.this, osms);
                        PlaceSelectorActivity.this.recyclerViewOSM.setAdapter(osmAdapter);
                        PlaceSelectorActivity.this.layout.setVisibility(View.VISIBLE);
                        PlaceSelectorActivity.this.loading.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private boolean alreadyContained(OSM osm, List<Place> places) {
        for (Place place: places)
            if (osm.getId().equals(place.getOsmId()))
                return true;
        return false;
    }

    public void select(Place place) {
        Intent data = new Intent();
        data.putExtra(KEY, new Gson().toJson(place));
        setResult(RESULT_OK, data);
        finish();
    }

    public void select(OSM osm) {
        Place place = new Place();
        place.setArea(new Circle(this.coordinate,30));
        osm.export(place);
        place.setFixedLocation(true);
        Intent intent = new Intent(PlaceSelectorActivity.this, PlaceActivity.class);
        intent.putExtra(PlaceActivity.PARAMETER, new Gson().toJson(place));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PlaceSelectorActivity.this.startActivityForResult(intent,PLACE_EDIT_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PLACE_EDIT_REQUEST_CODE) {
                String json = data.getStringExtra(PlaceActivity.RESULT);
                Intent returnData = new Intent();
                returnData.putExtra(KEY,json);
                setResult(RESULT_OK, returnData);
                finish();
            }
        }
    }


}
