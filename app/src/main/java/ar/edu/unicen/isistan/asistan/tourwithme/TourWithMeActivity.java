package ar.edu.unicen.isistan.asistan.tourwithme;

import static android.os.AsyncTask.Status.FINISHED;
import static android.os.AsyncTask.Status.RUNNING;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.views.asistan.MainActivity;
import ar.edu.unicen.isistan.asistan.views.asistan.live.LiveFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.places.MyPlacesMapFragment;
import io.opencensus.common.Scope;

public class TourWithMeActivity extends FragmentActivity implements MyPlacesMapFragment.OnFragmentInteractionListener {

    private FragmentManager fragmentManager;
    private ProfileGenerator profileGenerator;
    private TourGenerator tourGenerator;
    private Button btn_showProfile;
    private Button btn_showTour;
    public ProgressBar progress_Bar;

    private static MutableLiveData<ArrayList<Place>> tourPlaces = new
            MutableLiveData<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_tourwithme);
        btn_showProfile = findViewById(R.id.btn_showProfile);
        btn_showTour = findViewById(R.id.btn_showTour);
        progress_Bar = findViewById(R.id.progressBar);


        //TODO: meter ubicacion actual en tourPlaces para que inicie el mapa centrado

        fragmentManager = getSupportFragmentManager();
        tourGenerator = new TourGenerator();
        progress_Bar.setVisibility(View.VISIBLE);
        profileGenerator = new ProfileGenerator(this);

        btn_showTour.setOnClickListener(view ->
                showTourClick()
        );

        btn_showProfile.setOnClickListener(view ->
                showProfileClick()
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showProfileClick() {

        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment listFragment = UserPreferencesListFragment.newInstance(1, profileGenerator.getUserCategoryPreferencesString());
        ft.replace(R.id.myFrameLayout, listFragment);
        ft.commit();

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showTourClick(){
        tourGenerator.GenerateTour(profileGenerator.getUserCategoryPreferences()); //genero la lista de lugares a recomendar
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
