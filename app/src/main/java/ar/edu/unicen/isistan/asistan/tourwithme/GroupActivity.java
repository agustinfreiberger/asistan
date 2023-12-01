package ar.edu.unicen.isistan.asistan.tourwithme;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.GeoLocation;
import ar.edu.unicen.isistan.asistan.storage.database.reports.userstate.UserState;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.User;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.UserManager;
import ar.edu.unicen.isistan.asistan.synchronizer.reports.userstate.UserStateReporter;
import ar.edu.unicen.isistan.asistan.tourwithme.models.UserInfoDTO;
import ar.edu.unicen.isistan.asistan.tourwithme.views.GroupFragment;
import ar.edu.unicen.isistan.asistan.tourwithme.views.GroupListFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.movements.MovementsFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.places.MyPlacesMapFragment;

public class GroupActivity extends AppCompatActivity implements GroupFragment.OnFragmentInteractionListener, MovementsFragment.OnFragmentInteractionListener, MyPlacesMapFragment.OnFragmentInteractionListener, GroupListFragment.OnFragmentInteractionListener {


    private RequestQueue mQueue;
    private User myUser;
    private static ArrayList<UserInfoDTO> foundUsersList = new ArrayList<>();
    private GroupFragment fragment;
    private ProgressBar progress_Bar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        progress_Bar = findViewById(R.id.progressBar);
        progress_Bar.setVisibility(View.VISIBLE);

        mQueue = Volley.newRequestQueue(this);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            fragment = GroupFragment.newInstance(getUsuariosCercanos());
            transaction.replace(R.id.group_frame_layout, fragment);
            transaction.commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private ArrayList<UserInfoDTO> getUsuariosCercanos(){

        GeoLocation lastLocation = Database.getInstance().geoLocation().lastTrusted().getValue();

        //String url = String.format("https://tourwithmeapi.azurewebsites.net/Group/UsuariosCercanos?x=%s&y=%s",lastLocation.getLongitude(), lastLocation.getLongitude());

        String url = String.format("https://tourwithmeapi.azurewebsites.net/Group/UsuariosCercanos?x=-37.32310632584254&y=-59.14021300998831");

        JsonArrayRequest jsonObjectResponse = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i<response.length();i++){
                            Log.d("Leyendo response de: ",url +"---"+response.getString(i));
                            JSONObject userJson = response.getJSONObject(i);
                            UserInfoDTO foundUser = new UserInfoDTO(userJson.getString("Name"),"",0,userJson.getJSONObject("Location").getDouble("X"),userJson.getJSONObject("Location").getDouble("Y"));


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

    public String sendUserData() {

        myUser = UserManager.loadProfile(getApplicationContext());
        UserState userState = UserStateReporter.get(this.getApplicationContext());
        UserInfoDTO userInfoDTO = new UserInfoDTO(myUser.getName(), myUser.getLastName(), myUser.getAge(), userState.getLocation().getLatitude(), userState.getLocation().getLongitude());

        return null;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}





