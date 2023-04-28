package ar.edu.unicen.isistan.asistan.tourwithme;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import com.google.gson.Gson;
import java.util.ArrayList;
import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.views.asistan.places.MyPlacesMapFragment;

public class TourWithMeActivity extends FragmentActivity implements MyPlacesMapFragment.OnFragmentInteractionListener {

    private FragmentManager fragmentManager;
    private ProfileGenerator profileGenerator;
    private TourGenerator tourGenerator;
    private Button btn_showProfile;
    private Button btn_showTour;
    private Button btn_showGroup;
    public ProgressBar progress_Bar;


    protected static MutableLiveData<ArrayList<Place>> tourPlaces = new
            MutableLiveData<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tourwithme);
        btn_showProfile = findViewById(R.id.btn_showProfile);
        btn_showTour = findViewById(R.id.btn_showTour);
        btn_showGroup = findViewById(R.id.btn_showGroup);
        progress_Bar = findViewById(R.id.progressBar);


        fragmentManager = getSupportFragmentManager();
        tourGenerator = new TourGenerator();

        progress_Bar.setVisibility(View.VISIBLE);
        profileGenerator = new ProfileGenerator(progress_Bar);

        btn_showTour.setOnClickListener(view ->
                showTourClick()
        );

        btn_showProfile.setOnClickListener(view ->
                showProfileClick()
        );

        btn_showGroup.setOnClickListener(view ->
                showGroupClick()
        );

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showProfileClick() {
        FragmentTransaction ft = fragmentManager.beginTransaction();

        Fragment listFragment = UserPreferencesListFragment.newInstance(1, profileGenerator.getUserCategoryPreferences());
        ft.replace(R.id.myFrameLayout, listFragment);
        ft.commit();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showTourClick(){
        progress_Bar.setVisibility(View.VISIBLE);

        tourGenerator.GenerateTour(profileGenerator.getUserCategoryPreferences()); //genero la lista de lugares a recomendar
        tourPlaces.postValue(tourGenerator.tourList);

        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment mapFragment = MyPlacesMapFragment.newInstance(tourPlaces);
        ft.replace(R.id.myFrameLayout, mapFragment);
        ft.commit();
        progress_Bar.setVisibility(View.GONE);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showGroupClick() {
        Intent intent = new Intent(this, GroupActivity.class);
        String arrayAsString = new Gson().toJson(profileGenerator.getUserCategoryPreferences());
        intent.putExtra("tourwithme.userCategoryPreferences", arrayAsString);
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
