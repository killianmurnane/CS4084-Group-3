package com.example.cs4084_group_3;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;



import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class ProgressFragment extends Fragment {

    private TextView tvSessions, tvMinutes, tvSquatPB, tvBenchPB, tvRunPB, tvStreak, etGoal, etCalories;

    //chart views
    private Spinner exerciseSpinner;
    private ExerciseProgressChart progressChart;
    private TextView tvChartMetricLabel;
    private Spinner metricSpinner;
    private TextView tvNoData;

    private final List<String> exerciseNames = new ArrayList<>();

    private ArrayList<WorkoutLog> logEntries;

    //spinner options
    private static final String METRIC_MAX_WEIGHT = "Max Weight (kg)";
    private static final String METRIC_TOTAL_VOL = "Total Volume (kg)";
    private static final String METRIC_MAX_REPS = "Max Reps";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Bind stat views
        tvSessions = view.findViewById(R.id.tvSessions);
        tvMinutes = view.findViewById(R.id.tvMinutes);
        tvSquatPB = view.findViewById(R.id.tvSquatPB);
        tvBenchPB = view.findViewById(R.id.tvBenchPB);
        tvRunPB = view.findViewById(R.id.tvRunPB);
        tvStreak = view.findViewById(R.id.tvStreak);
        etGoal = view.findViewById(R.id.etGoal);
        etCalories = view.findViewById(R.id.etCalories);

        //bind chart views
        exerciseSpinner = view.findViewById(R.id.spinnerExercise);
        metricSpinner = view.findViewById(R.id.spinnerMetric);
        progressChart = view.findViewById(R.id.exerciseProgressChart);
        tvNoData = view.findViewById(R.id.tvChartNoData);
        tvChartMetricLabel = view.findViewById(R.id.tvChartMetricLabel);


        // Load data
        Progress progress = ProgressStore.readProgress(requireContext());
        if (progress == null) progress = new Progress();
        readProgress(progress);

        logEntries = WorkoutLogStore.getEntries(requireContext());
        buildExerciseList();

        //setup metric spinner
        List<String> metrics = Arrays.asList(METRIC_MAX_WEIGHT, METRIC_TOTAL_VOL, METRIC_MAX_REPS);
        ArrayAdapter<String> metricAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, metrics);
        metricAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        metricSpinner.setAdapter(metricAdapter);

        //setup exercise spinner
        ArrayAdapter<String> exerciseAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, exerciseNames);
        exerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseSpinner.setAdapter(exerciseAdapter);

        //spinner listeners
        AdapterView.OnItemSelectedListener refreshListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                refreshChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { /* no-op */ }
        };

        exerciseSpinner.setOnItemSelectedListener(refreshListener);
        metricSpinner.setOnItemSelectedListener(refreshListener);

        //Initial chart refresh
        refreshChart();
    }


    //stats helpers
    private void readProgress(Progress progress) {

        tvSessions.setText(String.valueOf(progress.workoutsCompleted));
        tvMinutes.setText(progress.totalMinutes + " min");

        tvSquatPB.setText(String.format("%.1f kg", progress.squatPB));
        tvBenchPB.setText(String.format("%.1f kg", progress.benchPB));
        tvRunPB.setText(String.format("%.1f min", progress.runPB));

        tvStreak.setText(progress.currentStreak + " day streak");

        etGoal.setText(progress.goal);
        etCalories.setText(String.valueOf(progress.weeklyCalories));
    }

    @Override
    public void onPause() {
        super.onPause();

        Progress progress = ProgressStore.readProgress(requireContext());
        if (progress == null) progress = new Progress();

        progress.goal = etGoal.getText().toString();

        String calText = etCalories.getText().toString();
        progress.weeklyCalories = calText.isEmpty() ? 0 : Integer.parseInt(calText);

        ProgressStore.writeProgress(requireContext(), progress);
    }

    //chart helpers
    private void buildExerciseList() {
        exerciseNames.clear();
        for (WorkoutLog entry : logEntries) {
            for (WorkoutExercise ex : entry.getExercises()) {
                if (ex.getName() != null && !exerciseNames.contains(ex.getName())) {
                    exerciseNames.add(ex.getName());
                }
            }
        }
        Collections.sort(exerciseNames);

        if (exerciseNames.isEmpty()) {
            exerciseNames.add("No exercises logged yet");
        }
    }

    //rebuild chart for current metric and exercise
    private void refreshChart() {
        if (exerciseSpinner.getAdapter() == null
                || exerciseSpinner.getAdapter().isEmpty()
                || logEntries == null || logEntries.isEmpty()) {
            showNoData();
            return;
        }

        String selectedExercise = (String) exerciseSpinner.getSelectedItem();
        String selectedMetric   = (String) metricSpinner.getSelectedItem();

        if (selectedExercise == null || selectedExercise.equals("No exercises logged yet")) {
            showNoData();
            return;
        }

        // collect data from log entries
        List<String> labels = new ArrayList<>();
        List<Float>  values = new ArrayList<>();

        for (WorkoutLog entry : logEntries) {
            for (WorkoutExercise ex : entry.getExercises()) {
                if (selectedExercise.equals(ex.getName()) && ex.getSets() != null && !ex.getSets().isEmpty()) {
                    float metricValue = computeMetric(ex, selectedMetric);
                    if (metricValue > 0) {
                        labels.add(shortDate(entry.getDate()));
                        values.add(metricValue);
                    }
                    break; // only take the first occurrence per session
                }
            }
        }

        if (values.isEmpty()) {
            showNoData();
            return;
        }

        tvNoData.setVisibility(View.GONE);
        progressChart.setVisibility(View.VISIBLE);
        tvChartMetricLabel.setText(selectedExercise + " — " + selectedMetric);
        progressChart.setData(labels, values, metricUnit(selectedMetric));
    }


    private float computeMetric(WorkoutExercise ex, String metric) {
        float result = 0f;
        switch (metric) {
            case METRIC_MAX_WEIGHT:
                for (ExerciseSet s : ex.getSets()) {
                    if ((float) s.getWeight() > result) result = (float) s.getWeight();
                }
                break;
            case METRIC_TOTAL_VOL:
                for (ExerciseSet s : ex.getSets()) {
                    result += (float) s.getWeight() * s.getReps();
                }
                break;
            case METRIC_MAX_REPS:
                for (ExerciseSet s : ex.getSets()) {
                    if (s.getReps() > result) result = s.getReps();
                }
                break;
        }
        return result;
    }

    // Y axis unit label
    private String metricUnit(String metric) {
        if (METRIC_MAX_REPS.equals(metric)) return "reps";
        return "kg";
    }

    // date formatting eg "Mon 10 Feb 2026" → "10 Feb" for axis labels
    private String shortDate(String fullDate) {
        if (fullDate == null) return "";
        String[] parts = fullDate.split(" ");
        // Format: "Mon 10 Feb 2026"
        if (parts.length >= 4) return parts[1] + " " + parts[2];
        // Fallback: return as-is
        return fullDate;
    }

    private void showNoData() {
        progressChart.setVisibility(View.GONE);
        tvNoData.setVisibility(View.VISIBLE);
        tvChartMetricLabel.setText("");
        progressChart.setData(null, null, null); // reset
    }




}