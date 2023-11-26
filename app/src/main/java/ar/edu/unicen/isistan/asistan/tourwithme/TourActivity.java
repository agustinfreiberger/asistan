package ar.edu.unicen.isistan.asistan.tourwithme;

import static ar.edu.unicen.isistan.asistan.tourwithme.TourWithMeActivity.tourPlaces;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.views.asistan.movements.MovementsFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.places.MyPlacesFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.places.MyPlacesMapFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.places.PlacesFragment;

public class TourActivity extends AppCompatActivity implements MovementsFragment.OnFragmentInteractionListener, PlacesFragment.OnFragmentInteractionListener, MyPlacesFragment.OnFragmentInteractionListener, MyPlacesMapFragment.OnFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            PlacesFragment fragment = PlacesFragment.newInstance(tourPlaces);
            transaction.replace(R.id.tour_frame_layout, fragment);
            transaction.commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}