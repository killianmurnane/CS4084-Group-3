package com.example.cs4084_group_3;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ProgressStore {

    private static final String progressFilePrefix = "progress_";

    private static String getProgressFileName(Context context) {
        return progressFilePrefix + AuthStore.getCurrentUserSafeKey(context) + ".json";
    }

    public static void writeProgress(Context context, Progress progress) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(progress);

            FileOutputStream fos = context.openFileOutput(getProgressFileName(context), Context.MODE_PRIVATE);
            fos.write(json.getBytes(StandardCharsets.UTF_8));
            fos.close();

            FirebaseSyncManager.syncProgress(context, progress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Progress readProgress(Context context) {
        try {
            FileInputStream fis = context.openFileInput(getProgressFileName(context));
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(isr);

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            reader.close();
            isr.close();
            fis.close();

            if (builder.length() == 0) {
                return new Progress();
            }

            Gson gson = new Gson();
            Progress progress = gson.fromJson(builder.toString(), Progress.class);

            return (progress != null) ? progress : new Progress();

        } catch (Exception e) {
            return new Progress();
        }
    }
}