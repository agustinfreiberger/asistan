package ar.edu.unicen.isistan.asistan.views.asistan.config;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.PowerManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import org.jetbrains.annotations.NotNull;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.Configuration;
import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.ConfigurationManager;
import ar.edu.unicen.isistan.asistan.utils.time.Time;
import ar.edu.unicen.isistan.asistan.utils.autostarter.AutoStartPermissionHelper;
import ar.edu.unicen.isistan.asistan.views.map.MapController;

public class ConfigFragment extends Fragment {

    private OnFragmentInteractionListener listener;
    private LinearLayout times_layout;

    public ConfigFragment() {
        // Required empty public constructor
    }

    public static ConfigFragment newInstance() {
        ConfigFragment fragment = new ConfigFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            this.listener = (OnFragmentInteractionListener) context;
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

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_config, container, false);
        this.init(view);
        return view;
    }

    private void init(View view) {
        // Config
        Context context = this.getContext();
        if (context != null) {
            Configuration config = ConfigurationManager.load(this.getContext());

            // General
            view.findViewById(R.id.version_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            ((TextView) view.findViewById(R.id.version_text)).setText(String.format("v%s", this.getVersionName(this.getContext())));

            final EditText start_time_text = view.findViewById(R.id.start_time);
            final Time start_time = config.getStartTime();
            start_time_text.setText(start_time.toString());

            final TimePickerDialog.OnTimeSetListener start_time_listener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    ConfigFragment.this.setStarTime(hourOfDay, minute);
                    start_time.set(hourOfDay, minute);
                    start_time_text.setText(start_time.toString());

                }

            };

            start_time_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimePickerDialog dialog = new TimePickerDialog(
                            ConfigFragment.this.getContext(),
                            start_time_listener, start_time.getHour(), start_time.getMinutes(), true);

                    dialog.show();
                }
            });

            final EditText end_time_text = view.findViewById(R.id.end_time);
            final Time end_time = config.getEndTime();
            end_time_text.setText(end_time.toString());

            final TimePickerDialog.OnTimeSetListener end_time_listener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    ConfigFragment.this.setEndTime(hourOfDay, minute);
                    end_time.set(hourOfDay, minute);
                    end_time_text.setText(end_time.toString());
                }

            };

            end_time_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimePickerDialog dialog = new TimePickerDialog(
                            ConfigFragment.this.getContext(),
                            end_time_listener, end_time.getHour(), end_time.getMinutes(), true);

                    dialog.show();
                }
            });

            this.times_layout = view.findViewById(R.id.notification_time_interval_layout);

            if (!config.isProgrammedTime())
                this.times_layout.setVisibility(View.GONE);


            Switch aux = view.findViewById(R.id.programmed_notification_switch);
            aux.setChecked(config.isProgrammedTime());
            aux.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ConfigFragment.this.setReduceTime(isChecked);
                    if (isChecked)
                        ConfigFragment.this.times_layout.setVisibility(View.VISIBLE);
                    else
                        ConfigFragment.this.times_layout.setVisibility(View.GONE);
                }
            });

            // Visualization
            Spinner spinner = view.findViewById(R.id.map_spinner);
            MapsAdapter mapsAdapter = new MapsAdapter(context, R.layout.list_item_map, R.id.name);
            spinner.setAdapter(mapsAdapter);
            spinner.setSelection(config.getMapView());

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ConfigFragment.this.setMap(MapController.Map.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if (AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(context)) {
                final LinearLayout autoStartLayout = view.findViewById(R.id.autostart_layout);
                autoStartLayout.setVisibility(View.VISIBLE);
                autoStartLayout.setOnClickListener((View v) -> {
                    try {
                        AutoStartPermissionHelper.getInstance().getAutoStartPermission(context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String packageName = context.getPackageName();
                PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                TextView batteryStatus = view.findViewById(R.id.battery_status);
                if (powerManager != null && powerManager.isIgnoringBatteryOptimizations(packageName)) {
                    batteryStatus.setText("Hecho");
                    batteryStatus.setTextColor(getResources().getColor(R.color.colorPrimary, null));
                } else {
                    batteryStatus.setText("Desactivar");
                    batteryStatus.setTextColor(getResources().getColor(R.color.colorAccent, null));
                }
                final LinearLayout batteryLayout = view.findViewById(R.id.battery_layout);
                batteryLayout.setVisibility(View.VISIBLE);
                batteryLayout.setOnClickListener((View v) -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    context.startActivity(intent);
                });

            }
                    }
    }

    private void setMap(MapController.Map map) {
        Context context = this.getContext();
        if (context != null) {
            Configuration config = ConfigurationManager.load(context);
            config.setMapView(map.getCode());
            ConfigurationManager.store(this.getContext(), config);
        }
    }

    private void setReduceTime(boolean value) {
        Context context = this.getContext();
        if (context != null) {
            Configuration config = ConfigurationManager.load(context);
            config.setProgrammedTime(value);
            ConfigurationManager.store(this.getContext(), config);
        }
    }

    private void setStarTime(int hour, int minutes) {
        Context context = this.getContext();
        if (context != null) {
            Configuration config = ConfigurationManager.load(context);
            config.setStartTime(new Time(hour,minutes));
            ConfigurationManager.store(this.getContext(), config);
        }
    }

    private void setEndTime(int hour, int minutes) {
        Context context = this.getContext();
        if (context != null) {
            Configuration config = ConfigurationManager.load(context);
            config.setEndTime(new Time(hour,minutes));
            ConfigurationManager.store(this.getContext(), config);
        }
    }

    private String getVersionName(Context context) {
        if (context != null) {
            try {
                return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    @Override
    public void onResume(){
        super.onResume();
        Context context = this.getContext();
        View view = this.getView();
        if (context != null && view != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = context.getPackageName();
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            TextView batteryStatus = view.findViewById(R.id.battery_status);
            if (powerManager != null && powerManager.isIgnoringBatteryOptimizations(packageName)) {
                batteryStatus.setText("Hecho");
                batteryStatus.setTextColor(getResources().getColor(R.color.colorPrimary, null));
            } else {
                batteryStatus.setText("Desactivar");
                batteryStatus.setTextColor(getResources().getColor(R.color.colorAccent, null));
            }
        }

    }


}
