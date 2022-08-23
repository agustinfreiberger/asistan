package ar.edu.unicen.isistan.asistan.views.asistan.live;

import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import org.jetbrains.annotations.NotNull;
import java.util.Locale;
import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.Configuration;
import ar.edu.unicen.isistan.asistan.storage.database.activity.Activity;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.GeoLocation;
import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.ConfigurationManager;
import ar.edu.unicen.isistan.asistan.tourwithme.ShowSuggestedLocations;
import ar.edu.unicen.isistan.asistan.views.asistan.places.edit.PlaceActivity;
import ar.edu.unicen.isistan.asistan.views.map.GoogleMapController;
import ar.edu.unicen.isistan.asistan.views.map.MapController;
import ar.edu.unicen.isistan.asistan.views.map.OsmMapController;

public class LiveFragment extends Fragment implements OnMapReadyCallback {

    private final static String SECONDS = "s";
    private final static String METERS = "m";
    private final static String PERCENTAGE = "%%";

    private OnFragmentInteractionListener listener;

    @Nullable
    private MapController map;
    private LinearLayout deactivated;
    private LinearLayout live_info;
    private TextView act_probability;
    private TextView act_time;
    private TextView loc_accuracy;
    private TextView loc_time;
    private ImageView act_img;
    private ImageView provider_img;
    private Handler handler;
    private Runnable updater;
    private Observer<GeoLocation> location_observer;
    private Observer<Activity> activity_observer;
    private SharedPreferences.OnSharedPreferenceChangeListener config_listener;
    private GeoLocation last_loc;
    private Activity last_act;
    private Button CompassButton;
    @Nullable
    private LiveData<Activity> activityLiveData;
    @Nullable
    private LiveData<GeoLocation> locationLiveData;

    public LiveFragment() {
        // Required empty public constructor
    }

