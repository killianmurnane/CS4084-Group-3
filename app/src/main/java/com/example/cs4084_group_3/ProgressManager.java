package com.example.cs4084_group_3;

import android.content.Context;

import java.time.LocalDate;
import java.util.HashMap;

public class ProgressManager {

    public static void updateAfterWorkout(Context context,
                                          int sets,
                                          int reps,
                                          double weight,
                                          int durationMinutes,
                                          double squat,
                                          double bench,
                                          double deadlift) {

        Progress progress = ProgressStore.readProgress(context);
        if (progress == null) progress = new Progress();

        // --- BASIC STATS ---
        progress.workoutsCompleted++;
        progress.totalSets += sets;
        progress.totalReps += reps;
        progress.totalVolume += sets * reps * weight;
        progress.totalMinutes += durationMinutes;

        // --- DAILY TRACKING ---
        String today = LocalDate.now().toString();

        if (progress.dailyMinutes == null) {
            progress.dailyMinutes = new HashMap<>();
        }

        int current = progress.dailyMinutes.containsKey(today)
                ? progress.dailyMinutes.get(today)
                : 0;

        progress.dailyMinutes.put(today, current + durationMinutes);

        // --- PERSONAL BESTS ---
        if (squat > progress.squatPB) progress.squatPB = squat;
        if (bench > progress.benchPB) progress.benchPB = bench;
        if (deadlift > progress.deadliftPB) progress.deadliftPB = deadlift;

        // --- STREAK LOGIC ---
        LocalDate todayDate = LocalDate.now();

        if (progress.lastWorkoutDate != null && !progress.lastWorkoutDate.isEmpty()) {
            LocalDate lastDate = LocalDate.parse(progress.lastWorkoutDate);

            if (lastDate.plusDays(1).equals(todayDate)) {
                progress.currentStreak++;
            } else if (!lastDate.equals(todayDate)) {
                progress.currentStreak = 1;
            }
        } else {
            progress.currentStreak = 1;
        }

        if (progress.currentStreak > progress.longestStreak) {
            progress.longestStreak = progress.currentStreak;
        }

        progress.lastWorkoutDate = todayDate.toString();

        ProgressStore.writeProgress(context, progress);
    }
}