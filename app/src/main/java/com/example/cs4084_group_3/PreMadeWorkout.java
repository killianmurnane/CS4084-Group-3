package com.example.cs4084_group_3;

import java.util.ArrayList;

public class PreMadeWorkout {

    public static ArrayList<Workout> getPreMadeWorkouts() {
        ArrayList<Workout> preMade = new ArrayList<>();

        preMade.add(createWorkout(
                "Chest + Back",
                60,
                "Chest and back strength workout",
                new String[]{
                        "Bench Press",
                        "Barbell Row",
                        "Lat Pull Down",
                        "Incline Chest Press",
                        "Seated Row",
                        "Chest Fly"
                }
        ));

        preMade.add(createWorkout(
                "Arms + Shoulders",
                50,
                "Arms and shoulders workout",
                new String[]{
                        "Preacher Curl",
                        "Shoulder Dumbbell Press",
                        "Overhead Extension",
                        "Lateral Raises",
                        "Hammer Curls"
                }
        ));

        preMade.add(createWorkout(
                "Legs",
                60,
                "Lower body strength workout",
                new String[]{
                        "Squats",
                        "Deadlift",
                        "Calf Raises",
                        "Leg Extensions",
                        "Leg Curls",
                        "Abductor Machine"
                }
        ));

        preMade.add(createWorkout(
                "Full Upper",
                70,
                "Upper body workout",
                new String[]{
                        "Bench Press",
                        "Preacher Curls",
                        "Seated Row",
                        "Shoulder Dumbbell Press",
                        "Incline Dumbbell Press",
                        "Overhead Extension",
                        "Lateral Raises"
                }
        ));

        preMade.add(createWorkout(
                "Bodyweight",
                35,
                "Bodyweight workout",
                new String[]{
                        "Dips",
                        "Pull Ups",
                        "Crunches",
                        "Leg Raises",
                        "Lunges"
                }
        ));

        return preMade;
    }

    public static Workout getPreMadeWorkoutByName(String name) {
        for (Workout workout : getPreMadeWorkouts()) {
            if (workout.getName().equals(name)) {
                return workout;
            }
        }
        return null;
    }

    private static Workout createWorkout(String name, float duration, String description, String[] exercises) {
        Workout workout = new Workout();
        workout.setName(name);
        workout.setDuration(duration);
        workout.setDescription(description);

        for (String exerciseName : exercises) {
            workout.addExercise(exerciseName);
        }

        return workout;
    }
}