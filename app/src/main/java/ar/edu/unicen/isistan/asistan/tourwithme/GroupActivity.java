package ar.edu.unicen.isistan.asistan.tourwithme;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import ar.edu.unicen.isistan.asistan.storage.database.reports.userstate.UserState;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.User;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.UserManager;
import ar.edu.unicen.isistan.asistan.synchronizer.reports.userstate.UserStateReporter;

public class GroupActivity extends AppCompatActivity {


    RecyclerView foundUsersRecyclerView;

    static GroupAdapter adapter;
    RequestQueue mQueue;
    User myUser;
    static ArrayList<UserInfoDTO> foundUsersList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQueue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        pruebaLlamadaApi();

    }

    private void pruebaLlamadaApi(){
        String url = "https://jsonplaceholder.typicode.com/users";

        JsonArrayRequest jsonObjectRequest1 = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i<response.length();i++){
                        Log.d("Usuario: ", "onResponse: "+response.getJSONObject(i).getString("name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error

                }
            });

        mQueue.add(jsonObjectRequest1);
        //MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }


    private static List<JsonObject> getUsuariosCercanos(String query)  {
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
    }

    public String sendUserData() {

        myUser = UserManager.loadProfile(getApplicationContext());
        UserState userState = UserStateReporter.get(this.getApplicationContext());
        UserInfoDTO userInfoDTO = new UserInfoDTO(myUser.getName(), myUser.getLastName(), myUser.getAge(), userState.getLocation().getLatitude(), userState.getLocation().getLongitude());

        return null;
    }


//    JsonElement jsonResponse = JsonParser.parseString(new String(payload.asBytes()));
//    UserInfoDTO foundUser = new Gson().fromJson(jsonResponse, UserInfoDTO.class);
//            foundUsersList.add(foundUser);

}





