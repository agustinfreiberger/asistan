package ar.edu.unicen.isistan.asistan.tourwithme;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.tourwithme.generators.TourGenerator;
import ar.edu.unicen.isistan.asistan.tourwithme.models.UserInfoDTO;
import ar.edu.unicen.isistan.asistan.tourwithme.views.GroupFragment;
import ar.edu.unicen.isistan.asistan.tourwithme.views.GroupListFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.movements.MovementsFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.places.MyPlacesFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.places.MyPlacesMapFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.places.PlacesFragment;

public class GroupActivity extends AppCompatActivity implements GroupFragment.OnFragmentInteractionListener, MovementsFragment.OnFragmentInteractionListener, MyPlacesMapFragment.OnFragmentInteractionListener, GroupListFragment.OnFragmentInteractionListener, PlacesFragment.OnFragmentInteractionListener, MyPlacesFragment.OnFragmentInteractionListener {

    private RequestQueue mQueue;
    private static ArrayList<UserInfoDTO> foundUsersList = new ArrayList<>();
    private GroupFragment fragment;
    private ProgressBar progress_Bar;
    private TourGenerator tourGenerator;
    private UserInfoDTO myUserInfoDTO;
    public static MutableLiveData<ArrayList<Place>> groupTourPlaces = new MutableLiveData<>();

    public GroupActivity() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        progress_Bar = findViewById(R.id.progressBar);
        progress_Bar.setVisibility(View.VISIBLE);
        myUserInfoDTO = new UserInfoDTO();

        Intent intent = getIntent();
        myUserInfoDTO.setId(UUID.fromString(intent.getStringExtra("guid")));
        myUserInfoDTO.setLocation(intent.getDoubleExtra("latitud", 0.0),intent.getDoubleExtra("longitud", 0.0) );

        tourGenerator = new TourGenerator();
        mQueue = Volley.newRequestQueue(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() {
        super.onStart();

        foundUsersList = getUsuariosCercanosYSimilares();

        groupTourPlaces.postValue(tourGenerator.GenerateGroupTour(foundUsersList));

        progress_Bar.setVisibility(View.GONE);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragment = GroupFragment.newInstance(foundUsersList);
        transaction.replace(R.id.group_frame_layout, fragment);
        transaction.commit();
    }
    private ArrayList<UserInfoDTO> getUsuariosCercanosYSimilares(){

        String url = String.format("https://tourwithmeapi.azurewebsites.net/Group/GetUsuariosCercanosYSimilares?Id=%s&lat=%s&longitud=%s", myUserInfoDTO.getId(),myUserInfoDTO.getLatitud(), myUserInfoDTO.getLongitud());

        JsonArrayRequest jsonObjectResponse = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if(foundUsersList.size() >0){
                        foundUsersList.clear();
                    }
                    for (int i = 0; i<response.length();i++){
                        Log.d("Leyendo response de: ",url +"---"+response.getString(i));
                        JSONObject userJson = response.getJSONObject(i);
                        UserInfoDTO foundUser = new UserInfoDTO(userJson.getString("name"),"",userJson.getInt("age"),userJson.getDouble("latitud"),userJson.getDouble("longitud"));

                        for (int index = 0; index<userJson.getJSONArray("preferences").length(); index++) {
                            JSONObject prefJson = userJson.getJSONArray("preferences").getJSONObject(index);
                            foundUser.addPreference(prefJson.getInt("placecategory"), (float) prefJson.getDouble("preference"));
                        }

                        foundUsersList.add(foundUser);
                    }
                    progress_Bar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Log.e("Error en request: ", url +"---"+error.getMessage());
            }
        });

        mQueue.add(jsonObjectResponse);

        return foundUsersList;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}





