package com.example.cs4084_group_3;

public class ExerciseSet {
    //weight in kg and reps
    private double weight;
    private int reps;

    public ExerciseSet() {}

    public ExerciseSet(double weight, int reps) {
        this.weight = weight;
        this.reps = reps;
    }

    //set weight
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    //set reps
    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

}