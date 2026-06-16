package com.example.cs4084_group_3;

import java.util.ArrayList;
import java.util.List;

public class WorkoutLog {

    private String workoutName;
    private String date;
    private int durationMinutes;
    private List<WorkoutExercise> exercises;

    public WorkoutLog() {
        this.exercises = new ArrayList<>();
    }

    public WorkoutLog(String workoutName, String date, int durationMinutes,
                       List<WorkoutExercise> exercises) {
        this.workoutName     = workoutName;
        this.date            = date;
        this.durationMinutes = durationMinutes;
        this.exercises       = exercises != null ? exercises : new ArrayList<>();
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public List<WorkoutExercise> getExercises() {
        if (exercises == null) {
            exercises = new ArrayList<>();
        }
        return exercises;
    }

    public void setExercises(List<WorkoutExercise> exercises) {
        this.exercises = exercises;
    }
}
