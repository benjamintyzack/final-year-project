package com.finalyearproject.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkoutDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView dateText;
    private TableLayout table;

    public WorkoutDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkoutDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkoutDetailsFragment newInstance(String param1, String param2) {
        WorkoutDetailsFragment fragment = new WorkoutDetailsFragment();
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
        String workoutId = this.getArguments().getString("id");
        View view = inflater.inflate(R.layout.fragment_workout_details, container, false);

        table = view.findViewById(R.id.table);
        dateText = view.findViewById(R.id.date);

        getExercises(workoutId);
        // Inflate the layout for this fragment
        return view;
    }

    public void getExercises(String workoutId) {
        FirebaseDatabase.getInstance("https://finalyearproject-e1d79-default-rtdb.europe-west1.firebasedatabase.app").getReference("Workouts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Workout workout = new Workout();

                        for (DataSnapshot data: snapshot.getChildren()) {
                            Workout wk = data.getValue(Workout.class);
                            if (wk.getId().equals(workoutId)) {
                                workout = wk;
                            }
                        }
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        String dateString = formatter.format(new Date(Long.parseLong(workout.getCurrentDate())));
                        dateText.setText(dateString);
                        if(getActivity() != null){
                            for (Exercise exercise: workout.getExerciseList()) {
                                TableRow row = new TableRow(getActivity());
                                TextView tv1 = new TextView(getActivity());
                                tv1.setText(exercise.getExerciseName());
                                TextView tv2 = new TextView(getActivity());
                                String weight = exercise.getWeightUsed() + " KG";
                                tv2.setText(weight);
                                TextView tv3 = new TextView(getActivity());
                                String reps = exercise.getRepsCompleted() + " reps";
                                tv3.setText(reps);
                                int paddingDp = 20;
                                float density = getActivity().getResources().getDisplayMetrics().density;
                                int paddingPixel = (int)(paddingDp * density);
                                tv2.setPadding(paddingPixel,0,0,0);
                                tv3.setPadding(paddingPixel,0,0,0);
                                row.addView(tv1);
                                row.addView(tv2);
                                row.addView(tv3);
                                table.addView(row);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}