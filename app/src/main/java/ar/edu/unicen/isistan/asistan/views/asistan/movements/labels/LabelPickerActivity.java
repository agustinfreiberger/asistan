package ar.edu.unicen.isistan.asistan.views.asistan.movements.labels;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.labels.Label;

public class LabelPickerActivity  extends AppCompatActivity {

    public static final String PARAMETER_SELECTED = "selected";
    public static final String PARAMETER_OPTIONS = "options";
    public static final String RESULT = "selected";

    private ArrayList<Label> originalSelected;
    private ArrayList<Label> selected;
    private ArrayList<Label> options;
    private LabelAdapter selectedAdapter;
    private LabelAdapter optionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_picker);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("Selección de etiquetas");

        this.selected = new ArrayList<>();
        this.options = new ArrayList<>();


        String json_selected = this.getIntent().getStringExtra(PARAMETER_SELECTED);
        String json_options = this.getIntent().getStringExtra(PARAMETER_OPTIONS);

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Label>>() {}.getType();
        this.selected.addAll((ArrayList<Label>) gson.fromJson(json_selected,type));
        this.options.addAll((ArrayList<Label>) gson.fromJson(json_options,type));
        this.originalSelected = new ArrayList<>(this.selected);

        for (Label label: this.selected)
            this.options.remove(label);

        RecyclerView recyclerViewSelected = this.findViewById(R.id.selected_list);
        RecyclerView recyclerViewOptions = this.findViewById(R.id.options_list);

        recyclerViewSelected.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        this.selectedAdapter = new LabelAdapter(this, this.selected);
        recyclerViewSelected.setAdapter(this.selectedAdapter);

        recyclerViewOptions.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        this.optionsAdapter = new LabelAdapter(this, this.options);
        recyclerViewOptions.setAdapter(this.optionsAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.label_picker_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean modified = !(this.selected.equals(this.originalSelected));
        menu.findItem(R.id.confirm).setVisible(modified);
        menu.findItem(R.id.cancel).setVisible(modified);
        return true;
    }

    public void change(Label label) {
        if (this.selected.remove(label))
            this.options.add(label);
        else if (this.options.remove(label))
            this.selected.add(label);
        Collections.sort(this.selected);
        Collections.sort(this.options);
        this.selectedAdapter.notifyDataSetChanged();
        this.optionsAdapter.notifyDataSetChanged();
        this.invalidateOptionsMenu();
    }

    public void revert() {
        this.options.addAll(this.selected);
        this.selected.clear();
        this.selected.addAll(this.originalSelected);
        for (Label label: selected)
            this.options.remove(label);
        Collections.sort(this.selected);
        Collections.sort(this.options);
        this.selectedAdapter.notifyDataSetChanged();
        this.optionsAdapter.notifyDataSetChanged();
        this.invalidateOptionsMenu();
    }

    public void confirm() {
        Intent data = new Intent();
        data.putExtra(RESULT,new Gson().toJson(this.selected));
        setResult(RESULT_OK, data);
        finish();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (this.selected.equals(this.originalSelected))
            super.onBackPressed();
        else
            this.revert();
    }
}
