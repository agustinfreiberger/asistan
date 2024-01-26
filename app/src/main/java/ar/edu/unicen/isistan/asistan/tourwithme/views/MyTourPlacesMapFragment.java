package ar.edu.unicen.isistan.asistan.tourwithme.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.Configuration;
import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.ConfigurationManager;
import ar.edu.unicen.isistan.asistan.views.asistan.places.MyPlacesMapFragment;
import ar.edu.unicen.isistan.asistan.views.map.GoogleMapController;
import ar.edu.unicen.isistan.asistan.views.map.MapController;
import ar.edu.unicen.isistan.asistan.views.map.OsmMapController;

public class MyTourPlacesMapFragment extends Fragment implements OnMapReadyCallback {
    private OnFragmentInteractionListener listener;

    @Nullable
    private MapController map;
    private MutableLiveData<ArrayList<Place>> places;
    private Observer<ArrayList<Place>> observer;

    private MyTourPlacesMapFragment() {
    }

    private void setPlaces(@NotNull MutableLiveData<ArrayList<Place>> places) {
        this.places = places;
        this.observer = new Observer<ArrayList<Place>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Place> places) {
                if (places != null) {
                    MyTourPlacesMapFragment.this.refreshMap(places);
                }
            }
        };
    }

    public static MyTourPlacesMapFragment newInstance(@NotNull MutableLiveData<ArrayList<Place>> places) {
        MyTourPlacesMapFragment fragment = new MyTourPlacesMapFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_map, container, false);
        this.init(view);
        return view;
    }

    private void init(View view) {
        Context context =this.getContext();
        if (context != null) {
            Configuration configuration = ConfigurationManager.load(context);
            if (configuration.getMapView() == MapController.Map.OPEN_STREET_MAP.getCode())
                this.map = OsmMapController.prepare(this.getContext(),view);
            else
                GoogleMapController.prepare(getChildFragmentManager(),this);
        }
    }

    private void refreshMap(@Nullable ArrayList<Place> places) {
        if (this.map != null && places != null) {
            this.map.clear();
            this.map.draw(places.toArray(new Place[0]));
        }
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (this.getContext() != null && googleMap != null) {
            this.map = new GoogleMapController(this.getContext(), googleMap);
            this.refreshMap(this.places.getValue());
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.getActivity() != null)
            this.places.removeObserver(this.observer);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.getActivity() != null)
            this.places.observe(this.getActivity(),this.observer);
    }

}
