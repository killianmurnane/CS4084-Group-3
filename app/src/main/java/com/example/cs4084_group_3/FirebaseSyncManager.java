package com.example.cs4084_group_3;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseSyncManager {

    private static final String TAG = "FirebaseSync";

    public interface SyncCallback {
        void onComplete(boolean success, @Nullable String errorMessage);
    }

    private static DatabaseReference getUserRoot(Context context) {
        if (!AuthStore.isLoggedIn(context)) {
            return null;
        }
        String userKey = AuthStore.getCurrentUserFirebaseKey(context);
        return FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userKey);
    }

    public static void syncProfile(Context context, Profile profile) {
        try {
            DatabaseReference userRoot = getUserRoot(context);
            if (userRoot == null || profile == null) {
                Log.w(TAG, "Skipping profile sync: no logged-in user or null profile");
                return;
            }

            userRoot.child("profile").setValue(profile, (error, ref) -> {
                if (error != null) {
                    Log.e(TAG, "Profile sync failed: " + error.getMessage());
                }
            });

            Map<String, Object> meta = new HashMap<>();
            meta.put("email", AuthStore.getCurrentUserEmail(context));
            meta.put("updatedAt", System.currentTimeMillis());
            userRoot.child("meta").updateChildren(meta, (error, ref) -> {
                if (error != null) {
                    Log.e(TAG, "Profile meta sync failed: " + error.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Profile sync threw exception", e);
        }
    }

    public static void syncProgress(Context context, Progress progress) {
        try {
            DatabaseReference userRoot = getUserRoot(context);
            if (userRoot == null || progress == null) {
                Log.w(TAG, "Skipping progress sync: no logged-in user or null progress");
                return;
            }

            userRoot.child("progress").setValue(progress, (error, ref) -> {
                if (error != null) {
                    Log.e(TAG, "Progress sync failed: " + error.getMessage());
                }
            });
            userRoot.child("meta").child("updatedAt").setValue(System.currentTimeMillis(), (error, ref) -> {
                if (error != null) {
                    Log.e(TAG, "Progress meta sync failed: " + error.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Progress sync threw exception", e);
        }
    }

    public static void syncWorkouts(Context context, ArrayList<Workout> workouts) {
        try {
            DatabaseReference userRoot = getUserRoot(context);
            if (userRoot == null || workouts == null) {
                Log.w(TAG, "Skipping workouts sync: no logged-in user or null workouts");
                return;
            }

            userRoot.child("workouts").setValue(workouts, (error, ref) -> {
                if (error != null) {
                    Log.e(TAG, "Workouts sync failed: " + error.getMessage());
                }
            });
            userRoot.child("meta").child("updatedAt").setValue(System.currentTimeMillis(), (error, ref) -> {
                if (error != null) {
                    Log.e(TAG, "Workouts meta sync failed: " + error.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Workouts sync threw exception", e);
        }
    }

    public static void syncWorkoutLog(Context context, ArrayList<WorkoutLog> entries) {
        try {
            DatabaseReference userRoot = getUserRoot(context);
            if (userRoot == null || entries == null) {
                Log.w(TAG, "Skipping workout log sync: no logged-in user or null entries");
                return;
            }

            userRoot.child("workoutLog").setValue(entries, (error, ref) -> {
                if (error != null) {
                    Log.e(TAG, "Workout log sync failed: " + error.getMessage());
                }
            });
            userRoot.child("meta").child("updatedAt").setValue(
                    System.currentTimeMillis(), (error, ref) -> {
                        if (error != null) {
                            Log.e(TAG, "Workout log meta sync failed: " + error.getMessage());
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Workout log sync threw exception", e);
        }
    }

    public static void syncAllFromLocal(Context context) {
        syncAllFromLocal(context, null);
    }

    public static void syncAllFromLocal(Context context, @Nullable SyncCallback callback) {
        try {
            DatabaseReference userRoot = getUserRoot(context);
            if (userRoot == null) {
                if (callback != null) {
                    callback.onComplete(false, "No logged-in user session");
                }
                return;
            }

            Profile profile = ProfileStore.readProfile(context);
            Progress progress = ProgressStore.readProgress(context);
            ArrayList<Workout> workouts = new WorkoutStore.JsonWorkoutStore().getWorkouts(context);

            Map<String, Object> meta = new HashMap<>();
            meta.put("email", AuthStore.getCurrentUserEmail(context));
            meta.put("updatedAt", System.currentTimeMillis());

            // Use setValue per child so POJOs are serialized correctly
            ArrayList<WorkoutLog> workoutLog = WorkoutLogStore.getEntries(context);
            userRoot.child("workoutLog").setValue(workoutLog);
            userRoot.child("profile").setValue(profile);
            userRoot.child("progress").setValue(progress);
            userRoot.child("workouts").setValue(workouts);
            userRoot.child("meta").setValue(meta, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error != null) {
                        Log.e(TAG, "Full sync failed: " + error.getMessage());
                        if (callback != null) {
                            callback.onComplete(false, error.getMessage());
                        }
                    } else {
                        Log.i(TAG, "Full sync completed for user: " + AuthStore.getCurrentUserEmail(context));
                        if (callback != null) {
                            callback.onComplete(true, null);
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Full sync threw exception", e);
            if (callback != null) {
                callback.onComplete(false, e.getMessage());
            }
        }
    }
}