package ar.edu.unicen.isistan.asistan.tourwithme;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.tourwithme.generators.ProfileGenerator;
import ar.edu.unicen.isistan.asistan.tourwithme.generators.TourGenerator;
import ar.edu.unicen.isistan.asistan.tourwithme.views.UserPreferencesListFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.places.MyPlacesMapFragment;

public class TourWithMeActivity extends AppCompatActivity implements MyPlacesMapFragment.OnFragmentInteractionListener {

    private FragmentManager fragmentManager;
    private ProfileGenerator profileGenerator;
    private TourGenerator tourGenerator;
    private ConstraintLayout textLayout;
    public ProgressBar progress_Bar;
    public static MutableLiveData<ArrayList<Place>> tourPlaces = new
            MutableLiveData<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tourwithme);
        textLayout = findViewById(R.id.text_twm_layout);

        Button btn_showProfile = findViewById(R.id.btn_showProfile);
        Button btn_showTour = findViewById(R.id.btn_showTour);
        Button btn_showGroup = findViewById(R.id.btn_showGroup);
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
        textLayout.setVisibility(View.GONE);

        FragmentTransaction ft = fragmentManager.beginTransaction();

        Fragment listFragment = UserPreferencesListFragment.newInstance(1, profileGenerator.getUserCategoryPreferences());
        ft.replace(R.id.myFrameLayout, listFragment);
        ft.commit();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showTourClick(){
        progress_Bar.setVisibility(View.VISIBLE);

        tourPlaces.postValue(tourGenerator.GenerateTour(profileGenerator.getUserCategoryPreferences()));

        Intent intent = new Intent(this, TourActivity.class);
        intent.putExtra("tourwithme.tourPlaces", tourPlaces.getValue());
        startActivity(intent);

        progress_Bar.setVisibility(View.GONE);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showGroupClick() {
        Intent intent = new Intent(this, GroupActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
