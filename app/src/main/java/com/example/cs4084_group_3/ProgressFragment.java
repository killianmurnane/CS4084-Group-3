package com.example.cs4084_group_3;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;



import java.util.ArrayList;

public class ProgressFragment extends Fragment {

    private TextView tvSessions, tvMinutes, tvSquatPB, tvBenchPB, tvRunPB, tvStreak, etGoal, etCalories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Bind views
        tvSessions = view.findViewById(R.id.tvSessions);
        tvMinutes = view.findViewById(R.id.tvMinutes);
        tvSquatPB = view.findViewById(R.id.tvSquatPB);
        tvBenchPB = view.findViewById(R.id.tvBenchPB);
        tvRunPB = view.findViewById(R.id.tvRunPB);
        tvStreak = view.findViewById(R.id.tvStreak);
        etGoal = view.findViewById(R.id.etGoal);
        etCalories = view.findViewById(R.id.etCalories);

        // Load progress ONCE
        Progress progress = ProgressStore.readProgress(requireContext());
        if (progress == null) progress = new Progress();

        // Update UI
        readProgress(progress);


    }

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


}