package com.finalyearproject.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView workoutListView;
    private ArrayAdapter<String> workoutListAdapter;
    private List<Workout> allWorkouts = new ArrayList<>();
    private List<String> values = new ArrayList<>();

    FirebaseAuth mAuth;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);

        workoutListView = view.findViewById(R.id.workoutListView);

        mAuth = FirebaseAuth.getInstance();
        readDatabase();
        workoutListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                findWorkout(i);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    public void readDatabase() {
        FirebaseDatabase.getInstance("https://finalyearproject-e1d79-default-rtdb.europe-west1.firebasedatabase.app").getReference("Workouts").orderByChild("currentDate")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot data: snapshot.getChildren()) {
                            Workout workout = data.getValue(Workout.class);
                            if (workout.getUserId().equals(mAuth.getUid())) {
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                String dateString = formatter.format(new Date(Long.parseLong(workout.getCurrentDate())));
                                values.add(dateString);
                            }
                        }
                        if (getActivity() != null) {
                            Collections.reverse(values);
                            workoutListAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,
                                    values);
                            workoutListView.setAdapter(workoutListAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void findWorkout(int pos) {
        FirebaseDatabase.getInstance("https://finalyearproject-e1d79-default-rtdb.europe-west1.firebasedatabase.app").getReference("Workouts").orderByChild("currentDate")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Workout workout = new Workout();

                        for (DataSnapshot data: snapshot.getChildren()) {
                            Workout wk = data.getValue(Workout.class);
                            if (wk.getUserId().equals(mAuth.getUid())) {
                                allWorkouts.add(wk);
                                Collections.reverse(allWorkouts);
                            }
                        }
                        for (int i=0; i <= allWorkouts.size(); i++) {
                            if (i == pos) {
                                Log.i("Workouts: ", "size " + allWorkouts.get(i).getId());
                                workout = allWorkouts.get(i);
                            }
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("id", workout.getId()); // Put anything what you want

                        WorkoutDetailsFragment fragment = new WorkoutDetailsFragment();
                        fragment.setArguments(bundle);

                        if (getActivity() != null) {
                            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}