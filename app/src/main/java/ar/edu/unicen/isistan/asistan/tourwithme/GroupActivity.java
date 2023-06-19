package ar.edu.unicen.isistan.asistan.tourwithme;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.GeoLocation;
import ar.edu.unicen.isistan.asistan.storage.database.reports.userstate.UserState;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.User;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.UserManager;
import ar.edu.unicen.isistan.asistan.synchronizer.reports.userstate.UserStateReporter;
import ar.edu.unicen.isistan.asistan.tourwithme.models.UserInfoDTO;
import ar.edu.unicen.isistan.asistan.tourwithme.views.UsersAdapter;
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

//        if (savedInstanceState == null) {
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            RecyclerViewFragment fragment = new RecyclerViewFragment();
//            transaction.replace(R.id.groupRecyclerView, fragment);
//            transaction.commit();
//        }

        mQueue = Volley.newRequestQueue(this);
        trackerState = new TrackerState();
    }

    @Override
    protected void onStart() {
        super.onStart();

        getUsuariosCercanos();

    }

    private void getUsuariosCercanos(){
        GeoLocation lastLocation = trackerState.getLastValidLocation();

        String url = String.format("https://tourwithmeapi.azurewebsites.net/Group/UsuariosCercanos?x=%s&y=%s",lastLocation.getLongitude(), lastLocation.getLongitude());

        JsonArrayRequest jsonObjectResponse = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i<response.length();i++){
                            Log.d("Leyendo response de: ",url +"---"+response.getString(i));
                            UserInfoDTO foundUser = new Gson().fromJson(String.valueOf(response.getJSONObject(i)), UserInfoDTO.class);
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


  /*  private static List<JsonObject> getUsuariosCercanos(String query)  {
        try {
            URL osm = new URL("OVERPASS_API");
            HttpURLConnection connection = (HttpURLConnection) osm.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(30000);
            connection.setRequestProperty("Content-Type", "x-www-form-urlencoded; charset=UTF-8");
            connection.setRequestMethod("POST");

            DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
            printout.writeBytes("data=" + URLEncoder.encode(query, "utf-8"));
            printout.flush();
            printout.close();

            BufferedReader buffered = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = buffered.readLine()) != null) {
                builder.append(line);
            }

            JsonParser parser = new JsonParser();
            JsonObject root = parser.parse(builder.toString()).getAsJsonObject();
            JsonArray elements = root.getAsJsonArray("elements");
            ArrayList<JsonObject> out = new ArrayList<>();

            for (JsonElement element: elements) {
                out.add(element.getAsJsonObject());
            }

            return out;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/

    public String sendUserData() {

        myUser = UserManager.loadProfile(getApplicationContext());
        UserState userState = UserStateReporter.get(this.getApplicationContext());
        UserInfoDTO userInfoDTO = new UserInfoDTO(myUser.getName(), myUser.getLastName(), myUser.getAge(), userState.getLocation().getLatitude(), userState.getLocation().getLongitude());

        return null;
    }
}





