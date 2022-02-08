package com.finalyearproject.app;

import static android.view.View.*;
import static android.widget.Toast.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class WorkoutPage extends AppCompatActivity implements OnTouchListener, OnClickListener {

    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private EditText exerciseText, weightText, repetitionText;
    private ImageView micButton;
    private Button startWorkoutButton, addExerciseButton, endWorkoutButton;
    final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    private List<Exercise> exerciseList = new ArrayList<>();

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_page);

        mAuth = FirebaseAuth.getInstance();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }

        exerciseText = findViewById(R.id.exercise);
        weightText = findViewById(R.id.weight);
        repetitionText = findViewById(R.id.repetitions);
        micButton = findViewById(R.id.button);
        startWorkoutButton = findViewById(R.id.startWorkout);
        startWorkoutButton.setOnClickListener(this);
        addExerciseButton = findViewById(R.id.addExercise);
        addExerciseButton.setOnClickListener(this);
        endWorkoutButton = findViewById(R.id.endWorkout);
        endWorkoutButton.setOnClickListener(this);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

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
                if (exerciseText.getText().toString().isEmpty()) {
                    exerciseText.setText(data.get(0));
                }
                else if (!exerciseText.getText().toString().isEmpty() && weightText.getText().toString().isEmpty()) {
                    weightText.setText(data.get(0));
                }
                else if (!exerciseText.getText().toString().isEmpty() && !weightText.getText().toString().isEmpty() && repetitionText.getText().toString().isEmpty()) {
                    repetitionText.setText(data.get(0));
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        micButton.setOnTouchListener(this);
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

        FirebaseDatabase.getInstance("https://finalyearproject-e1d79-default-rtdb.europe-west1.firebasedatabase.app").getReference("Workouts")
                .child(newWorkout.getId())
                .setValue(newWorkout).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                Toast.makeText(WorkoutPage.this, "Workout Saved", Toast.LENGTH_LONG).show();
                startWorkoutButton.setVisibility(VISIBLE);
                addExerciseButton.setVisibility(GONE);
                endWorkoutButton.setVisibility(GONE);
                micButton.setVisibility(GONE);
                exerciseList.clear();
            } else {
                Toast.makeText(WorkoutPage.this, "Failed to save workout", Toast.LENGTH_LONG).show();
            }
        });
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
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                makeText(this,"Permission Granted", LENGTH_SHORT).show();
        }
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
}