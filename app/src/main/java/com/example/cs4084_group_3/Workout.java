package com.example.cs4084_group_3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Workouts class
 */
public class Workout {
    private String name;
    private float duration;
    private String description;
    private List<WorkoutExercise> exercises;

    public Workout() {
        this.exercises = new ArrayList<>();
    }

    public Workout(String name){
        this.name = name;
        this.exercises = new ArrayList<>();
    }

    // name
    public void setName(String newName) {
        this.name = newName;
    }
    public String getName() {
        return this.name;
    }

    //duration
    public void setDuration(float newDuration){
        this.duration = newDuration;
    }
    public float getDuration(){
        return this.duration;
    }

    //description
    public void setDescription(String newDescription) {
        this.description = newDescription;
    }
    public String getDescription() {
        return this.description;
    }

    public WorkoutExercise addExercise(String exerciseName){
        WorkoutExercise exercise = ExerciseData.createExercise(exerciseName);
        exercises.add(exercise);
        return exercise;
    }

    public void addExercise(WorkoutExercise exercise){
        exercises.add(exercise);
    }

    public void removeExercise(int index) {
        if (index >= 0 && index < exercises.size()) {
            exercises.remove(index);
        }
    }

    public WorkoutExercise getExercise(int index){
        return exercises.get(index);
    }

    public List<WorkoutExercise> getExercises(){
        return exercises;
    }
}
