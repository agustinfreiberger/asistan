package ar.edu.unicen.isistan.asistan.tourwithme;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;

import java.nio.charset.StandardCharsets;

import ar.edu.unicen.isistan.asistan.R;

public class GroupActivity extends FragmentActivity {

    TextView messageText;
    Button publishButton;


    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {}

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {}

                @Override
                public void onDisconnected(String endpointId) {}
            };

    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    // A remote advertising endpoint is found.
                    // To retrieve the published message data.
                    messageText.setText(new String(info.getEndpointInfo(), StandardCharsets.UTF_8));
                }

                @Override
                public void onEndpointLost(String endpointId) {
                    // A previously discovered endpoint has gone away.
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        publishButton = findViewById(R.id.buttonPublish);
        messageText = findViewById(R.id.message_text);


      /*  publishButton.setOnClickListener(view ->
                publish("Hello world!")
        );*/
    }

    private void startPublishing() {
        // Using the default advertising option is enough since  connecting is not required.
        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().build();
        Nearby.getConnectionsClient(this)
                .startAdvertising(android.os.Build.MODEL.getBytes(), "nearby123", connectionLifecycleCallback, advertisingOptions)
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
        Nearby.getConnectionsClient(this)
                // The SERVICE_ID value must uniquely identify your app.
                // As a best practice, use the package name of your app
                // (for example, com.google.example.myapp).
                .startDiscovery("nearby123", endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            // We're discovering!
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            // We're unable to start discovering.
                        });
    }

    private void stopPublishing() {
        // Stop advertising when done.
        Nearby.getConnectionsClient(this).stopAdvertising();
        Nearby.getConnectionsClient(this).stopAllEndpoints();
    }

    private void stopSubscription() {
        // Stop discovery when the subscription is done.
        Nearby.getConnectionsClient(this).stopDiscovery();
        Nearby.getConnectionsClient(this).stopAllEndpoints();
    }

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
}
