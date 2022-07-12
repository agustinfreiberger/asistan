package ar.edu.unicen.isistan.asistan.views.asistan.movements;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.Movement;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Commute;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Step;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.TransportMode;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.VisitCategory;
import ar.edu.unicen.isistan.asistan.views.asistan.movements.commutes.CommuteActivity;
import ar.edu.unicen.isistan.asistan.views.asistan.movements.visits.VisitActivity;

public class MovementsAdapter extends RecyclerView.Adapter<MovementsAdapter.MovementViewHolder> {

    private List<Movement> movements;
    private MyDiaryFragment fragment;
    private RecyclerView parent;
    private Movement movement;

    public MovementsAdapter(MyDiaryFragment fragment, List<Movement> movements) {
        this.fragment = fragment;
        this.movements = movements;
        this.movement = null;
    }

    @Override
    public void onAttachedToRecyclerView(@NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.parent = recyclerView;
    }

    @NonNull
    @Override
    public MovementViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == Movement.MovementType.VISIT.getCode())
            return new VisitViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_visit, viewGroup, false));
        else
            return new CommuteViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_commute, viewGroup, false));

    }

    @Override
    public void onBindViewHolder(@NonNull MovementViewHolder viewHolder, int position) {
        viewHolder.bindView(position);
    }

    @Override
    public int getItemViewType(int position) {
        return this.movements.get(position).getType().getCode();
    }

    @Override
    public int getItemCount() {
        return this.movements.size();
    }

    public boolean focus(Movement movement) {
        this.movement = movement;
        int index = this.movements.indexOf(movement);
        if (index >= 0) {
            if (index < this.movements.size()-1)
                index++;
            this.parent.smoothScrollToPosition(index);
            return true;
        } else {
            return false;
        }
    }

    public void clearFocus() {
        if (this.movement != null) {
            this.movement = null;
            this.notifyDataSetChanged();
        }
    }

    abstract class MovementViewHolder extends RecyclerView.ViewHolder {

        private MovementViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void bindView(int position);
    }

    class VisitViewHolder extends MovementViewHolder {

        private final DateFormat dateFormat = new SimpleDateFormat("HH:mm",Locale.US);

        private View view;
        private TextView time;
        private TextView name;
        private ImageView icon;

        private VisitViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.time = itemView.findViewById(R.id.time);
            this.name = itemView.findViewById(R.id.name);
            this.icon = itemView.findViewById(R.id.icon);
        }

        public void bindView(int position) {
            final Visit visit = (Visit) MovementsAdapter.this.movements.get(position);

            if (visit.equals(MovementsAdapter.this.movement))
                this.view.setBackgroundColor(this.view.getContext().getResources().getColor(R.color.colorLightPrimary));
            else
                this.itemView.setBackgroundColor(Color.WHITE);

            String name;
            int imgSrc;
            if (visit.getCategory() == VisitCategory.VISIT.getCode()) {
                Place place = visit.getPlace();
                imgSrc = place != null ? PlaceCategory.get(place.getPlaceCategory()).getIconSrc() : PlaceCategory.NEW.getIconSrc();
                if (place == null)
                    name = "¿Lugar nuevo?";
                else
                    name = place.getShowName();
            } else {
                VisitCategory category = VisitCategory.get(visit.getCategory());
                name = category.getName();
                imgSrc = category.getIconSrc();
            }

            this.name.setText(name);
            this.icon.setImageDrawable(AppCompatDrawableManager.get().getDrawable(this.view.getContext(), imgSrc));

            Date start_date = new Date(visit.getStartTime());
            Date end_date = new Date(visit.getEndTime());

            this.time.setText(this.dateFormat.format(end_date) + "\n⋮\n" + this.dateFormat.format(start_date));

            this.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(view.getContext(), VisitActivity.class);
                            intent.putExtra(VisitActivity.PARAMETER, visit.getId());
                            MovementsAdapter.this.fragment.startActivityForResult(intent, MyDiaryFragment.VISIT_REQUEST_CODE);
                        }
                    });
                }
            });
        }
    }

    class CommuteViewHolder extends MovementViewHolder {

        private final DecimalFormat decimalFormat = new DecimalFormat("#.00");

        private View view;
        private TextView time;
        private TextView name;
        private LinearLayout icons;

        private CommuteViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.time = itemView.findViewById(R.id.time);
            this.name = itemView.findViewById(R.id.name);
            this.icons = itemView.findViewById(R.id.icons);
        }

        @Override
        public void bindView(int position) {
            final Commute commute = (Commute) MovementsAdapter.this.movements.get(position);

            if (commute.equals(MovementsAdapter.this.movement))
                this.view.setBackgroundColor(this.view.getContext().getResources().getColor(R.color.colorLightPrimary));
            else
                this.view.setBackgroundColor(Color.WHITE);

            this.icons.removeAllViews();

            String distance_text = "";
            double distance = commute.distance();
            if (distance < 1000)
                distance_text = ((int) distance) + " metros";
            else
                distance_text = this.decimalFormat.format(distance/1000D) + " km";

            TransportMode transportMode = commute.transportMode();
            String transportModeText = transportMode.getDescription();

            for (Step step: commute.getSteps()) {
                TransportMode mode = step.transportMode();
                ImageView imageView = new ImageView(this.view.getContext());
                imageView.setImageDrawable(AppCompatDrawableManager.get().getDrawable(this.view.getContext(), mode.getIconSrc()));
                this.icons.addView(imageView);
            }

            this.name.setText(transportModeText + "\n" + distance_text);
            long minutes = (commute.duration()/60000);
            if (minutes != 0)
                this.time.setText( minutes + " minutos");
            else {
                long seconds = (commute.duration() / 1000);
                this.time.setText(seconds + " segundos");
            }

            this.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), CommuteActivity.class);
                    intent.putExtra(CommuteActivity.PARAMETER, commute.getId());
                    view.getContext().startActivity(intent);
                }
            });
        }

    }

}