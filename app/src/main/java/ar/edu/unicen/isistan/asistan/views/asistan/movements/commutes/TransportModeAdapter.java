package ar.edu.unicen.isistan.asistan.views.asistan.movements.commutes;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.appcompat.widget.AppCompatDrawableManager;
import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.TransportMode;

public class TransportModeAdapter extends ArrayAdapter<TransportMode> {

    private List<TransportMode> modes;
    private LayoutInflater cursorInflater;
    private TransportModeActivity activity;

    public TransportModeAdapter(@NonNull Context context, int resource, @NonNull List<TransportMode> modes,TransportModeActivity activity) {
        super(context, resource, modes);
        this.cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.modes = modes;
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if(view == null)
            view = cursorInflater.inflate(R.layout.transport_mode_item, parent, false);

        final TransportMode mode = this.modes.get(position);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransportModeAdapter.this.activity.select(mode);
            }
        });

        TextView title = view.findViewById(R.id.title);
        title.setText(mode.getDescription());

        ImageView icon = view.findViewById(R.id.icon);
        icon.setImageDrawable(AppCompatDrawableManager.get().getDrawable(this.activity, mode.getIconSrc()));

        return view;
    }

}
