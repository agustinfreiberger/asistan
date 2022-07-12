package ar.edu.unicen.isistan.asistan.views.asistan.movements.labels;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.TooltipCompat;
import androidx.recyclerview.widget.RecyclerView;
import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.labels.Label;

public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.LabelViewHolder>{

    private LabelPickerActivity activity;
    private ArrayList<Label> labels;

    public LabelAdapter(ArrayList<Label> labels) {
        this.activity = null;
        this.labels = labels;
    }

    public LabelAdapter(LabelPickerActivity activity, ArrayList<Label> labels) {
        this.activity = activity;
        this.labels = labels;
    }

    @NonNull
    @Override
    public LabelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_label, parent, false);
        return new LabelAdapter.LabelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return this.labels.size();
    }

    public class LabelViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView name;

        public LabelViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.name = itemView.findViewById(R.id.name);
        }

        void bindView(int position) {
            final Label label = LabelAdapter.this.labels.get(position);

            String name = label.getName();
            this.name.setText(name);
            TooltipCompat.setTooltipText(this.view, label.getDescription());
            this.name.setBackgroundColor(label.getColor());

            this.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LabelAdapter.this.activity != null)
                        LabelAdapter.this.activity.change(label);
                }
            });

        }
    }

}