    public static LiveFragment newInstance() {
        LiveFragment fragment = new LiveFragment();
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
        View view = inflater.inflate(R.layout.fragment_live, container, false);
        this.init(view);
        return view;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            this.listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null && this.getContext() != null) {
            this.map = new GoogleMapController(this.getContext(), googleMap);
            if (this.locationLiveData != null) {
                GeoLocation location = this.locationLiveData.getValue();
                if (location != null)
                    this.map.setLocation(location);
            }
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void init(View view) {
        Context context = this.getContext();
        if (context != null) {

            this.last_loc = null;
            this.last_act = null;
            this.updater = null;
            this.handler = new Handler(Looper.getMainLooper());

            this.location_observer = new Observer<GeoLocation>() {

                @Override
                public void onChanged(@Nullable GeoLocation location) {
                    if (location != null && location.getLocElapsedTime() < SystemClock.elapsedRealtime())
                        LiveFragment.this.updateLocation(location);
                }
            };

            this.activity_observer = new Observer<Activity>() {
                @Override
                public void onChanged(@Nullable Activity activity) {
                    if (activity != null && activity.getElapsedTime() < SystemClock.elapsedRealtime())
                        LiveFragment.this.updateActivity(activity);
                }
            };

            this.config_listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                    Configuration config = ConfigurationManager.load(context);
                    if (config.isRunning())
                        LiveFragment.this.start();
                    else
                        LiveFragment.this.stop();
                }
            };

            this.deactivated = view.findViewById(R.id.deactivated);
            this.live_info = view.findViewById(R.id.live_info);
            this.act_img = view.findViewById(R.id.activity_icon);
            this.act_probability = view.findViewById(R.id.confidence);
            this.act_time = view.findViewById(R.id.act_time_ago);
            this.loc_accuracy = view.findViewById(R.id.accuracy);
            this.loc_time = view.findViewById(R.id.loc_time_ago);
            this.provider_img = view.findViewById(R.id.provider_icon);
            this.CompassButton = view.findViewById(R.id.btn_tourwithme);

            CompassButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), ShowSuggestedLocations.class);
                    startActivity(i);
                }
            });

            Configuration configuration = ConfigurationManager.load(context);
            if (configuration.getMapView() == MapController.Map.OPEN_STREET_MAP.getCode())
                this.map = OsmMapController.prepare(this.getContext(),view);
            else
                GoogleMapController.prepare(getChildFragmentManager(),this);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this.getContext() != null && (this.activityLiveData == null || this.locationLiveData == null)) {
            this.activityLiveData = Database.getInstance().activity().last();
            this.locationLiveData = Database.getInstance().geoLocation().lastTrusted();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        this.unSubscribe();
        super.onPause();
    }

    @Override
    public void onResume() {
        this.subscribe();
        super.onResume();
    }

    private void subscribe() {
        Context context = this.getContext();
        if (context != null) {
            if (this.locationLiveData == null)
                this.locationLiveData = Database.getInstance().geoLocation().last();
            if (this.activityLiveData == null)
                this.activityLiveData = Database.getInstance().activity().last();

            if (locationLiveData != null && this.activityLiveData != null) {
                this.locationLiveData.observe(this, this.location_observer);
                this.activityLiveData.observe(this, this.activity_observer);
                ConfigurationManager.subscribe(context,this.config_listener);
                Configuration config = ConfigurationManager.load(context);
                if (config.isRunning())
                    LiveFragment.this.start();
                else
                    LiveFragment.this.stop();
            }
        }
    }

    private void unSubscribe() {
        Context context = this.getContext();
        if (context != null) {
            if (this.locationLiveData == null)
                this.locationLiveData = Database.getInstance().geoLocation().last();
            if (this.activityLiveData == null)
                this.activityLiveData = Database.getInstance().activity().last();

            if (locationLiveData != null && this.activityLiveData != null) {
                this.locationLiveData.removeObserver(this.location_observer);
                this.activityLiveData.removeObserver(this.activity_observer);
                ConfigurationManager.unsubscribe(context,this.config_listener);
            }
        }

        if (this.updater != null) {
            this.handler.removeCallbacks(this.updater);
            this.updater = null;
        }
    }

    private void start() {
        if (this.getContext() != null) {
            this.live_info.setVisibility(View.VISIBLE);
            this.deactivated.setVisibility(View.GONE);

            this.updater = new Runnable() {
                @Override
                public void run() {
                    LiveFragment.this.updateTimes();
                    LiveFragment.this.handler.postDelayed(this, 1000);
                }
            };

            this.handler.postDelayed(this.updater, 1000);
        }
    }

    private void stop() {
        if (this.getContext() != null) {

            this.live_info.setVisibility(View.GONE);
            this.deactivated.setVisibility(View.VISIBLE);

            if (this.updater != null) {
                this.handler.removeCallbacks(this.updater);
                this.updater = null;
            }
        }
    }

    private void updateActivity(final Activity activity) {
        if (this.getActivity() != null) {
            LiveFragment.this.last_act = activity;
            int source;

            switch (activity.getType()) {
                case DetectedActivity.IN_VEHICLE:
                    source = R.drawable.ic_in_vehicle_24dp;
                    break;
                case DetectedActivity.ON_BICYCLE:
                    source = R.drawable.ic_on_bike_24dp;
                    break;
                case DetectedActivity.ON_FOOT:
                    source = R.drawable.ic_walking_black_24dp;
                    break;
                case DetectedActivity.STILL:
                    source = R.drawable.ic_still_black_24dp;
                    break;
                case DetectedActivity.TILTING:
                    source = R.drawable.ic_tilting_black_24dp;
                    break;
                case DetectedActivity.WALKING:
                    source = R.drawable.ic_walking_black_24dp;
                    break;
                case DetectedActivity.RUNNING:
                    source = R.drawable.ic_running_black_24dp;
                    break;
                default:
                    source = R.drawable.ic_unknown_black_24dp;
                    break;
            }

            LiveFragment.this.act_img.setImageDrawable(AppCompatDrawableManager.get().getDrawable(this.getActivity(), source));
            LiveFragment.this.act_probability.setText(String.format(Locale.US, "%d" + PERCENTAGE, (int) activity.getConfidence()));
            LiveFragment.this.updateTimes();
        }
    }

    private void updateLocation(final GeoLocation location) {
        if (this.getActivity() != null) {
            LiveFragment.this.last_loc = location;

            int source;
            switch (location.getProvider()) {
                case GeoLocation.GPS_PROVIDER:
                    source = R.drawable.ic_gps_provider_black_24dp;
                    break;
                case GeoLocation.NETWORK_PROVIDER:
                    source = R.drawable.ic_network_provider_black_24dp;
                    break;
                case GeoLocation.REPEATER_PROVIDER:
                    source = R.drawable.ic_repeater_provider_black_24dp;
                    break;
                case GeoLocation.FUSED_PROVIDER:
                    source = R.drawable.ic_fused_provider_black_24dp;
                    break;
                default:
                    source = R.drawable.ic_unknown_black_24dp;
                    break;
            }


            LiveFragment.this.provider_img.setImageDrawable(AppCompatDrawableManager.get().getDrawable(this.getActivity(), source));
            LiveFragment.this.loc_accuracy.setText(String.format(Locale.US, "%d" + METERS, (int) location.getAccuracy()));
            LiveFragment.this.updateTimes();

            if (LiveFragment.this.map != null)
                LiveFragment.this.map.setLocation(location);

        }
    }

    private void updateTimes() {
        long current = SystemClock.elapsedRealtime();
        if (last_loc != null) {
            this.loc_time.setText(String.format(Locale.US, "%d" + SECONDS, (int) (current-last_loc.getLocElapsedTime())/1000));
        }
        if (last_act != null) {
            this.act_time.setText(String.format(Locale.US, "%d" + SECONDS, (int) (current-last_act.getElapsedTime())/1000));
        }
    }

}
