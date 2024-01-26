package ar.edu.unicen.isistan.asistan.tourwithme;

import static ar.edu.unicen.isistan.asistan.tourwithme.TourWithMeActivity.tourPlaces;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.tourwithme.views.MyTourPlacesFragment;
import ar.edu.unicen.isistan.asistan.tourwithme.views.MyTourPlacesMapFragment;
import ar.edu.unicen.isistan.asistan.tourwithme.views.TourPlacesFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.movements.MovementsFragment;

public class TourActivity extends AppCompatActivity implements MovementsFragment.OnFragmentInteractionListener, TourPlacesFragment.OnFragmentInteractionListener, MyTourPlacesFragment.OnFragmentInteractionListener, MyTourPlacesMapFragment.OnFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            TourPlacesFragment fragment = TourPlacesFragment.newInstance(tourPlaces);
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