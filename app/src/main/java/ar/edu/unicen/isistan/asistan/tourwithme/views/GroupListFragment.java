package ar.edu.unicen.isistan.asistan.tourwithme.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.tourwithme.models.UserInfoDTO;

public class GroupListFragment extends Fragment {

    private RecyclerView foundUsersRecyclerView;

    private static UsersAdapter adapter;
    private static ArrayList<UserInfoDTO> usersList = new ArrayList<>();


    public GroupListFragment(ArrayList<UserInfoDTO> users) {
        usersList = users;
    }


    public static GroupListFragment newInstance(ArrayList<UserInfoDTO> users) {
        GroupListFragment fragment = new GroupListFragment(users);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            foundUsersRecyclerView = (RecyclerView) view;
            adapter = new UsersAdapter(usersList);
            foundUsersRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            foundUsersRecyclerView.setAdapter(adapter);
        }
        return view;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}

