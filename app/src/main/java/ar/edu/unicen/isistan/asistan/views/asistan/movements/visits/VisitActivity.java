package ar.edu.unicen.isistan.asistan.views.asistan.movements.visits;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.labels.Labeler;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.labels.Label;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.VisitCategory;
import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.Configuration;
import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.ConfigurationManager;
import ar.edu.unicen.isistan.asistan.views.asistan.movements.labels.LabelAdapter;
import ar.edu.unicen.isistan.asistan.views.asistan.movements.labels.LabelPickerActivity;
import ar.edu.unicen.isistan.asistan.views.map.GoogleMapController;
import ar.edu.unicen.isistan.asistan.views.map.MapController;
import ar.edu.unicen.isistan.asistan.views.map.OsmMapController;

public class VisitActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PLACE_SELECTOR_REQUEST_CODE = 0;
    private static final int LABELS_REQUEST_CODE = 1;

    public static final String PARAMETER = "visit";

    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm",Locale.US);
    private TextView place;
    private TextView time;
    private ImageView placeIcon;
    private MapController map;
    private View loading;
    private View layout;
    private Visit originalVisit;
    private Visit visit;
    private RecyclerView recyclerViewLabels;
    private TextView emptyLabels;
    @Nullable
    private LoadVisitTask loadTask;
    @Nullable
    private DeleteVisitTask deleteTask;
    @Nullable
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("Edicion de visita");

        this.deleteTask = null;
        this.dialog = null;
        this.recyclerViewLabels = this.findViewById(R.id.labels_list);
        this.recyclerViewLabels.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        this.emptyLabels = this.findViewById(R.id.empty_labels);

        this.findViewById(R.id.labels).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VisitActivity.this.editLabels();
            }
        });

        long visitId = this.getIntent().getLongExtra(PARAMETER,-1);

        this.place = this.findViewById(R.id.place);
        this.placeIcon = this.findViewById(R.id.place_icon);
        this.place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VisitActivity.this.visit.getPlace() != null && VisitActivity.this.visit.getCategory() == VisitCategory.VISIT.getCode())
                    VisitActivity.this.selectPlace();
            }
        });
        this.placeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VisitActivity.this.visit.getPlace() != null && VisitActivity.this.visit.getCategory() == VisitCategory.VISIT.getCode())
                    VisitActivity.this.selectPlace();
            }
        });

        this.time = this.findViewById(R.id.time);

        Configuration configuration = ConfigurationManager.load(this);
        if (configuration.getMapView() == MapController.Map.OPEN_STREET_MAP.getCode())
            this.map = OsmMapController.prepare(this, getWindow().getDecorView().getRootView());
        else
            GoogleMapController.prepare(getSupportFragmentManager(),this);

        this.loading = this.findViewById(R.id.loading);
        this.layout = this.findViewById(R.id.layout);
        this.loading.setVisibility(View.VISIBLE);
        this.layout.setVisibility(View.GONE);

        this.loadTask = new LoadVisitTask(this);
        this.loadTask.execute(visitId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.visit_activity_menu, menu);
        menu.findItem(R.id.locked).setEnabled(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.visit != null) {
            menu.findItem(R.id.locked).setVisible(!this.visit.isClosed());
            menu.findItem(R.id.delete_visit).setEnabled(this.visit.isClosed());
            boolean modified = this.visit.different(this.originalVisit);
            menu.findItem(R.id.confirm).setVisible(modified);
            menu.findItem(R.id.cancel).setVisible(modified);
            menu.findItem(R.id.casual_stop).setEnabled(this.visit.getCategory() != VisitCategory.STOP.getCode());
            menu.findItem(R.id.forgotten_mobile).setEnabled(this.visit.getCategory() != VisitCategory.FORGOTTEN_MOBILE.getCode());
            return true;
        } else {
            return false;
        }
    }

    private void startLoad() {
        this.loading = this.findViewById(R.id.loading);
        this.layout = this.findViewById(R.id.layout);
        this.loading.setVisibility(View.VISIBLE);
        this.layout.setVisibility(View.GONE);
    }

    private void endLoad(@Nullable Visit visit) {
        this.loadTask = null;
        if (visit == null)
            this.finish();
        else {
            this.visit = visit;
            this.originalVisit = this.visit.copy();
            VisitActivity.this.layout.setVisibility(View.VISIBLE);
            VisitActivity.this.loading.setVisibility(View.GONE);
            VisitActivity.this.init();
        }

    }

    private void selectPlace() {
        Intent intent = new Intent(VisitActivity.this,PlaceSelectorActivity.class);
        intent.putExtra(PlaceSelectorActivity.PARAMETER, new Gson().toJson(VisitActivity.this.visit.getCenter()));
        if (VisitActivity.this.visit.getPlace() != null && VisitActivity.this.visit.getPlace().getName() == null)
            intent.putExtra(PlaceSelectorActivity.PARAMETER_NEW_PLACE, new Gson().toJson(VisitActivity.this.visit.getPlace()));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        VisitActivity.this.startActivityForResult(intent,PLACE_SELECTOR_REQUEST_CODE);
    }

    private void init() {
        this.setData();
        this.invalidateOptionsMenu();
    }

    private void setData() {
        if (this.visit != null) {
            if (this.visit.getCategory() != VisitCategory.VISIT.getCode()) {
                VisitCategory category = VisitCategory.get(this.visit.getCategory());
                this.place.setText(category.getName());
                this.placeIcon.setImageDrawable(AppCompatDrawableManager.get().getDrawable(this, category.getIconSrc()));
            } else if (this.visit.getPlace() != null) {
                this.place.setText(this.visit.getPlace().getShowName());
                this.placeIcon.setImageDrawable(AppCompatDrawableManager.get().getDrawable(this, PlaceCategory.get(this.visit.getPlace().getPlaceCategory()).getIconSrc()));
            } else {
                this.place.setText("¿Lugar nuevo?");
                this.placeIcon.setImageDrawable(AppCompatDrawableManager.get().getDrawable(this, PlaceCategory.NEW.getIconSrc()));
            }

            LabelAdapter adapter = new LabelAdapter(Label.get(this.visit.getLabels()));
            this.recyclerViewLabels.setAdapter(adapter);


            if (this.visit.getLabels().isEmpty())
                this.emptyLabels.setVisibility(View.VISIBLE);
            else
                this.emptyLabels.setVisibility(View.GONE);

            this.time.setText(this.dateFormat.format(new Date(this.visit.getStartTime())) + " - " + this.dateFormat.format(new Date(this.visit.getEndTime())));

            if (this.map != null) {
                this.map.clear();
                this.map.draw(this.visit);
                 if (this.visit.getPlace() != null) {
                    this.map.highlight(this.visit.getPlace().getArea().getCenter());
                }
                else {
                    this.map.highlight(this.visit.getCenter());
                }
            }
        }
    }

    private void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setTitle("Borrar visita");
        builder.setMessage("Solo debe borrar una visita si usted no se detuvo en este lugar y considera que es un error de AsisTan.");
        builder.setPositiveButton("Yo no me detuve aquí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int id) {
                VisitActivity.this.deleteTask = new DeleteVisitTask(VisitActivity.this);
                VisitActivity.this.deleteTask.execute(VisitActivity.this.originalVisit);
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.setCancelable(true);
        builder.show();

    }

    private void startDelete() {
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(progressBar);

        View view = View.inflate(this, R.layout.dialog_progress, null);
        TextView text = view.findViewById(R.id.text_message);
        text.setText("Borrando visita");
        builder.setView(view);
        builder.setCancelable(false);
        this.dialog = builder.create();
        this.dialog.show();
    }

    private void endDelete(boolean result) {
        this.deleteTask = null;
        if (result) {
            if (this.dialog != null)
                dialog.dismiss();
            VisitActivity.this.finish();
        } else {
            if (this.dialog != null)
                dialog.dismiss();
            VisitActivity.this.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(VisitActivity.this, R.style.Dialog);
                            builder.setTitle("Reintentar mas tarde");
                            builder.setMessage("No se puede borrar la visita ahora, intente borrar la visita mas tarde.");
                            builder.setPositiveButton("Continuar", null);
                            builder.show();
                        }
                    }
            );
        }
    }

    private void stop() {
        this.visit.setCategory(VisitCategory.STOP.getCode());
        this.visit.setPlace(null);
        this.init();
    }

    private void forgottenMobile() {
        this.visit.setCategory(VisitCategory.FORGOTTEN_MOBILE.getCode());
        this.visit.setPlace(null);
        this.init();
    }

    private void confirm() {
        if (this.visit.different(this.originalVisit)) {
            boolean result = Database.getInstance().mobility().changeVisit(this, this.visit);
            if (result) {
                Intent data = new Intent();
                this.setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    private void editLabels() {
        if (this.visit != null && this.visit.isClosed() && this.visit.getCategory() != VisitCategory.UNCONFIRMED.getCode()) {
            Intent intent = new Intent(VisitActivity.this, LabelPickerActivity.class);
            intent.putExtra(LabelPickerActivity.PARAMETER_OPTIONS, new Gson().toJson(Label.VISIT_ACTIVITY.getSubLabels()));
            intent.putExtra(LabelPickerActivity.PARAMETER_SELECTED, new Gson().toJson(Label.get(this.visit.getLabels())));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            VisitActivity.this.startActivityForResult(intent, LABELS_REQUEST_CODE);
        }
    }

    private void revert() {
        if (this.visit.different(this.originalVisit))
            this.visit.load(this.originalVisit);
        this.init();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PLACE_SELECTOR_REQUEST_CODE) {
                String json = data.getStringExtra(PlaceSelectorActivity.KEY);
                Place place = new Gson().fromJson(json,Place.class);
                this.visit.setPlace(place);
                new Labeler().simplyLabel(this.visit);
                this.visit.setCategory(VisitCategory.VISIT.getCode());
                this.init();
            } else if (requestCode == LABELS_REQUEST_CODE) {
                String json = data.getStringExtra(LabelPickerActivity.RESULT);
                Type type = new TypeToken<ArrayList<Label>>() {}.getType();
                ArrayList<Label> labels = new Gson().fromJson(json,type);
                ArrayList<Integer> codes = new ArrayList<>();
                for (Label label: labels)
                    codes.add(label.getCode());
                this.visit.setLabels(codes);
                this.init();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirm:
                AsyncTask.execute(this::confirm);
                return true;
            case R.id.cancel:
                this.revert();
                return true;
            case R.id.select_place:
                this.selectPlace();
                return true;
            case R.id.casual_stop:
                this.stop();
                return true;
            case R.id.forgotten_mobile:
                this.forgottenMobile();
                return true;
            case R.id.delete_visit:
                this.delete();
                return true;
            case R.id.edit_labels:
                this.editLabels();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (this.visit == null || !this.visit.isClosed() || this.visit.getCategory() == VisitCategory.UNCONFIRMED.getCode())
            super.onBackPressed();
        else {
            if (this.visit.different(this.originalVisit)) {
                this.revert();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.map != null)
            this.map.onDestroy();
        if (this.loadTask != null)
            this.loadTask.cancel(true);
        if (this.deleteTask != null)
            this.deleteTask.cancel(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            this.map = new GoogleMapController(this,googleMap);
            this.init();
        }
    }

    private static class DeleteVisitTask extends AsyncTask<Visit,Integer,Boolean> {

        private WeakReference<VisitActivity> reference;

        public DeleteVisitTask(VisitActivity activity) {
            this.reference = new WeakReference<>(activity);
        }

        protected void onPreExecute() {
            VisitActivity activity = this.reference.get();
            if (activity != null)
               activity.startDelete();
        }

        @Override
        @NotNull
        protected Boolean doInBackground(@Nullable Visit... visits) {
            VisitActivity visitActivity = this.reference.get();
            if (visits == null || visits.length != 1 || visits[0] == null || visitActivity == null)
                return false;

            return Database.getInstance().mobility().deleteVisit(visitActivity,visits[0]);
        }

        protected void onPostExecute(@NotNull Boolean result) {
            VisitActivity activity = this.reference.get();
            if (activity != null)
                activity.endDelete(result);
        }

    }

    private static class LoadVisitTask extends AsyncTask<Long, Integer, Visit> {

        private WeakReference<VisitActivity> reference;

        public LoadVisitTask(VisitActivity activity) {
            this.reference = new WeakReference<>(activity);

        }
        protected void onPreExecute() {
            VisitActivity activity = this.reference.get();
            if (activity != null)
                activity.startLoad();
        }

        @Override
        @Nullable
        protected Visit doInBackground(Long... longs) {
            if (longs == null || longs.length != 1 || longs[0] == null)
                return null;

            return Database.getInstance().mobility().selectVisitAndContext(longs[0]);
        }

        protected void onPostExecute(@Nullable Visit result) {
            VisitActivity activity = this.reference.get();
            if (activity != null)
                activity.endLoad(result);
        }

    }

}

