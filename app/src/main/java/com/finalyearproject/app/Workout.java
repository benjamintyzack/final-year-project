package com.finalyearproject.app;

import java.util.ArrayList;
import java.util.List;

public class Workout {
    String id;
    String userId;
    List<Exercise> exerciseList = new ArrayList<>();
    String currentDate; //Date the workout was completed, change variable name to reflect this

    public Workout() {

    }

    public Workout(String id, String userId, List<Exercise> exerciseList) {
        this.id = id;
        this.userId = userId;
        this.exerciseList = exerciseList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Exercise> getExerciseList() {
        return exerciseList;
    }

    public void setExerciseList(List<Exercise> exerciseList) {
        this.exerciseList = exerciseList;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
}
