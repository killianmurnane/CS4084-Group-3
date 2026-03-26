package com.example.cs4084_group_3;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ExerciseData {


    public enum MuscleGroup {
        Chest, Back, Legs, Biceps, Triceps, Shoulders, Core
    }

        private static final Map<MuscleGroup, List<String>> exerciseMap = new EnumMap<>(MuscleGroup.class);

        static {
            exerciseMap.put(MuscleGroup.Chest, Arrays.asList("Bench Press", "Incline Bench Press", "Chest Fly", "Incline dumbbell press"));
            exerciseMap.put(MuscleGroup.Back, Arrays.asList("Pull-ups", "Chin Ups", "Lat Pulldown", "Barbell Row", "Dumbbell Row", "Machine Row"));
            exerciseMap.put(MuscleGroup.Legs, Arrays.asList("Squats", "Lunges", "Leg Press", "Leg Curls", "Calf Raises", "Deadlifts", "Leg Extensions", "Adductor Machine", "Abductor Machine"));
            exerciseMap.put(MuscleGroup.Biceps, Arrays.asList("Barbell Curl", "Dumbbell Curl", "Hammer Curl", "Preacher Curl"));
            exerciseMap.put(MuscleGroup.Triceps, Arrays.asList("Dips", "Pushdowns", "Overhead Extensions", "JM Press"));
            exerciseMap.put(MuscleGroup.Shoulders, Arrays.asList("Shoulder Dumbbell Press", "Lateral Raises", "Front Raises", "Reverse Fly"));
            exerciseMap.put(MuscleGroup.Core, Arrays.asList("Crunch Machine", "Leg Raises", "Crunches"));
        }

        public static List<String> getExerciseGroup(MuscleGroup group){
            return exerciseMap.get(group);
        }

        public static MuscleGroup[] getMuscleGroups(){
            return MuscleGroup.values();
        }

        public static WorkoutExercise createExercise(String exerciseName){
            return new WorkoutExercise(exerciseName);
        }
    }
