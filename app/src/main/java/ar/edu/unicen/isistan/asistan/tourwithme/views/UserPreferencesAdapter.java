package ar.edu.unicen.isistan.asistan.tourwithme.views;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ar.edu.unicen.isistan.asistan.databinding.FragmentUserPreferencesBinding;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.tourwithme.models.UserCategoryPreference;

import java.util.ArrayList;
import java.util.List;

public class UserPreferencesAdapter extends RecyclerView.Adapter<UserPreferencesAdapter.ViewHolder> {

    private final List<UserCategoryPreference> mValues;

    public UserPreferencesAdapter(ArrayList<UserCategoryPreference> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentUserPreferencesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.imageView.setImageResource(PlaceCategory.get(mValues.get(position).getPlacecategory()).getMarkerSrc());
        holder.mIdView.setText(PlaceCategory.get(mValues.get(position).getPlacecategory()).getName());
        holder.mContentView.setText(mValues.get(position).getPreference().toString());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imageView;
        public final TextView mIdView;
        public final TextView mContentView;

        public ViewHolder(FragmentUserPreferencesBinding binding) {
            super(binding.getRoot());
            imageView = binding.itemIcon;
            mIdView = binding.itemNumber;
            mContentView = binding.content;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}