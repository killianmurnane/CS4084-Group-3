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

    private static final String LOG_FILE_PREFIX = "workout_log_";

    private static String getFileName(Context context) {
        return LOG_FILE_PREFIX + AuthStore.getCurrentUserSafeKey(context) + ".json";
    }

    public static void addEntry(Context context, WorkoutLog entry) {
        ArrayList<WorkoutLog> entries = getEntries(context);
        entries.add(entry);
        writeEntries(context, entries);
    }

    public static ArrayList<WorkoutLog> getEntries(Context context) {
        try {
            FileInputStream fis = context.openFileInput(getFileName(context));
            InputStreamReader isr= new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)sb.append(line);

            reader.close();
            isr.close();
            fis.close();

            if (sb.length() == 0) return new ArrayList<>();


            Gson gson = new Gson();
            ArrayList<WorkoutLog> entries = gson.fromJson(
                    sb.toString(), new TypeToken<ArrayList<WorkoutLog>>() {}.getType());
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

            FileOutputStream fos =
                    context.openFileOutput(getFileName(context), Context.MODE_PRIVATE);
            fos.write(json.getBytes(StandardCharsets.UTF_8));
            fos.close();

            // Sync to Firebase, matching the pattern used by WorkoutStore/ProgressStore
            FirebaseSyncManager.syncWorkoutLog(context, entries);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
