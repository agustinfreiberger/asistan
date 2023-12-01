package ar.edu.unicen.isistan.asistan.tourwithme.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.tourwithme.models.UserInfoDTO;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Area;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Circle;
import ar.edu.unicen.isistan.asistan.views.asistan.movements.MovementsFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.places.MyPlacesMapFragment;

public class GroupFragment extends Fragment {

    private ArrayList<UserInfoDTO> foundUsersList = new ArrayList<>();

    private GroupFragment.OnFragmentInteractionListener listener;

    private MutableLiveData<ArrayList<Place>> places = new MutableLiveData<>();

    public GroupFragment() {
    }

    public GroupFragment(ArrayList<UserInfoDTO> usuariosCercanos) {
        foundUsersList = usuariosCercanos;
    }

    public static GroupFragment newInstance(@NotNull ArrayList<UserInfoDTO> usuariosCercanos) {
        GroupFragment fragment = new GroupFragment(usuariosCercanos);
        fragment.setPlaces(usuariosCercanos);
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
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        this.init(view);
        return view;
    }

    private void init(final View view) {
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.group_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.navigation_map:
                        fragment = MyPlacesMapFragment.newInstance(places);
                        break;
                    default:
                        fragment = GroupListFragment.newInstance(foundUsersList);
                        break;
                }

                FragmentManager fragmentManager = GroupFragment.this.getChildFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_layout, fragment).commit();

                return true;
            }
        });


        Fragment fragment = GroupListFragment.newInstance(foundUsersList);
        FragmentManager fragmentManager = GroupFragment.this.getChildFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_layout, fragment).commit();

    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof MovementsFragment.OnFragmentInteractionListener) {
            listener = (GroupFragment.OnFragmentInteractionListener) context;
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

    //Creo la ubicación de cada usuario para mostrarlas en el mapa
    private void setPlaces(@NotNull ArrayList<UserInfoDTO> usuariosCercanos) {

        Place placeUsuario;
        Area areaUsuario;
        ArrayList<Place> usuariosUbicacion = new ArrayList<>();

        for (UserInfoDTO user: usuariosCercanos) {
            placeUsuario = new Place();
            areaUsuario = new Circle(user.getLocation(),1);
            placeUsuario.setDescription(user.getName() +" "+user.getLastName());
            placeUsuario.setArea(areaUsuario);
            placeUsuario.setPlaceCategory(1099);
            usuariosUbicacion.add(placeUsuario);
        }

        places.postValue(usuariosUbicacion);
    }

}