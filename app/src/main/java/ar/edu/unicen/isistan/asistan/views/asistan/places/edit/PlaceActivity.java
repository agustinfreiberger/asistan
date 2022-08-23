package ar.edu.unicen.isistan.asistan.views.asistan.places.edit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.Gson;

import java.util.Locale;

import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.Configuration;
import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.ConfigurationManager;
import ar.edu.unicen.isistan.asistan.tourwithme.ShowSuggestedLocations;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Area;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Circle;
import ar.edu.unicen.isistan.asistan.views.map.GoogleMapController;
import ar.edu.unicen.isistan.asistan.views.map.MapController;
import ar.edu.unicen.isistan.asistan.views.map.OsmMapController;

public class PlaceActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PLACE_CATEGORY_REQUEST_CODE = 0;

    private static final int MIN_RADIUS = 15;
    public static final String PARAMETER = "place";
    public static final String RESULT = "modified_place";

    private MapController map;
    private EditText name;
    private TextView type;
    private ImageView type_icon;
    private EditText description;
    private TextView radioValue;
    private View radio;
    private SeekBar radio_bar;
    private Place originalPlace;
    private Place place;
    private Button CompassButton;
    private MapController.PlaceController placeController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        String placeJson = this.getIntent().getStringExtra(PARAMETER);
        Gson gson = new Gson();

        this.map = null;

        this.place = gson.fromJson(placeJson, Place.class);
        this.originalPlace = gson.fromJson(placeJson, Place.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("Edicion de lugar");

        this.name = this.findViewById(R.id.place_name);
        this.name.setHint("Sin nombre");
        this.name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!PlaceActivity.this.name.getText().toString().isEmpty()) {
                    PlaceActivity.this.place.setName(PlaceActivity.this.name.getText().toString());
                } else
                    PlaceActivity.this.place.setName(null);
                PlaceActivity.this.invalidateOptionsMenu();
            }

        });

        View.OnClickListener categoryListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaceActivity.this.changeCategory();
            }
        };


        this.type = this.findViewById(R.id.place_type);
        this.type_icon = this.findViewById(R.id.place_type_icon);
        this.type.setOnClickListener(categoryListener);
        this.type_icon.setOnClickListener(categoryListener);

        this.description = this.findViewById(R.id.place_description);
        this.description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!PlaceActivity.this.description.getText().toString().isEmpty()) {
                    PlaceActivity.this.place.setDescription(PlaceActivity.this.description.getText().toString());
                } else
                    PlaceActivity.this.place.setDescription(null);
                PlaceActivity.this.invalidateOptionsMenu();
            }
        });

        this.radio = this.findViewById(R.id.radio);
        this.radioValue = this.findViewById(R.id.radio_value);
        this.radio_bar = this.findViewById(R.id.radio_bar);
        this.radio_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                PlaceActivity.this.updateRadio(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                PlaceActivity.this.invalidateOptionsMenu();
            }
        });

        Configuration configuration = ConfigurationManager.load(this);
        if (configuration.getMapView() == MapController.Map.OPEN_STREET_MAP.getCode())
            this.map = OsmMapController.prepare(this, getWindow().getDecorView().getRootView());
        else
            GoogleMapController.prepare(getSupportFragmentManager(),this);

        this.init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.place_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean modified = this.place.different(this.originalPlace);
        menu.findItem(R.id.change_area).setEnabled(!this.place.getArea().getType().equals(Area.AreaType.CIRCLE));
        menu.findItem(R.id.confirm).setVisible(modified || this.originalPlace.getId() == 0);
        menu.findItem(R.id.cancel).setVisible(modified);
        return true;
    }

    private void updateRadio(int progress) {
        int new_radio = progress + MIN_RADIUS;

        this.radioValue.setText(String.format(Locale.US, "%dm", new_radio));
        Area area = this.place.getArea();
        if (area.getType().equals(Area.AreaType.CIRCLE)) {
            Circle circle = (Circle) area;
            circle.setRadius(new_radio);
            this.place.setArea(circle);
            if (this.placeController != null)
                this.placeController.setRadius(new_radio);
        }
    }

    private void changeCategory() {
        Intent intent = new Intent(PlaceActivity.this,PlaceCategoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PlaceActivity.this.startActivityForResult(intent,PLACE_CATEGORY_REQUEST_CODE);
    }

    @SuppressLint("RestrictedApi")
    private void init() {
        if (this.place != null) {
            this.name.setText(this.place.getName());

            PlaceCategory place_category = PlaceCategory.get(this.place.getPlaceCategory());
            if (place_category == null)
                place_category = PlaceCategory.NEW;

            String type = place_category.getName();
            if (place_category == PlaceCategory.NEW)
                type = "Seleccionar una categoría";

            int icon_resource = place_category.getIconSrc();
            this.type.setText(type);
            this.type_icon.setImageDrawable(AppCompatDrawableManager.get().getDrawable(this, icon_resource));

            if (this.map != null) {
                this.map.clear();
                this.placeController = this.map.draw(this.place, new MapController.PlaceChangeListener() {

                    @Override
                    public void onPlaceChange(Coordinate coordinate) {
                        Area area = PlaceActivity.this.place.getArea();
                        if (area.getType().equals(Area.AreaType.CIRCLE)) {
                            Circle circle = (Circle) area;
                            circle.setCenter(coordinate);
                            PlaceActivity.this.place.setArea(circle);
                            PlaceActivity.this.place.setFixedLocation(true);
                            PlaceActivity.this.init();
                        }
                    }
                });

                Area area = this.place.getArea();
                if (area.getType().equals(Area.AreaType.CIRCLE)) {
                    this.radio.setVisibility(View.VISIBLE);
                    Circle circle = (Circle) area;
                    this.radioValue.setText(String.format(Locale.US, "%dm", (int) circle.getRadius()));
                    this.radio_bar.setProgress((int) (circle.getRadius() - MIN_RADIUS));
                    this.placeController.setDraggable(true);
                } else {
                    this.radio.setVisibility(View.GONE);
                    this.placeController.setDraggable(false);
                }
            }

            this.description.setText(this.place.getDescription());

            this.invalidateOptionsMenu();
        }
    }

    private void revert() {
        this.place.load(this.originalPlace);
        this.init();
    }

    private void changeArea() {
        if (!this.place.getArea().getType().equals(Area.AreaType.CIRCLE))
            this.place.setArea(new Circle(this.originalPlace.getArea().getCenter(),30));
        this.init();
    }

    private void confirm() {
        if (this.place.getName() == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
            builder.setTitle("Lugar sin nombre");
            builder.setMessage("Por favor, indica un nombre antes de guardar el lugar");
            builder.setPositiveButton("Continuar", null);
            builder.show();
        } else if (this.place.getPlaceCategory() == PlaceCategory.UNSPECIFIED.getCode() || this.place.getPlaceCategory() == PlaceCategory.NEW.getCode()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
            builder.setTitle("Lugar sin categoria");
            builder.setMessage("Por favor, selecciona una categoria para el lugar");
            builder.setPositiveButton("Continuar", null);
            builder.show();
        } else {
            AsyncTask.execute(() -> {
                PlaceActivity.this.place.setUpload(true);
                if (PlaceActivity.this.place.getId() != 0)
                    Database.getInstance().mobility().update(PlaceActivity.this.place);
                Intent data = new Intent();
                data.putExtra(RESULT, new Gson().toJson(PlaceActivity.this.place));
                PlaceActivity.this.setResult(RESULT_OK, data);
                PlaceActivity.this.finish();
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PLACE_CATEGORY_REQUEST_CODE) {
                int code = data.getIntExtra(PlaceCategoryActivity.RESULT, PlaceCategory.NEW.getCode());
                this.place.setPlaceCategory(code);
                this.init();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirm:
                this.confirm();
                return true;
            case R.id.cancel:
                this.revert();
                return true;
            case R.id.edit_category:
                this.changeCategory();
                return true;
            case R.id.change_area:
                this.changeArea();
                //return super.onOptionsItemSelected(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        if(this.place.different(this.originalPlace)) {
            this.revert();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            this.map = new GoogleMapController(this,googleMap);
            this.init();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.map != null)
            this.map.onDestroy();
    }
}
