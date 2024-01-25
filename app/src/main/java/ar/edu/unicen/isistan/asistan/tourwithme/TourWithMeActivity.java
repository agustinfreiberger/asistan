package ar.edu.unicen.isistan.asistan.tourwithme;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.User;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.UserManager;
import ar.edu.unicen.isistan.asistan.tourwithme.generators.ProfileGenerator;
import ar.edu.unicen.isistan.asistan.tourwithme.generators.TourGenerator;
import ar.edu.unicen.isistan.asistan.tourwithme.models.UserInfoDTO;
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

    public UserInfoDTO myUserInfoDTO;

    public Coordinate currentLocation;

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

    @Override
    public void onStart() {
        super.onStart();

        loadCurrentLocation();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadUserData();
                sendUserData();
            }
        }, 5000);

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
        intent.putExtra("guid", myUserInfoDTO.getId().toString());
        intent.putExtra("latitud", myUserInfoDTO.getLatitud());
        intent.putExtra("longitud", myUserInfoDTO.getLongitud());
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void loadUserData(){
        User myUser = UserManager.loadProfile(getApplicationContext());
        myUserInfoDTO = new UserInfoDTO(myUser.getName(), myUser.getLastName(), myUser.getAge(), -1,-1);
        myUserInfoDTO.setId(UUID.randomUUID());
        myUserInfoDTO.setLocation(currentLocation.getLatitude(), currentLocation.getLongitude());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            myUserInfoDTO.setPreferences(profileGenerator.getUserCategoryPreferences());
        }
    }

    private void loadCurrentLocation() {
            Database.getInstance().geoLocation().lastTrusted().observe(this, location -> {
                currentLocation = location.getCoordinate();
            });
    }

    private void sendUserData(){

        RequestQueue queue = Volley.newRequestQueue(this);

        Gson gson = new Gson();
        String json = gson.toJson(myUserInfoDTO);

        try {
            // Create a JSONObject from the JSON string
            JSONObject jsonObject = new JSONObject(json);

            // Create a JsonObjectRequest to send the JSON object to the server
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    "https://tourwithmeapi.azurewebsites.net/Group/CrearUsuario",
                    jsonObject,
                    response -> {
                        // Handle the server response
                        // You can perform any necessary operations here
                    },
                    error -> {
                        // Handle errors
                        error.printStackTrace();
                    }
            );

            // Add the request to the RequestQueue
            queue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
