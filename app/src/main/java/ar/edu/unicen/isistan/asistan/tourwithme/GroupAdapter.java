package ar.edu.unicen.isistan.asistan.tourwithme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.databinding.FragmentUserPreferencesBinding;
import ar.edu.unicen.isistan.asistan.databinding.ListItemGroupBinding;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private ArrayList<UserInfoDTO> usersList;


    public GroupAdapter(ArrayList<UserInfoDTO> usersList){
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ListItemGroupBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.ViewHolder holder, int position) {
        String name = usersList.get(position).getName() +" "+usersList.get(position).getLastName();
        int age = usersList.get(position).getAge();

        holder.setData(name, age);
    }


    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameView;
        private TextView ageView;

        public ViewHolder(ListItemGroupBinding binding) {
            super(binding.getRoot());
            nameView = binding.userNameTextView;
            ageView = binding.userAgeTextView;
        }

        public void setData(String name, int age) {
            nameView.setText(name);
            ageView.setText(age);
        }
    }
}
