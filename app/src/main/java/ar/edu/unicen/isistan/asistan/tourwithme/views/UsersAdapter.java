package ar.edu.unicen.isistan.asistan.tourwithme.views;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.databinding.FragmentUserBinding;
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

        holder.setData(name, age);
    }


    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameView;
        private TextView ageView;

        public ViewHolder(FragmentUserBinding binding) {
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
