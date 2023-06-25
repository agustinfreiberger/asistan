package ar.edu.unicen.isistan.asistan.tourwithme.views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.tourwithme.models.UserCategoryPreference;
import ar.edu.unicen.isistan.asistan.tourwithme.models.UserInfoDTO;

public class UsersListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private RecyclerView foundUsersRecyclerView;

    private static UsersAdapter adapter;
    private static ArrayList<UserInfoDTO> usersList = new ArrayList<>();


    public UsersListFragment(ArrayList<UserInfoDTO> users) {
        usersList = users;
    }

    // TODO: Customize parameter initialization
    public static UsersListFragment newInstance(int columnCount, ArrayList<UserInfoDTO> users) {
        UsersListFragment fragment = new UsersListFragment(users);
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            foundUsersRecyclerView = (RecyclerView) view;
            adapter = new UsersAdapter(usersList);
            if (mColumnCount <= 1) {
                foundUsersRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                foundUsersRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            foundUsersRecyclerView.setAdapter(adapter);
        }
        return view;
    }
}

