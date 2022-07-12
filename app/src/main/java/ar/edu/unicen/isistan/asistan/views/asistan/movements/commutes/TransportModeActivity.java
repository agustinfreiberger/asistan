package ar.edu.unicen.isistan.asistan.views.asistan.movements.commutes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.TransportMode;

public class TransportModeActivity extends Activity {

    public static final String KEY = "mode";
    public static final String INDEX_KEY = "index";

    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transport_mode_activity);
        ListView listView = findViewById(R.id.transport_mode_list);

        this.index = this.getIntent().getIntExtra(INDEX_KEY,-1);

        ArrayList<TransportMode> modes = new ArrayList<>(Arrays.asList(TransportMode.values()));
        modes.remove(TransportMode.UNSPECIFIED);

        TransportModeAdapter adapter = new TransportModeAdapter(this,0, modes,this);
        listView.setAdapter(adapter);
    }

    public void select(TransportMode mode) {
        Intent data = new Intent();
        data.putExtra(TransportModeActivity.KEY,mode.getCode());
        data.putExtra(TransportModeActivity.INDEX_KEY, this.index);
        setResult(RESULT_OK, data);
        finish();
    }

}
