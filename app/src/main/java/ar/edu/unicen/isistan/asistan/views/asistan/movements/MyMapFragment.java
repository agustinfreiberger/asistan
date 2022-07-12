package ar.edu.unicen.isistan.asistan.views.asistan.movements;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import android.content.Context;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.Movement;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.Configuration;
import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.ConfigurationManager;
import ar.edu.unicen.isistan.asistan.views.map.GoogleMapController;
import ar.edu.unicen.isistan.asistan.views.map.MapController;
import ar.edu.unicen.isistan.asistan.views.map.OsmMapController;

public class MyMapFragment extends Fragment implements OnMapReadyCallback {

    private OnFragmentInteractionListener listener;

    private MapController map;
    private MutableLiveData<ArrayList<Movement>> movements;
    private Observer<ArrayList<Movement>> observer;

    public MyMapFragment() {
        //this.selected = null;
    }

    private void setMovements(MutableLiveData<ArrayList<Movement>> movements) {
        this.movements = movements;
        this.observer = new Observer<ArrayList<Movement>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Movement> movements) {
                if (movements != null) {
                    MyMapFragment.this.refreshMap(movements);
                }
            }
        };
    }

    public static MyMapFragment newInstance(MutableLiveData<ArrayList<Movement>> movements) {
        MyMapFragment fragment = new MyMapFragment();
        fragment.setMovements(movements);
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

    private void refreshMap(@Nullable ArrayList<Movement> movements) {
        if (this.map != null && movements != null) {
            this.map.clear();

            ArrayList<Visit> visits = new ArrayList<>();
            for (Movement movement: movements) {
                if (movement.getType().equals(Movement.MovementType.VISIT))
                    visits.add((Visit) movement);
            }
            this.map.drawVisits(visits);
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
        if (googleMap != null && this.getContext() != null) {
            this.map = new GoogleMapController(this.getContext(),googleMap);
            this.refreshMap(this.movements.getValue());
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
            this.movements.removeObserver(this.observer);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.getActivity() != null)
            this.movements.observe(this.getActivity(),this.observer);
    }

}
