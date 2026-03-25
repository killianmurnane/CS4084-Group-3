package com.example.cs4084_group_3;

/**
 * Exercise class - represents an individual exercise within a workout
 */
public class WorkoutExercise {
    private String name;
    private int sets;
    private int reps;

    public WorkoutExercise() {
    }

    public WorkoutExercise(String name, int sets, int reps) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public String getName() {
        return this.name;
    }

    public void setSets(int newSets) {
        this.sets = newSets;
    }

    public int getSets() {
        return this.sets;
    }

    public void setReps(int newReps) {
        this.reps = newReps;
    }

    public int getReps() {
        return this.reps;
    }
}
