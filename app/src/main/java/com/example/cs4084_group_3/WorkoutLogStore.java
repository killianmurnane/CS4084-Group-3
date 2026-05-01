package com.example.cs4084_group_3;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class WorkoutLogStore {

    private static final String LOG_FILE_NAME = "workout_log.json";

    public static void addEntry(Context context, WorkoutLog entry) {
        ArrayList<WorkoutLog> entries = getEntries(context);
        entries.add(entry);
        writeEntries(context, entries);
    }

    public static ArrayList<WorkoutLog> getEntries(Context context) {
        try {
            FileInputStream fileInputStream = context.openFileInput(LOG_FILE_NAME);
            InputStreamReader inputStreamReader =
                    new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();

            if (stringBuilder.length() == 0) {
                return new ArrayList<>();
            }

            Gson gson = new Gson();
            ArrayList<WorkoutLog> entries = gson.fromJson(
                    stringBuilder.toString(),
                    new TypeToken<ArrayList<WorkoutLog>>() {}.getType());

            return entries != null ? entries : new ArrayList<>();

        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void writeEntries(Context context, ArrayList<WorkoutLog> entries) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(entries);

            FileOutputStream fileOutputStream =
                    context.openFileOutput(LOG_FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(json.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
