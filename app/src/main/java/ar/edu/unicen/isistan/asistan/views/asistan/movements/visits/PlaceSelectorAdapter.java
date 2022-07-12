package ar.edu.unicen.isistan.asistan.views.asistan.movements.visits;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.List;
import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;

public class PlaceSelectorAdapter extends RecyclerView.Adapter<PlaceSelectorAdapter.PlaceViewHolder> {

    public static final String KEY = "place";
    private PlaceSelectorActivity activity;
    private List<Place> places;

    public PlaceSelectorAdapter(PlaceSelectorActivity activity, List<Place> places) {
        this.places = places;
        this.activity = activity;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_place, viewGroup, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder viewHolder, int i) {
        viewHolder.bindView(i);
    }

    @Override
    public int getItemCount() {
        return this.places.size();
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {

        private final DecimalFormat decimalFormat = new DecimalFormat("#.000");

        private View view;
        private TextView place_name;
        private ImageView place_icon;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.place_name = itemView.findViewById(R.id.place_name);
            this.place_icon = itemView.findViewById(R.id.place_type_icon);
        }

        void bindView(int position) {
            final Place place = PlaceSelectorAdapter.this.places.get(position);

            String name = place.getShowName();
            if (name == null)
                name = "Sin Nombre " + place.getId();
            this.place_name.setText(name);

            PlaceCategory place_category = PlaceCategory.get(place.getPlaceCategory());
            if (place_category == null)
                place_category = PlaceCategory.NEW;

            int icon_resource = place_category.getIconSrc();
            this.place_icon.setImageDrawable(AppCompatDrawableManager.get().getDrawable(this.view.getContext(), icon_resource));

            this.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlaceSelectorAdapter.this.activity.select(place);
                }
            });

        }
    }
}
