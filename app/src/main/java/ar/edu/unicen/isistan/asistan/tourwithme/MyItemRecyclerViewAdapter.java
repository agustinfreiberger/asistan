package ar.edu.unicen.isistan.asistan.tourwithme;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.databinding.FragmentUserPreferencesBinding;

import java.util.ArrayList;
import java.util.List;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<UserCategoryPreference> mValues;

    public MyItemRecyclerViewAdapter(ArrayList<UserCategoryPreference> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentUserPreferencesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.imageView.setImageResource(mValues.get(position).getCategory().getMarkerSrc());
        holder.mIdView.setText(mValues.get(position).getCategory().getName());
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