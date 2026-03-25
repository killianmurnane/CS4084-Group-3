package com.example.cs4084_group_3;

import android.content.Context;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public abstract class WorkoutStore {
    public abstract void writeWorkouts(Context context, ArrayList<Workout> values);
    public abstract ArrayList<Workout> getWorkouts(Context context);

    public static class JsonWorkoutStore extends WorkoutStore {
        private final static String workoutsFileName = "workouts.json";

        @Override
        public void writeWorkouts(Context context, ArrayList<Workout> values) {
            try {
                Gson gson = new Gson();
                String json = gson.toJson(values);
                FileOutputStream fileOutputStream = context.openFileOutput(workoutsFileName, Context.MODE_PRIVATE);
                fileOutputStream.write(json.getBytes(StandardCharsets.UTF_8));
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public ArrayList<Workout> getWorkouts(Context context) {
            try {
                FileInputStream fileInputStream = context.openFileInput(workoutsFileName);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                inputStreamReader.close();
                fileInputStream.close();
                
                Gson gson = new Gson();
                ArrayList<Workout> workouts = gson.fromJson(stringBuilder.toString(), new TypeToken<ArrayList<Workout>>(){}.getType());
                return workouts != null ? workouts : new ArrayList<>();
            } catch (FileNotFoundException e) {
                // File doesn't exist yet, return empty list
                return new ArrayList<>();
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
    }

}
