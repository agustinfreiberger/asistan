package ar.edu.unicen.isistan.asistan.tourwithme.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.views.asistan.movements.MovementsFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.places.MyPlacesFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.places.MyPlacesMapFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.places.PlacesFragment;

public class TourPlacesFragment extends Fragment {
    private TourPlacesFragment.OnFragmentInteractionListener listener;

    private MutableLiveData<ArrayList<Place>> places;

    public TourPlacesFragment() {
        this.places = new MutableLiveData<>();
    }

    public static TourPlacesFragment newInstance() {
        TourPlacesFragment fragment = new TourPlacesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static TourPlacesFragment newInstance(@NotNull MutableLiveData<ArrayList<Place>> places) {
        TourPlacesFragment fragment = new TourPlacesFragment();
        fragment.setPlaces(places);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places, container, false);
        this.init(view);
        return view;
    }

    private void init(final View view) {
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.places_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.navigation_map:
                        fragment = MyTourPlacesMapFragment.newInstance(places);
                        break;
                    default:
                        fragment = MyTourPlacesFragment.newInstance(places);
                        break;
                }

                FragmentManager fragmentManager = TourPlacesFragment.this.getChildFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_layout, fragment).commit();

                return true;
            }
        });


        Fragment fragment = MyTourPlacesFragment.newInstance(places);
        FragmentManager fragmentManager = TourPlacesFragment.this.getChildFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_layout, fragment).commit();


    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof MovementsFragment.OnFragmentInteractionListener) {
            listener = (TourPlacesFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    private void setPlaces(@NotNull MutableLiveData<ArrayList<Place>> places) {
        this.places.postValue(places.getValue());
    }

}
