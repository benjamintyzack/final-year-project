package com.finalyearproject.app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Exercise {
    String exerciseName, weightUsed, repsCompleted, userId;
    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

    Exercise() {

    }

    Exercise(String exerciseName, String weightUsed, String repsCompleted, String userId) {
        this.exerciseName = exerciseName;
        this.weightUsed = weightUsed;
        this.repsCompleted = repsCompleted;
        this.userId = userId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getWeightUsed() {
        return weightUsed;
    }

    public void setWeightUsed(String weightUsed) {
        this.weightUsed = weightUsed;
    }

    public String getRepsCompleted() {
        return repsCompleted;
    }

    public void setRepsCompleted(String repsCompleted) {
        this.repsCompleted = repsCompleted;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
}
