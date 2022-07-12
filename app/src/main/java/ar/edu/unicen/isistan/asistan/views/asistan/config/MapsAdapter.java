package ar.edu.unicen.isistan.asistan.views.asistan.config;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatDrawableManager;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.views.map.MapController;

public class MapsAdapter extends ArrayAdapter<MapController.Map> {

    public MapsAdapter(@NonNull Context context, int resource, int textResource) {
        super(context, resource, textResource);
    }

    @Override
    public int getCount() {
        return MapController.Map.values().length;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = mInflater.inflate(R.layout.list_item_map_selected, parent, false);
        ViewHolder mViewHolder = new ViewHolder(convertView);
        mViewHolder.show(MapController.Map.values()[position]);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.list_item_map, parent, false);
        DropDownViewHolder mViewHolder = new DropDownViewHolder(convertView);
        mViewHolder.show(MapController.Map.values()[position]);
        return convertView;
    }

    private class DropDownViewHolder {

        private ImageView icon;
        private TextView name;
        private View view;

        private DropDownViewHolder(View itemView) {
            this.view = itemView;
            this.name = itemView.findViewById(R.id.name);
            this.icon = itemView.findViewById(R.id.icon);
        }

        public void show(MapController.Map value) {
            this.name.setText(value.getName());
            this.icon.setImageDrawable(AppCompatDrawableManager.get().getDrawable(this.view.getContext(), value.getSrc()));
        }

    }

    private class ViewHolder {

        private ImageView icon;
        private View view;

        private ViewHolder(View itemView) {
            this.view = itemView;
            this.icon = itemView.findViewById(R.id.icon);
        }

        public void show(MapController.Map value) {
            this.icon.setImageDrawable(AppCompatDrawableManager.get().getDrawable(this.view.getContext(), value.getSrc()));
        }

    }
}
