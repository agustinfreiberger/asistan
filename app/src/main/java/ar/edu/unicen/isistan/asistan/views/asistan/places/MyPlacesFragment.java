package ar.edu.unicen.isistan.asistan.views.asistan.places;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.views.asistan.places.edit.PlaceActivity;

public class MyPlacesFragment extends Fragment {

    public static final int PLACE_REQUEST_CODE = 1;

    private OnFragmentInteractionListener listener;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private ArrayList<Place> places;
    private Parcelable listState;

    public MyPlacesFragment() {
        this.places = new ArrayList<>();
        this.listState = null;
    }

    public static MyPlacesFragment newInstance() {
        MyPlacesFragment fragment = new MyPlacesFragment();
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

            this.load();
        }
    }

    private void load() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (MyPlacesFragment.this.getContext() != null) {
                    MyPlacesFragment.this.places.clear();

                    List<Place> everyPlace = Database.getInstance().mobility().allPlaces();
                    for (Place place: everyPlace) {
                        if (place.getPlaceCategory() >= 0) {
                            MyPlacesFragment.this.places.add(place);
                        }
                    }
                    MyPlacesFragment.this.adapter = new MyPlacesAdapter(MyPlacesFragment.this, MyPlacesFragment.this.places);
                    if (MyPlacesFragment.this.getActivity() != null) {
                        MyPlacesFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyPlacesFragment.this.recyclerView.setAdapter(MyPlacesFragment.this.adapter);
                            }
                        });
                    }
                }
            }
        });
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
        //this.loadProfile();
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
