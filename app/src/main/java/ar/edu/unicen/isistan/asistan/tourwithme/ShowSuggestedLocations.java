package ar.edu.unicen.isistan.asistan.tourwithme;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.views.asistan.places.MyPlacesFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.places.MyPlacesMapFragment;

public class ShowSuggestedLocations extends FragmentActivity implements MyPlacesMapFragment.OnFragmentInteractionListener {

    private FragmentManager fragmentManager;
    private ProfileGenerator profileGenerator;
    private TourGenerator tourGenerator;
    private Button btn_showProfile;
    private Button btn_showTour;


    private static MutableLiveData<ArrayList<Place>> tourPlaces = new
            MutableLiveData<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_suggestedlocations);
        //btn_showProfile = findViewById(R.id.btn_showProfile);
        btn_showTour = findViewById(R.id.btn_showTour);

        //TODO: meter ubicacion actual en tourPlaces para que inicie el mapa centrado

        profileGenerator = new ProfileGenerator();
        tourGenerator = new TourGenerator();
        profileGenerator.execute();

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment mapFragment = MyPlacesMapFragment.newInstance(tourPlaces);
        ft.replace(R.id.myFrameLayout, mapFragment);
        ft.commit();


        btn_showTour.setOnClickListener(view ->
                showTourClick()
        );


    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showTourClick(){
        tourGenerator.GenerateTour(profileGenerator.getUserPoiPreferences()); //genero la lista de lugares a recomendar
        tourPlaces.postValue(tourGenerator.tourList);

        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment mapFragment = MyPlacesMapFragment.newInstance(tourPlaces);
        ft.replace(R.id.myFrameLayout, mapFragment);
        ft.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
