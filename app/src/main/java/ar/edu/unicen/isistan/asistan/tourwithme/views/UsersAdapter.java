package ar.edu.unicen.isistan.asistan.tourwithme.views;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ar.edu.unicen.isistan.asistan.databinding.FragmentUserBinding;
import ar.edu.unicen.isistan.asistan.tourwithme.models.UserCategoryPreference;
import ar.edu.unicen.isistan.asistan.tourwithme.models.UserInfoDTO;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private ArrayList<UserInfoDTO> usersList;

    public UsersAdapter(ArrayList<UserInfoDTO> usersList){
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.ViewHolder holder, int position) {
        String name = usersList.get(position).getName() +" "+usersList.get(position).getLastName();
        int age = usersList.get(position).getAge();
        List<UserCategoryPreference> preferences = usersList.get(position).getPreferences();

        holder.setData(name, age, preferences);
    }


    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameView;
        private TextView ageView;
        private NestedScrollView nestedScrollView;
        private LinearLayout innerLinearLayout;

        private ImageView categoryIcon;

        public ViewHolder(FragmentUserBinding binding) {
            super(binding.getRoot());
            nameView = binding.userNameTextView;
            ageView = binding.userAgeTextView;
            nestedScrollView = binding.preferencesNestedScrollView;
            innerLinearLayout = binding.preferencesNestedLayout;
            categoryIcon = binding.icon;
        }

        public void setData(String name, int age, List<UserCategoryPreference> preferences) {
            nameView.setText(name);
            ageView.setText(String.valueOf(age));

            nestedScrollView.removeAllViews();
            innerLinearLayout.removeAllViews();

            for (UserCategoryPreference preference : preferences) {
                categoryIcon = new ImageView(this.innerLinearLayout.getContext());
                categoryIcon.setImageResource(preference.getCategory().getMarkerSrc());
                innerLinearLayout.addView(categoryIcon);
            }
            nestedScrollView.addView(innerLinearLayout);

        }
    }
}
