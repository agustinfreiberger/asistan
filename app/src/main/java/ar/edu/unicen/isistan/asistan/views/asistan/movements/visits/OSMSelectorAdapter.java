package ar.edu.unicen.isistan.asistan.views.asistan.movements.visits;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.recyclerview.widget.RecyclerView;
import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSM;

public class OSMSelectorAdapter extends RecyclerView.Adapter<OSMSelectorAdapter.OSMViewHolder> {

    private PlaceSelectorActivity activity;
    private List<OSM> osms;

    public OSMSelectorAdapter(PlaceSelectorActivity activity, List<OSM> osms) {
        this.activity = activity;
        this.osms = osms;
    }

    @NonNull
    @Override
    public OSMViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_place, parent, false);
        return new OSMSelectorAdapter.OSMViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OSMViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return this.osms.size();
    }

    public class OSMViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView place_name;
        private ImageView place_icon;

        public OSMViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.place_name = itemView.findViewById(R.id.place_name);
            this.place_icon = itemView.findViewById(R.id.place_type_icon);
        }

        void bindView(int position) {
            final OSM osm= OSMSelectorAdapter.this.osms.get(position);

            String name = osm.getName();
            this.place_name.setText(name);

            PlaceCategory place_category = PlaceCategory.get(osm.getCategory());
            if (place_category == null)
                place_category = PlaceCategory.UNSPECIFIED;

            int icon_resource = place_category.getIconSrc();
            this.place_icon.setImageDrawable(AppCompatDrawableManager.get().getDrawable(this.view.getContext(), icon_resource));

            this.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OSMSelectorAdapter.this.activity.select(osm);
                }
            });

        }
    }
}
