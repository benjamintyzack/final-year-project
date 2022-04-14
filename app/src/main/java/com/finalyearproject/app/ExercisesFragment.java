package com.finalyearproject.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExercisesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExercisesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Spinner exerciseSpinner;
    private GraphView lineGraphView;
    private TextView oneRMEView;

    private FirebaseAuth mAuth;

    public ExercisesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExercisesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExercisesFragment newInstance(String param1, String param2) {
        ExercisesFragment fragment = new ExercisesFragment();
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

        View view = inflater.inflate(R.layout.fragment_exercises, container, false);

        exerciseSpinner = view.findViewById(R.id.exerciseSpinner);
        oneRMEView  = view.findViewById(R.id.oneRME);
        lineGraphView = view.findViewById(R.id.graph);
        lineGraphView.getViewport().setScalable(true);
        lineGraphView.getViewport().setScrollable(true);

        lineGraphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                return super.formatLabel(value, isValueX);
            }
        });

        mAuth = FirebaseAuth.getInstance();

        getExercises();
        exerciseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getExerciseData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    public void getExercises() {
        FirebaseDatabase.getInstance("https://finalyearproject-e1d79-default-rtdb.europe-west1.firebasedatabase.app").getReference("Workouts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Workout> values = new ArrayList<>();
                        List<String> exercises = new ArrayList<>();

                        for (DataSnapshot data: snapshot.getChildren()) {
                            Workout workout = data.getValue(Workout.class);
                            if (workout.getUserId().equals(mAuth.getUid())) {
                                values.add(workout);
                            }
                        }
                        for (Workout wk: values) {
                            for (Exercise exercise: wk.getExerciseList()) {
                                if (!exercises.contains(exercise.getExerciseName())) {
                                    exercises.add(exercise.getExerciseName());
                                }
                            }

                        }
                        if (getActivity() != null) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item, exercises);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                            exerciseSpinner.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void getExerciseData() {
        lineGraphView.removeAllSeries();
        oneRMEView.setText("");
        if(!exerciseSpinner.getSelectedItem().toString().isEmpty()) {
            String exerciseName = exerciseSpinner.getSelectedItem().toString();

            FirebaseDatabase.getInstance("https://finalyearproject-e1d79-default-rtdb.europe-west1.firebasedatabase.app").getReference("Workouts").orderByChild("currentDate")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<Workout> values = new ArrayList<>();
                            List<Exercise> exercises = new ArrayList<>();

                            for (DataSnapshot data: snapshot.getChildren()) {
                                Workout workout = data.getValue(Workout.class);
                                if (workout.getUserId().equals(mAuth.getUid())) {
                                    values.add(workout);
                                }
                            }
                            for (Workout wk: values) {
                                for (Exercise exercise: wk.getExerciseList()) {
                                    if (exercise.getExerciseName().equals(exerciseName)) {
                                        exercises.add(exercise);
                                    }
                                }
                            }
                            int x = 0;
                            int y;
                            int year = new Date().getYear();
                            LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
                            for (int i = 0; i < 12; i++) {
                                x+=1;
                                y= getHighestWeight(i, year, exercises);
                                DataPoint newPoint = new DataPoint(x, y);
                                series.appendData(newPoint, false, 12);
                                series.setDrawDataPoints(true);
                            }
                            lineGraphView.addSeries(series);
                            oneRMEView.setText(String.format("%.2f", calculateOneRME(exercises)));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    public int getHighestWeight(int month, int currentYear, List<Exercise> exercises) {
        int highestWeight = 0;
        for (int i = 0; i < exercises.size(); i++) {
            Date exerciseDate = new Date(Long.parseLong(exercises.get(i).getCurrentDate()));
            int exerciseMonth = exerciseDate.getMonth();
            int exerciseYear = exerciseDate.getYear();
            if(exerciseYear == currentYear) {
                if(exerciseMonth == month) {
                    if(Integer.parseInt(exercises.get(i).getWeightUsed().trim()) > highestWeight) {
                        highestWeight = Integer.parseInt(exercises.get(i).getWeightUsed().trim());
                    }
                }
            }
        }
        return highestWeight;
    }

    public double calculateOneRME(List<Exercise> exercises) {
        double oneRME = 0;
        double highestWeight = 0;
        int reps = 0;
        for (Exercise exercise : exercises) {
            if(Double.parseDouble(exercise.getWeightUsed().trim()) > oneRME) {
                if (Integer.parseInt(exercise.getRepsCompleted().trim()) == 1) {
                    oneRME = Double.parseDouble(exercise.getWeightUsed().trim());
                    return oneRME;
                }
                highestWeight = Double.parseDouble(exercise.getWeightUsed().trim());
                reps = Integer.parseInt(exercise.getRepsCompleted().trim());
            }
        }
        double sumA = reps * 2.5;
        double sumB = 100 - sumA; // % that the weight used represents of the theoretical 1RM
        double sumC = sumB / 100; // The number to divide the weight used by to find out 1RM estimation
        oneRME = highestWeight / sumC;

        return oneRME;
    }
}