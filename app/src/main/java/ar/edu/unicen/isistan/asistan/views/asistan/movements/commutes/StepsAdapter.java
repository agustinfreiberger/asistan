package ar.edu.unicen.isistan.asistan.views.asistan.movements.commutes;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import androidx.appcompat.widget.AppCompatDrawableManager;
import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Step;

public class StepsAdapter extends ArrayAdapter<Step> {

    private final DecimalFormat decimalFormat = new DecimalFormat("#.00");

    private List<Step> steps;
    private LayoutInflater cursorInflater;
    private CommuteActivity activity;

    public StepsAdapter(@NonNull Context context, int resource, @NonNull List<Step> steps, CommuteActivity activity) {
        super(context, resource, steps);
        this.cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.steps = steps;
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if(view == null)
            view = cursorInflater.inflate(R.layout.list_item_step, parent, false);

        final Step step = this.steps.get(position);

        final int index = position;

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StepsAdapter.this.activity, TransportModeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(TransportModeActivity.INDEX_KEY, index);
                StepsAdapter.this.activity.startActivityForResult(intent, CommuteActivity.TRANSPORT_MODE_REQUEST_CODE);
            }
        });

        TextView id = view.findViewById(R.id.step_id);
        id.setText(String.valueOf(position+1));

        TextView title = view.findViewById(R.id.title);
        title.setText(step.transportMode().getDescription());

        ImageView icon = view.findViewById(R.id.icon);
        icon.setImageDrawable(AppCompatDrawableManager.get().getDrawable(this.activity, step.transportMode().getIconSrc()));

        String distance_text = "";
        double distance = step.distance();
        if (distance < 1000)
            distance_text = ((int) distance) + " metros";
        else
            distance_text = this.decimalFormat.format(distance/1000D) + " km";

        TextView distanceText = view.findViewById(R.id.distance);
        distanceText.setText(distance_text);

        return view;
    }

}
