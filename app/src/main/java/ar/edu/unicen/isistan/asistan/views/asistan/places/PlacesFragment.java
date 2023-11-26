package ar.edu.unicen.isistan.asistan.views.asistan.places;

import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.views.asistan.movements.MovementsFragment;

public class PlacesFragment extends Fragment {

    private PlacesFragment.OnFragmentInteractionListener listener;

    private MutableLiveData<ArrayList<Place>> places;

    public PlacesFragment() {
        this.places = new MutableLiveData<>();
    }

    public static PlacesFragment newInstance() {
        PlacesFragment fragment = new PlacesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static PlacesFragment newInstance(@NotNull MutableLiveData<ArrayList<Place>> places) {
        PlacesFragment fragment = new PlacesFragment();
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
                        fragment = MyPlacesMapFragment.newInstance(places);
                        break;
                    default:
                        fragment = MyPlacesFragment.newInstance();
                        break;
                }

                FragmentManager fragmentManager = PlacesFragment.this.getChildFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_layout, fragment).commit();

                return true;
            }
        });


        Fragment fragment = MyPlacesFragment.newInstance(places);
        FragmentManager fragmentManager = PlacesFragment.this.getChildFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_layout, fragment).commit();

        this.update();

        if(this.places.getValue() != null && this.places.getValue().size() == 0){
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    PlacesFragment.this.loadData();
                }
            });
        }

    }

    private void update() {

    }

    private void loadData() {
        Database database = Database.getInstance();
        ArrayList<Place> places = new ArrayList<>(database.mobility().allPlaces());
        this.places.postValue(places);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof MovementsFragment.OnFragmentInteractionListener) {
            listener = (PlacesFragment.OnFragmentInteractionListener) context;
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
