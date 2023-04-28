package ar.edu.unicen.isistan.asistan.tourwithme;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.User;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.UserManager;

public class GroupActivity extends FragmentActivity {

    RecyclerView foundUsersRecyclerView;
    static GroupAdapter adapter;
    User myUser;
    static ArrayList<UserInfoDTO> foundUsersList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group);
        myUser = UserManager.loadProfile(this);
        initRecyclerView();
    }

    private void initRecyclerView() {
        foundUsersRecyclerView = findViewById(R.id.groupRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        foundUsersRecyclerView.setLayoutManager(layoutManager);
        this.adapter = new GroupAdapter(foundUsersList);
        foundUsersRecyclerView.setAdapter(adapter);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void startPublishing() {

        //cargo el dto con la info de mi usuario
        UserInfoDTO userInfoDTO = new UserInfoDTO(myUser.getName(), myUser.getLastName(), myUser.getAge());

        String arrayAsString = getIntent().getExtras().getString("tourwithme.userCategoryPreferences");
        List<UserCategoryPreference> userPreferenceslist = Arrays.asList(new Gson().fromJson(arrayAsString, UserCategoryPreference[].class));
        userInfoDTO.setPreferences(userPreferenceslist);


        // Using the default advertising option is enough since  connecting is not required.
        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().build();
        Nearby.getConnectionsClient(this)
                .startAdvertising("Hello world!", "nearby123", connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            // We were unable to start advertising.
                        });
    }

    private void startSubscription() {
        // Using the default discovery option is enough since connection is not required.
        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().build();
        Nearby.getConnectionsClient(this.getApplicationContext())
                // The SERVICE_ID value must uniquely identify your app.
                // As a best practice, use the package name of your app
                // (for example, com.google.example.myapp).
                .startDiscovery("nearby123", endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener(
                        (Void unused) -> {

                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            // We're unable to start discovering.
                        });
    }

    private void stopPublishing() {
        // Stop advertising when done.
        Nearby.getConnectionsClient(this.getApplicationContext()).stopAdvertising();
        Nearby.getConnectionsClient(this.getApplicationContext()).stopAllEndpoints();
    }

    private void stopSubscription() {
        // Stop discovery when the subscription is done.
        Nearby.getConnectionsClient(this.getApplicationContext()).stopDiscovery();
        Nearby.getConnectionsClient(this.getApplicationContext()).stopAllEndpoints();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();

        startPublishing();
        startSubscription();
    }


    @Override
    public void onStop() {
        stopSubscription();
        stopPublishing();

        super.onStop();
    }


    //aca nunca entra
    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {

        @Override
        public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
            Nearby.getConnectionsClient(getApplicationContext()).acceptConnection("nearby123", receiveBytesPayloadListener);
        }

        @Override
        public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution result) {
            //Si ambos aceptan la conección se manda la info del usuario
            switch (result.getStatus().getStatusCode()) {
                case ConnectionsStatusCodes.STATUS_OK:

               /* UserInfoDTO userInfoDTO = new UserInfoDTO(myUser.getName(), myUser.getLastName(), myUser.getAge());
                Payload payload = Payload.fromBytes(userInfoDTO.ToByteArray());
                Nearby.getConnectionsClient(context).sendPayload("nearby123",payload);*/
                    break;

                case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                    // The connection was rejected by one or both sides.
                    break;
                case ConnectionsStatusCodes.STATUS_ERROR:
                    // The connection broke before it was able to be accepted.
                    break;
                default:
                    // Unknown status code
            }
        }

        @Override
        public void onDisconnected(@NonNull String s) {

        }
    };

    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    // A remote advertising endpoint is found.
                    // To retrieve the published message data.
                    JsonElement jsonResponse = JsonParser.parseString(new String(info.getEndpointInfo()));
                    UserInfoDTO foundUser = new Gson().fromJson(jsonResponse, UserInfoDTO.class);
                    foundUsersList.add(foundUser);
                    adapter.notifyItemChanged(foundUsersList.size());


                }

                @Override
                public void onEndpointLost(String endpointId) {
                    // A previously discovered endpoint has gone away.
                }
    };


    private final PayloadCallback receiveBytesPayloadListener = new PayloadCallback() {

        @Override
        public void onPayloadReceived(String endpointId, Payload payload) {
            // This always gets the full data of the payload. Is null if it's not a BYTES payload.
            if (payload.getType() == Payload.Type.BYTES) {
                JsonElement jsonResponse = JsonParser.parseString(new String(payload.asBytes()));
                UserInfoDTO foundUser = new Gson().fromJson(jsonResponse, UserInfoDTO.class);
                foundUsersList.add(foundUser);
                adapter.notifyItemChanged(foundUsersList.size());

            }
        }

        @Override
        public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
            // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
            // after the call to onPayloadReceived().
        }
    };

}
