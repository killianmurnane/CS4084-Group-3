package com.example.cs4084_group_3;

import java.util.Arrays;

/**
 * Workouts class
 */
public class Workout {
    private String name;
    private float duration;
    private String description;
    public enum WorkoutLevel {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED
    }
    private WorkoutLevel level;
    private WorkoutExercise[] exercises;

    public Workout() {
        this.exercises = new WorkoutExercise[0];
    }

    public void setName(String newName) {
        this.name = newName;
    }
    public String getName() {
        return this.name;
    }
    public void setDuration(float newDuration){
        this.duration = newDuration;
    }
    public float getDuration(){
        return this.duration;
    }
    public void setDescription(String newDescription) {
        this.description = newDescription;
    }
    public String getDescription() {
        return this.description;
    }
    public void setLevel(WorkoutLevel newLevel){
        this.level = newLevel;
    }
    public String getLevel(){
        switch (this.level){
            case BEGINNER:
                return "Beginner";
            case INTERMEDIATE:
                return "Intermediate";
            case ADVANCED:
                return "Advanced";
            default:
                return "None";
        }
    }
    public void addExercise(String name, int sets, int reps){
        WorkoutExercise exercise = new WorkoutExercise(name, sets, reps);
        this.exercises = Arrays.copyOf(this.exercises, this.exercises.length + 1);
        this.exercises[this.exercises.length - 1] = exercise;
    }
    public WorkoutExercise[] getExercises(){
        return this.exercises;
    }
    public void setExercises(WorkoutExercise[] newExercises) {
        this.exercises = newExercises;
    }
}
