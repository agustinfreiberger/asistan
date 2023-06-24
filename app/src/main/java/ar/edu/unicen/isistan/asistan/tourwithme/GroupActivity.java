package ar.edu.unicen.isistan.asistan.tourwithme;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.GeoLocation;
import ar.edu.unicen.isistan.asistan.storage.database.reports.userstate.UserState;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.User;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.UserManager;
import ar.edu.unicen.isistan.asistan.synchronizer.reports.userstate.UserStateReporter;
import ar.edu.unicen.isistan.asistan.tourwithme.models.UserInfoDTO;
import ar.edu.unicen.isistan.asistan.tourwithme.views.UsersAdapter;
import ar.edu.unicen.isistan.asistan.tourwithme.views.UsersListFragment;
import ar.edu.unicen.isistan.asistan.tracker.mobility.state.TrackerState;

public class GroupActivity extends AppCompatActivity {


    RecyclerView foundUsersRecyclerView;

    static UsersAdapter adapter;
    TrackerState trackerState;
    RequestQueue mQueue;
    User myUser;
    static ArrayList<UserInfoDTO> foundUsersList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        mQueue = Volley.newRequestQueue(this);
        trackerState = new TrackerState();
    }

    @Override
    protected void onStart() {
        super.onStart();

        getUsuariosCercanos(); //TODO:ver que getUsuarios traiga bien los datos y luego se pasen bien al fragment de aca abajo

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new UsersListFragment(foundUsersList);
        transaction.replace(R.id.groupRecyclerView, fragment);
        transaction.commit();

    }

    private void getUsuariosCercanos(){
        trackerState.init();
        GeoLocation lastLocation = trackerState.getLastValidLocation();

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
    }

    public String sendUserData() {

        myUser = UserManager.loadProfile(getApplicationContext());
        UserState userState = UserStateReporter.get(this.getApplicationContext());
        UserInfoDTO userInfoDTO = new UserInfoDTO(myUser.getName(), myUser.getLastName(), myUser.getAge(), userState.getLocation().getLatitude(), userState.getLocation().getLongitude());

        return null;
    }
}





