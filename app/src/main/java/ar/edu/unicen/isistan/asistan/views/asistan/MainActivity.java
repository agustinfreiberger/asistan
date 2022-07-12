package ar.edu.unicen.isistan.asistan.views.asistan;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.navigation.NavigationView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Locale;
import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.ConfigurationManager;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.User;
import ar.edu.unicen.isistan.asistan.storage.database.asistan.AsistanEvent;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.UserManager;
import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.Configuration;
import ar.edu.unicen.isistan.asistan.tracker.Tracker;
import ar.edu.unicen.isistan.asistan.views.asistan.config.ConfigFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.live.LiveFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.movements.MyMapFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.movements.MovementsFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.movements.MyStatsFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.movements.MyDiaryFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.places.MyPlacesFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.places.MyPlacesMapFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.places.PlacesFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.profile.ProfileFragment;
import ar.edu.unicen.isistan.asistan.views.asistan.stats.StatsFragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ProfileFragment.OnFragmentInteractionListener, LiveFragment.OnFragmentInteractionListener, ConfigFragment.OnFragmentInteractionListener, MovementsFragment.OnFragmentInteractionListener, MyDiaryFragment.OnFragmentInteractionListener, MyMapFragment.OnFragmentInteractionListener, MyStatsFragment.OnFragmentInteractionListener, PlacesFragment.OnFragmentInteractionListener, MyPlacesFragment.OnFragmentInteractionListener, MyPlacesMapFragment.OnFragmentInteractionListener, StatsFragment.OnFragmentInteractionListener {

    private static final String CLASS_TAG = "MainActivity";

    public static final String PARAMETER = "START_FRAGMENT";
    public static final int INQUIRER_CODE = 1;
    private final static String MANUALLY_STARTED = "SERVICE_MANUALLY_STARTED";
    private final static String MANUALLY_STOPPED = "SERVICE_MANUALLY_STOPPED";

    public final static int PERMISSIONS_CODE_REQUEST = 10;
    public final static int ADDITIONAL_PERMISSION_REQUEST = 11;

    private NavigationView navigation_view;
    private Switch service_switch;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.user = null;

        this.setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.layout_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        this.navigation_view = findViewById(R.id.nav_view);
        this.navigation_view.setNavigationItemSelectedListener(this);

        this.init();

        if (this.getIntent() != null) {
            int code = this.getIntent().getIntExtra(PARAMETER, -1);
            switch (code) {
                case INQUIRER_CODE:
                    onNavigationItemSelected(this.navigation_view.getMenu().findItem(R.id.nav_movements));
                    break;
                default:
                    onNavigationItemSelected(this.navigation_view.getMenu().findItem(R.id.nav_live));
                    break;
            }
            this.getIntent().removeExtra(PARAMETER);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.layout_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onNavigationItemSelected(@NotNull MenuItem item) {
        // Handle movement_navigation_menu view item clicks here.
        int id = item.getItemId();
        Fragment fragment;

        if (id == R.id.nav_live) {
            fragment = LiveFragment.newInstance();
        } else if (id == R.id.nav_profile) {
            fragment = ProfileFragment.newInstance();
        } else if (id == R.id.nav_movements) {
            fragment = MovementsFragment.newInstance();
        } else if (id == R.id.nav_places) {
            fragment = PlacesFragment.newInstance();
        } else if (id == R.id.nav_config) {
            fragment = ConfigFragment.newInstance();
        } else if (id == R.id.nav_stats) {
            fragment = StatsFragment.newInstance();
        } else {
            fragment = LiveFragment.newInstance();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_layout, fragment).commit();

        DrawerLayout drawer = findViewById(R.id.layout_main);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void init() {
        this.user = UserManager.loadComplete(this.getApplicationContext());
        this.loadUserData();

        this.service_switch = this.findViewById(R.id.asistan_switch);
        Configuration config = ConfigurationManager.load(this);

        if (config.isRunning())
            this.service_switch.setChecked(true);

        this.service_switch.setOnCheckedChangeListener( (CompoundButton buttonView, boolean isChecked) -> {
            if (isChecked) {
                if (this.checkPermissions(true)) {
                    if (!ConfigurationManager.load(this).isRunning()) {
                        this.checkAdditionalPermission();
                        this.startTracking();
                    }
                }
            } else
                this.stopTracking();
        });

    }

    private boolean checkPermissions(boolean request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if ((ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this,Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED)) {
                    this.service_switch.setChecked(false);
                    if (request)
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACTIVITY_RECOGNITION}, MainActivity.PERMISSIONS_CODE_REQUEST);
                    return false;
                }
            } else {
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    this.service_switch.setChecked(false);
                    if (request)
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MainActivity.PERMISSIONS_CODE_REQUEST);
                    return false;
                }
            }
        }

        return true;
    }

    private void checkAdditionalPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
                builder.setTitle("Ubicación en segundo plano");
                builder.setMessage("Para poder registrar sus movimientos es recomendable que AsisTan tenga acceso a su ubicación todo el tiempo.");
                builder.setPositiveButton("De acuerdo", (DialogInterface dialogInterface, int id) ->
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, MainActivity.ADDITIONAL_PERMISSION_REQUEST)
                );
                builder.setNegativeButton("No gracias", null);
                builder.setCancelable(true);
                builder.show();
            }
        }
    }

    private void stopTracking() {
        Configuration configuration = ConfigurationManager.load(this);
        configuration.setRunning(false);
        ConfigurationManager.store(MainActivity.this, configuration);
        AsyncTask.execute( () -> Tracker.stop(MainActivity.this.getApplicationContext()));
        Database.getInstance().asistan().asyncInsert(new AsistanEvent(CLASS_TAG,MainActivity.MANUALLY_STOPPED));
    }

    private void startTracking() {
        Configuration configuration = ConfigurationManager.load(this);
        configuration.setRunning(true);
        ConfigurationManager.store(MainActivity.this, configuration);
        AsyncTask.execute( () -> Tracker.start(MainActivity.this.getApplicationContext()) );
        Database.getInstance().asistan().asyncInsert(new AsistanEvent(CLASS_TAG,MainActivity.MANUALLY_STARTED));
        this.service_switch.setChecked(true);
    }

    public void loadUserData() {
        if (this.user != null) {
            View view = this.navigation_view.getHeaderView(0);
            ((CircleImageView) view.findViewById(R.id.header_profile_image)).setImageBitmap(user.getPhoto());
            ((TextView) view.findViewById(R.id.header_complete_name)).setText(String.format(Locale.US,"%s %s", user.getName(), user.getLastName()));
            ((TextView) view.findViewById(R.id.header_email)).setText(user.getEmail());
        }
    }

    public User getUser() {
        return this.user;
    }

    @Override
    public void onRequestPermissionsResult(int code, @NotNull String[] permissions, @NotNull int[] results) {
        super.onRequestPermissionsResult(code,permissions,results);

        if (code == PERMISSIONS_CODE_REQUEST) {
           if (this.checkPermissions(false)) {
               this.startTracking();
               this.checkAdditionalPermission();
           }
        } else if (code != ADDITIONAL_PERMISSION_REQUEST) {
            List<Fragment> fragments = this.getSupportFragmentManager().getFragments();
            for (Fragment fragment : fragments) {
                if (fragment != null)
                    fragment.onRequestPermissionsResult(code, permissions, results);
            }
        }
    }

}
