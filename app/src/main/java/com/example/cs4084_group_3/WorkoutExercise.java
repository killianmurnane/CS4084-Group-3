package com.example.cs4084_group_3;

import java.util.List;
import java.util.ArrayList;

/**
 * Exercise class - represents an individual exercise within a workout
 */
public class WorkoutExercise {
    private String name;
    private List<ExerciseSet> sets;

    public WorkoutExercise() {
        this.sets = new ArrayList<>();
    }

    public WorkoutExercise(String name) {
        this.name = name;
        this.sets = new ArrayList<>();


    }

    public void setName(String newName) {
        this.name = newName;
    }

    public String getName() {
        return this.name;
    }

    //adding a new set with weight and reps
    public void addSet(double weight, int reps) {
        sets.add(new ExerciseSet(weight, reps));
    }

    public void addSet(ExerciseSet set) {
        sets.add(set);
    }

    public void removeSet(int index) {
        if (index >= 0 && index < sets.size()) {
            sets.remove(index);
        }
    }

    public ExerciseSet getSet(int index) {
        return sets.get(index);
    }

    public List<ExerciseSet> getSets() {
        if (sets == null) {
            sets = new ArrayList<>();
        }
        return sets;
    }




}


