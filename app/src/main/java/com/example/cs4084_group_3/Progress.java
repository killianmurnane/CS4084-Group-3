package com.example.cs4084_group_3;

import java.util.HashMap;
import java.util.Map;

public class Progress {

    public int workoutsCompleted;
    public int totalSets;
    public int totalReps;
    public double totalVolume;
    public int totalMinutes;

    // Personal Bests
    public double squatPB;
    public double benchPB;
    public double deadliftPB;

    // Running PB (time in minutes)
    public double runPB;

    // Streaks
    public int currentStreak;
    public int longestStreak;

    // Last workout date
    public String lastWorkoutDate;
    public Map<String, Integer> dailyMinutes;

    // Goals
    public String goal;

    // Weekly calories
    public int weeklyCalories;

    public Progress() {
        workoutsCompleted = 0;
        totalSets = 0;
        totalReps = 0;
        totalVolume = 0;
        totalMinutes = 0;

        squatPB = 0;
        benchPB = 0;
        deadliftPB = 0;
        runPB = 0;

        currentStreak = 0;
        longestStreak = 0;

        lastWorkoutDate = "";
        dailyMinutes = new HashMap<>();

        goal = "";
        weeklyCalories = 0;
    }
}