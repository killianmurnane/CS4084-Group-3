package com.example.cs4084_group_3;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ProfileStore {

    private static final String profileFileName = "profile.json";

    public static void writeProfile(Context context, Profile profile) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(profile);

            FileOutputStream fos = context.openFileOutput(profileFileName, Context.MODE_PRIVATE);
            fos.write(json.getBytes(StandardCharsets.UTF_8));
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Profile readProfile(Context context) {
        try {
            FileInputStream fis = context.openFileInput(profileFileName);
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

            Gson gson = new Gson();
            Profile profile = gson.fromJson(builder.toString(), Profile.class);

            return profile != null ? profile : new Profile();

        } catch (Exception e) {

            return new Profile();
        }
    }
}
