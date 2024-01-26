package ar.edu.unicen.isistan.asistan.tourwithme.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.views.asistan.places.edit.PlaceActivity;

public class MyTourPlacesFragment extends Fragment {
    public static final int PLACE_REQUEST_CODE = 1;

    private OnFragmentInteractionListener listener;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private ArrayList<Place> places;
    private Parcelable listState;

    public MyTourPlacesFragment() {
        this.places = new ArrayList<>();
        this.listState = null;
    }

    public static MyTourPlacesFragment newInstance() {
        MyTourPlacesFragment fragment = new MyTourPlacesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static MyTourPlacesFragment newInstance(@NotNull MutableLiveData<ArrayList<Place>> places) {
        MyTourPlacesFragment fragment = new MyTourPlacesFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_places, container, false);
        this.init(view);
        return view;
    }

    private void init(View view) {
        if (this.getContext() != null) {
            this.recyclerView = view.findViewById(R.id.places_list);

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getContext(),3);
            this.recyclerView.setLayoutManager(layoutManager);

        }
    }


    private void setPlaces(MutableLiveData<ArrayList<Place>> places) {

        places.observe(this, new Observer<ArrayList<Place>>() {
            @Override
            public void onChanged(ArrayList<Place> places) {
                if(places != null && places.size() > 0) {
                    MyTourPlacesFragment.this.places.clear();
                    for (Place place : places) {
                        if (place.getPlaceCategory() >= 0) {
                            MyTourPlacesFragment.this.places.add(place);
                        }
                    }
                    MyTourPlacesFragment.this.adapter = new MyTourPlacesAdapter(MyTourPlacesFragment.this, MyTourPlacesFragment.this.places);
                    if (MyTourPlacesFragment.this.getActivity() != null) {
                        MyTourPlacesFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyTourPlacesFragment.this.recyclerView.setAdapter(MyTourPlacesFragment.this.adapter);
                            }
                        });
                    }
                }
            }});
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
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

        if (this.recyclerView.getLayoutManager() != null)
            this.listState = this.recyclerView.getLayoutManager().onSaveInstanceState();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.listState != null && this.recyclerView.getLayoutManager() != null)
            this.recyclerView.getLayoutManager().onRestoreInstanceState(this.listState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PLACE_REQUEST_CODE) {
                String json = data.getStringExtra(PlaceActivity.RESULT);
                Place place = new Gson().fromJson(json,Place.class);

                for (int index = 0; index < this.places.size(); index++) {
                    if (this.places.get(index).equals(place)) {
                        this.places.get(index).load(place);
                        this.adapter.notifyItemChanged(index);
                        break;
                    }
                }

            }
        }
    }

}
