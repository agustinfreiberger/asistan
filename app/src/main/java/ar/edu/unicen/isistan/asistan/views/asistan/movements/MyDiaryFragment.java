package ar.edu.unicen.isistan.asistan.views.asistan.movements;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.Movement;
import ar.edu.unicen.isistan.asistan.views.asistan.movements.inquirer.Inquirer;
import ar.edu.unicen.isistan.asistan.views.utils.DraggableFloatingActionButton;

public class MyDiaryFragment extends Fragment {

    public static final int VISIT_REQUEST_CODE = 1;

    private OnFragmentInteractionListener listener;

    private MovementsFragment parent;
    private RecyclerView recyclerView;
    private MovementsAdapter adapter;
    private Parcelable listState;

    private Movement movement;
    private MutableLiveData<ArrayList<Movement>> movements;
    private Observer<ArrayList<Movement>> observer;
    private ArrayList<Movement> adapterData;
    private Inquirer inquirer;

    public MyDiaryFragment() {
        this.adapterData = new ArrayList<>();
    }

    private void setMovements(MutableLiveData<ArrayList<Movement>> movements) {
        this.movements = movements;
        this.observer = new Observer<ArrayList<Movement>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Movement> movements) {
                if (movements != null) {
                    MyDiaryFragment.this.adapterData.clear();
                    MyDiaryFragment.this.adapterData.addAll(movements);
                    if (MyDiaryFragment.this.adapter == null) {
                        MyDiaryFragment.this.adapter = new MovementsAdapter(MyDiaryFragment.this, MyDiaryFragment.this.adapterData);
                        MyDiaryFragment.this.recyclerView.setAdapter(MyDiaryFragment.this.adapter);
                        MyDiaryFragment.this.checkAssistant();
                    } else {
                        MyDiaryFragment.this.adapter.notifyDataSetChanged();
                    }

                    if (MyDiaryFragment.this.movement != null) {
                        if (MyDiaryFragment.this.adapter.focus(MyDiaryFragment.this.movement))
                            MyDiaryFragment.this.movement = null;
                    } else {
                        MyDiaryFragment.this.adapter.clearFocus();
                    }
                }
            }
        };
    }

    private void checkAssistant() {
        this.inquirer.refresh();
    }

    public static MyDiaryFragment newInstance(MutableLiveData<ArrayList<Movement>> movements, MovementsFragment parent) {
        MyDiaryFragment fragment = new MyDiaryFragment();
        fragment.setMovements(movements);
        fragment.setParent(parent);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void setParent(MovementsFragment parent) {
        this.parent = parent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visits, container, false);
        this.init(view);
        return view;
    }

    private void init(View view) {
        if (this.getContext() != null) {
            this.recyclerView = view.findViewById(R.id.visits_list);
            this.recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), LinearLayoutManager.VERTICAL));

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
            this.recyclerView.setLayoutManager(layoutManager);
        }
        DraggableFloatingActionButton assistantButton = view.findViewById(R.id.assistant);
        this.inquirer = new Inquirer(assistantButton,this);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            this.listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void focus(Movement movement) {
        this.movement = movement;
        this.parent.focus(movement);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (this.getActivity() != null)
            this.movements.removeObserver(this.observer);

        if (this.recyclerView.getLayoutManager() != null)
            this.listState = this.recyclerView.getLayoutManager().onSaveInstanceState();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.listState != null && this.recyclerView.getLayoutManager() != null)
            this.recyclerView.getLayoutManager().onRestoreInstanceState(this.listState);

        if (this.getActivity() != null)
            this.movements.observe(this.getActivity(),this.observer);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == VISIT_REQUEST_CODE) {
                this.checkAssistant();
            }
        }
    }
}
