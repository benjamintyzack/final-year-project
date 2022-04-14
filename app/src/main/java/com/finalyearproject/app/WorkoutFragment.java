package com.finalyearproject.app;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutFragment extends Fragment implements OnTouchListener, OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private EditText exerciseText, weightText, repetitionText;
    private ImageView micButton;
    private Button startWorkoutButton, addExerciseButton, endWorkoutButton;
    final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    private List<Exercise> exerciseList = new ArrayList<>();

    private FirebaseAuth mAuth;

    public WorkoutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkoutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkoutFragment newInstance(String param1, String param2) {
        WorkoutFragment fragment = new WorkoutFragment();
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
        View view = inflater.inflate(R.layout.fragment_workout, container, false);

        mAuth = FirebaseAuth.getInstance();

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }

        exerciseText = view.findViewById(R.id.exercise);
        weightText = view.findViewById(R.id.weight);
        repetitionText = view.findViewById(R.id.repetitions);
        micButton = view.findViewById(R.id.button);
        startWorkoutButton = view.findViewById(R.id.startWorkout);
        startWorkoutButton.setOnClickListener(this);
        addExerciseButton = view.findViewById(R.id.addExercise);
        addExerciseButton.setOnClickListener(this);
        endWorkoutButton = view.findViewById(R.id.endWorkout);
        endWorkoutButton.setOnClickListener(this);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());

        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                micButton.setImageResource(R.drawable.ic_baseline_mic_24);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Log.i("Scanner data", "Data: " + data.get(0));
                Scanner sc = new Scanner(data.get(0));
                sc.useDelimiter("for | of");
                int counter = 0;
                while(sc.hasNext()) {
                        if(counter == 0) {
                            exerciseText.setText(sc.next());
                            counter++;
                        } else if(counter == 1) {
                            repetitionText.setText(sc.next());
                            counter++;
                        } else if(counter == 2) {
                            weightText.setText(sc.next());
                        }
                }
                sc.close();
//                if (exerciseText.getText().toString().isEmpty()) {
//                    exerciseText.setText(data.get(0));
//                }
//                else if (!exerciseText.getText().toString().isEmpty() && weightText.getText().toString().isEmpty()) {
//                    weightText.setText(data.get(0));
//                }
//                else if (!exerciseText.getText().toString().isEmpty() && !weightText.getText().toString().isEmpty() && repetitionText.getText().toString().isEmpty()) {
//                    repetitionText.setText(data.get(0));
//                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        micButton.setOnTouchListener(this);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startWorkout:
                startWorkout();
                break;
            case R.id.addExercise:
                saveExercise();
                break;
            case R.id.endWorkout:
                endWorkout();
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        switch(action) {
            case MotionEvent.ACTION_UP:
                micButton.setImageResource(R.drawable.ic_baseline_mic_24);
                speechRecognizer.stopListening();
                break;
            case MotionEvent.ACTION_DOWN:
                micButton.setImageResource(R.drawable.mic_listening);
                speechRecognizer.startListening(speechRecognizerIntent);
                break;

        }
        return false;
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                makeText(getActivity(),"Permission Granted", LENGTH_SHORT).show();
        }
    }

    public void startWorkout() {
        startWorkoutButton.setVisibility(GONE);
        addExerciseButton.setVisibility(VISIBLE);
        endWorkoutButton.setVisibility(VISIBLE);
        micButton.setVisibility(VISIBLE);

    }

    public void saveExercise() {
        Exercise exercise = new Exercise();
        exercise.setExerciseName(exerciseText.getText().toString());
        exercise.setWeightUsed(weightText.getText().toString());
        exercise.setRepsCompleted(repetitionText.getText().toString());
        exercise.setUserId(mAuth.getCurrentUser().getUid());
        Long tsLong = new Date().getTime();
        String ts = tsLong.toString();
        exercise.setCurrentDate(ts);

        exerciseList.add(exercise);

        exerciseText.getText().clear();
        weightText.getText().clear();
        repetitionText.getText().clear();
    }

    public void endWorkout() {
        Workout newWorkout = new Workout();

        newWorkout.setId(UUID.randomUUID().toString());
        newWorkout.setUserId(mAuth.getCurrentUser().getUid());
        newWorkout.setExerciseList(exerciseList);
        Long tsLong2 = new Date().getTime();
        String ts2 = tsLong2.toString();
        newWorkout.setCurrentDate(ts2);

        FirebaseDatabase.getInstance("https://finalyearproject-e1d79-default-rtdb.europe-west1.firebasedatabase.app").getReference("Workouts")
                .child(newWorkout.getId())
                .setValue(newWorkout).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                Toast.makeText(getActivity(), "Workout Saved", Toast.LENGTH_LONG).show();
                startWorkoutButton.setVisibility(VISIBLE);
                addExerciseButton.setVisibility(GONE);
                endWorkoutButton.setVisibility(GONE);
                micButton.setVisibility(GONE);
                exerciseList.clear();
            } else {
                Toast.makeText(getActivity(), "Failed to save workout", Toast.LENGTH_LONG).show();
            }
        });
    }
}