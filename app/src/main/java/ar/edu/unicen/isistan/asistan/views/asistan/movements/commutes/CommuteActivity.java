package ar.edu.unicen.isistan.asistan.views.asistan.movements.commutes;

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
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.text.DecimalFormat;
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
import ar.edu.unicen.isistan.asistan.storage.database.mobility.labels.Label;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Commute;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Step;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.TransportMode;
import ar.edu.unicen.isistan.asistan.map.MapManager;
import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.Configuration;
import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.ConfigurationManager;
import ar.edu.unicen.isistan.asistan.views.asistan.movements.labels.LabelAdapter;
import ar.edu.unicen.isistan.asistan.views.asistan.movements.labels.LabelPickerActivity;
import ar.edu.unicen.isistan.asistan.views.map.GoogleMapController;
import ar.edu.unicen.isistan.asistan.views.map.MapController;
import ar.edu.unicen.isistan.asistan.views.map.OsmMapController;

public class CommuteActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String PARAMETER = "commute";

    private final DecimalFormat decimalFormat = new DecimalFormat("#.00");
    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm",Locale.US);

    public static final int TRANSPORT_MODE_REQUEST_CODE = 0;
    private static final int LABELS_REQUEST_CODE = 1;

    private MapController map;
    private TextView mode;
    private TextView time;
    private TextView distance;
    private LinearLayout modeIcons;
    private Commute originalCommute;
    private Commute commute;
    private ListView stepList;
    private LinearLayout editLayout;
    private boolean editing;
    private ArrayList<Step> steps;
    private View loading;
    private View layout;
    private RecyclerView recyclerViewLabels;

    @Nullable
    private LoadCommuteTask loadTask;
    @Nullable
    private DeleteCommuteTask deleteTask;
    @Nullable
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commute);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("Edicion de viaje");

        this.deleteTask = null;
        this.dialog = null;

        this.recyclerViewLabels = this.findViewById(R.id.labels_list);
        this.recyclerViewLabels.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));

        long commuteId = this.getIntent().getLongExtra(PARAMETER,-1);

        this.editing = false;

        this.stepList = this.findViewById(R.id.steps_list);
        this.editLayout = this.findViewById(R.id.edition);

        this.mode = this.findViewById(R.id.transport_mode);
        this.modeIcons = this.findViewById(R.id.transport_mode_icons);

        this.time = this.findViewById(R.id.time);
        this.distance = this.findViewById(R.id.distance);

        this.modeIcons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommuteActivity.this.commute.isClosed())
                    CommuteActivity.this.edit();
            }
        });

        this.mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommuteActivity.this.commute.isClosed())
                    CommuteActivity.this.edit();
            }
        });

        Configuration configuration = ConfigurationManager.load(this);
        if (configuration.getMapView() == MapController.Map.OPEN_STREET_MAP.getCode())
            this.map = OsmMapController.prepare(this, getWindow().getDecorView().getRootView());
        else
            GoogleMapController.prepare(getSupportFragmentManager(),this);

        this.loading = this.findViewById(R.id.loading);
        this.layout = this.findViewById(R.id.layout);
        this.loading.setVisibility(View.VISIBLE);
        this.layout.setVisibility(View.GONE);

        this.loadTask = new LoadCommuteTask(this);
        loadTask.execute(commuteId);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.commute_activity_menu, menu);
        menu.findItem(R.id.locked).setEnabled(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.commute != null) {
            if (!this.commute.isClosed()) {
                menu.findItem(R.id.locked).setVisible(true);
                menu.findItem(R.id.edit_transport_mode).setEnabled(false);
                menu.findItem(R.id.cancel_edit_transport_mode).setVisible(false);
                menu.findItem(R.id.delete_commute).setEnabled(false);
            } else {
                menu.findItem(R.id.locked).setVisible(false);
                menu.findItem(R.id.edit_transport_mode).setVisible(!this.editing);
                menu.findItem(R.id.cancel_edit_transport_mode).setVisible(this.editing);
                menu.findItem(R.id.delete_commute).setEnabled(this.originalCommute.mayBeAnError());
            }
            boolean modified = this.commute.different(this.originalCommute);
            menu.findItem(R.id.confirm).setVisible(modified);
            menu.findItem(R.id.cancel).setVisible(modified);
            menu.findItem(R.id.cancel_edit_transport_mode).setEnabled(!modified);
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

    private void endLoad(@Nullable Commute commute) {
        this.loadTask = null;
        if (commute == null)
            this.finish();
        else {
            this.commute = commute;
            this.originalCommute = this.commute.copy();
            CommuteActivity.this.steps = CommuteActivity.this.commute.getSteps();
            StepsAdapter adapter = new StepsAdapter(CommuteActivity.this,0,  CommuteActivity.this.steps,CommuteActivity.this);
            CommuteActivity.this.stepList.setAdapter(adapter);
            CommuteActivity.this.layout.setVisibility(View.VISIBLE);
            CommuteActivity.this.loading.setVisibility(View.GONE);
            CommuteActivity.this.init();
        }

    }

    private void init() {
        if (this.commute != null) {
            if (this.editing) {
                this.editLayout.setVisibility(View.VISIBLE);
                this.distance.setVisibility(View.GONE);
            } else {
                this.editLayout.setVisibility(View.GONE);
                this.distance.setVisibility(View.VISIBLE);
            }

            this.modeIcons.removeAllViews();

            TransportMode transportMode = this.commute.transportMode();
            this.mode.setText(transportMode.getDescription());

            for (Step step : commute.getSteps()) {
                TransportMode mode = step.transportMode();
                ImageView imageView = new ImageView(this);
                imageView.setImageDrawable(AppCompatDrawableManager.get().getDrawable(this, mode.getIconSrc()));
                this.modeIcons.addView(imageView);
            }

            double distance = this.commute.distance();
            String distance_text = distance < 1000 ? ((int) distance) + " metros" : this.decimalFormat.format(distance / 1000D) + " km";

            this.distance.setText(distance_text + " recorridos");

            long duration = this.commute.duration() / 60000;
            this.time.setText(duration + " minutos (" + this.dateFormat.format(new Date(commute.getStartTime())) + " - " + this.dateFormat.format(new Date(commute.getEndTime())) + ")");

            LabelAdapter adapter = new LabelAdapter(Label.get(this.commute.getLabels()));
            this.recyclerViewLabels.setAdapter(adapter);

            if (this.map != null) {
                this.map.clear();
                this.map.draw(this.commute);
            }

            this.invalidateOptionsMenu();
        }
    }

    private void edit() {
        if (this.originalCommute.getSteps().size() > 1) {
            this.editing = !this.editing;
            this.init();
        } else {
            Intent intent = new Intent(this, TransportModeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(TransportModeActivity.INDEX_KEY, 0);
            CommuteActivity.this.startActivityForResult(intent, TRANSPORT_MODE_REQUEST_CODE);
        }
    }

    private void revert() {
        this.commute.load(this.originalCommute);
        this.steps = this.commute.getSteps();
        StepsAdapter adapter = new StepsAdapter(this,0,  this.steps,this);
        this.stepList.setAdapter(adapter);
        this.init();
    }

    private void editLabels() {
        Intent intent = new Intent(CommuteActivity.this, LabelPickerActivity.class);
        intent.putExtra(LabelPickerActivity.PARAMETER_OPTIONS, new Gson().toJson(Label.COMMUTE_REASON.getSubLabels()));
        intent.putExtra(LabelPickerActivity.PARAMETER_SELECTED, new Gson().toJson(Label.get(this.commute.getLabels())));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        CommuteActivity.this.startActivityForResult(intent,LABELS_REQUEST_CODE);
    }

    private void delete() {
        if (this.originalCommute.mayBeAnError()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
            builder.setTitle("Borrar viaje");
            builder.setMessage("Solo debe borrar una visita si usted no se movimo del lugar y considera que es un error de AsisTan.");
            builder.setPositiveButton("Yo no hice este viaje", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int id) {
                    CommuteActivity.this.deleteTask = new DeleteCommuteTask(CommuteActivity.this);
                    CommuteActivity.this.deleteTask.execute(CommuteActivity.this.originalCommute);
                }
            });
            builder.setNegativeButton("Cancelar", null);
            builder.setCancelable(true);
            builder.show();
        }
    }

    private void startDelete() {
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(progressBar);

        View view = View.inflate(this, R.layout.dialog_progress, null);
        TextView text = view.findViewById(R.id.text_message);
        text.setText("Borrando viaje");

        builder.setView(view);
        builder.setCancelable(false);

        this.dialog = builder.create();
        this.dialog.show();
    }

    private void endDelete(boolean result) {
        this.deleteTask = null;
        if (result) {
            if (this.dialog != null)
                this.dialog.dismiss();
            this.finish();
        } else {
            if (this.dialog != null)
                this.dialog.dismiss();
            this.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(CommuteActivity.this, R.style.Dialog);
                            builder.setTitle("Reintentar mas tarde");
                            builder.setMessage("No se puede borrar el viaje ahora, intente borrarlo mas tarde.");
                            builder.setPositiveButton("Continuar", null);
                            builder.show();
                        }
                    }
            );
        }
    }

    private void confirm() {
        Database.getInstance().mobility().update(CommuteActivity.this.commute);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == TRANSPORT_MODE_REQUEST_CODE) {
                int code = data.getIntExtra(TransportModeActivity.KEY, TransportMode.UNSPECIFIED.getCode());
                if (code != TransportMode.UNSPECIFIED.getCode()) {
                    int index = data.getIntExtra(TransportModeActivity.INDEX_KEY, 0);
                    this.steps.get(index).setTransportMode(TransportMode.get(code));
                    this.commute.setSteps(this.steps);
                    StepsAdapter adapter = new StepsAdapter(this, 0, this.steps, this);
                    this.stepList.setAdapter(adapter);
                    this.init();
                }
            } else if (requestCode == LABELS_REQUEST_CODE) {
                String json = data.getStringExtra(LabelPickerActivity.RESULT);
                Type type = new TypeToken<ArrayList<Label>>() {}.getType();
                ArrayList<Label> labels = new Gson().fromJson(json, type);
                ArrayList<Integer> codes = new ArrayList<>();
                for (Label label: labels)
                    codes.add(label.getCode());
                this.commute.setLabels(codes);
                this.init();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (this.commute == null || !this.commute.isClosed())
            super.onBackPressed();
        else {
            if (this.commute.different(this.originalCommute)) {
                this.revert();
            } else if (this.editing) {
                this.editing = false;
                init();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirm:
                AsyncTask.execute(this::confirm);
                return true;
            case R.id.cancel:
                this.revert();
                return true;
            case R.id.edit_transport_mode:
                this.edit();
                return true;
            case R.id.edit_labels:
                this.editLabels();
                return true;
            case R.id.cancel_edit_transport_mode:
                this.editing = false;
                this.revert();
                return true;
            case R.id.delete_commute:
                this.delete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            this.map = new GoogleMapController(this,googleMap);
            this.init();
        }
    }

    private static class DeleteCommuteTask extends AsyncTask<Commute,Integer,Boolean> {

        private WeakReference<CommuteActivity> reference;

        public DeleteCommuteTask(CommuteActivity activity) {
            this.reference = new WeakReference<>(activity);
        }

        protected void onPreExecute() {
            CommuteActivity activity = this.reference.get();
            if (activity != null)
                activity.startDelete();
        }

        @Override
        @NotNull
        protected Boolean doInBackground(@Nullable Commute... commutes) {
            CommuteActivity commuteActivity = this.reference.get();
            if (commutes == null || commutes.length != 1 || commutes[0] == null || commuteActivity == null)
                return false;

            return Database.getInstance().mobility().deleteCommute(commuteActivity ,commutes[0]);
        }

        protected void onPostExecute(@NotNull Boolean result) {
            CommuteActivity activity = this.reference.get();
            if (activity != null)
                activity.endDelete(result);
        }

    }

    private static class LoadCommuteTask extends AsyncTask<Long, Integer, Commute> {

        private WeakReference<CommuteActivity> reference;

        public LoadCommuteTask(CommuteActivity activity) {
            this.reference = new WeakReference<>(activity);

        }
        protected void onPreExecute() {
            CommuteActivity activity = this.reference.get();
            if (activity != null)
                activity.startLoad();
        }

        @Override
        @Nullable
        protected Commute doInBackground(Long... longs) {
            if (longs == null || longs.length != 1 || longs[0] == null)
                return null;

            return Database.getInstance().mobility().selectCommuteAndContext(longs[0]);
        }

        protected void onPostExecute(@Nullable Commute result) {
            CommuteActivity activity = this.reference.get();
            if (activity != null)
                activity.endLoad(result);
        }

    }

}
