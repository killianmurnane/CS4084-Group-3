package com.example.cs4084_group_3;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.List;

public class WorkoutActiveFragment extends Fragment {

    // ── Timer state ───────────────────────────────────────────────────────────

    private int remainingSeconds = 0;
    private int totalSeconds = 0;
    private boolean isRunning   = false;
    private final Handler handler  = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;

    // ── Views ─────────────────────────────────────────────────────────────────

    private TextView        tvTimer;
    private MaterialButton  btnStartPause;

    // ── Workout data ──────────────────────────────────────────────────────────

    private Workout currentWorkout;

    // ── Fragment lifecycle ────────────────────────────────────────────────────

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_active_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Resolve workout name from navigation argument
        String workoutName = getArguments() != null
            ? getArguments().getString("workoutName", "")
            : "";

        // Load and find the workout from WorkoutStore
        loadWorkout(workoutName);

        // Bind views
        tvTimer          = view.findViewById(R.id.tvTimer);
        btnStartPause    = view.findViewById(R.id.btnStartPause);
        MaterialButton btnReset       = view.findViewById(R.id.btnReset);
        MaterialButton btnFinish      = view.findViewById(R.id.btnFinishWorkout);
        TextView tvName               = view.findViewById(R.id.tvWorkoutName);
        TextView tvMeta               = view.findViewById(R.id.tvWorkoutMeta);
        TextView tvDesc               = view.findViewById(R.id.tvWorkoutDescription);
        LinearLayout exercisesContainer = view.findViewById(R.id.exercisesContainer);

        // Populate workout info from loaded workout
        if (currentWorkout != null) {
            tvName.setText(currentWorkout.getName());
            tvMeta.setText(String.format("%.1f min",
                    currentWorkout.getDuration()));
            tvDesc.setText(currentWorkout.getDescription());
            
            // Initialize timer with workout duration (convert minutes to seconds)
            totalSeconds = Math.round(currentWorkout.getDuration() * 60);
            remainingSeconds = totalSeconds;
            updateTimerDisplay();
            
            // Build exercise cards from the workout's exercises
            if (currentWorkout.getExercises() != null && !currentWorkout.getExercises().isEmpty()) {
                buildExerciseCards(exercisesContainer, currentWorkout.getExercises());
            }
        }

        // Timer runnable — decrements every second
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    remainingSeconds--;
                    updateTimerDisplay();
                    handler.postDelayed(this, 1000);
                }
            }
        };

        // Start / Pause
        btnStartPause.setOnClickListener(v -> {
            if (isRunning) {
                isRunning = false;
                handler.removeCallbacks(timerRunnable);
                btnStartPause.setText(R.string.btn_resume_timer);
            } else {
                isRunning = true;
                handler.postDelayed(timerRunnable, 1000);
                btnStartPause.setText(R.string.btn_pause_timer);
            }
        });

        // Reset
        btnReset.setOnClickListener(v -> {
            isRunning = false;
            handler.removeCallbacks(timerRunnable);
            remainingSeconds = totalSeconds;
            updateTimerDisplay();
            btnStartPause.setText(R.string.btn_start_timer);
        });

        // Finish — stop timer and go back
        btnFinish.setOnClickListener(v -> {
            isRunning = false;
            handler.removeCallbacks(timerRunnable);
            Navigation.findNavController(v).popBackStack();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isRunning = false;
        handler.removeCallbacks(timerRunnable);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void loadWorkout(String workoutName) {
        // Load all workouts from storage
        WorkoutStore.JsonWorkoutStore store = new WorkoutStore.JsonWorkoutStore();
        ArrayList<Workout> workouts = store.getWorkouts(requireContext());
        
        // Find the workout matching the provided name
        for (Workout workout : workouts) {
            if (workout.getName().equals(workoutName)) {
                currentWorkout = workout;
                break;
            }
        }
    }

    private void updateTimerDisplay() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    private void buildExerciseCards(LinearLayout container, List<WorkoutExercise> exercises) {
        int sidePad   = dpToPx(16);
        int topMargin = dpToPx(12);

        for (int i = 0; i < exercises.size(); i++) {
            WorkoutExercise exercise = exercises.get(i);
            MaterialCardView card = new MaterialCardView(requireContext());
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.topMargin = topMargin;
            card.setLayoutParams(cardParams);
            card.setRadius(dpToPx(16));
            card.setCardElevation(dpToPx(2));

            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            row.setPadding(sidePad, sidePad, sidePad, sidePad);

            // Exercise number badge
            TextView badge = new TextView(requireContext());
            LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(dpToPx(32), dpToPx(32));
            badgeParams.setMarginEnd(dpToPx(14));
            badge.setLayoutParams(badgeParams);
            badge.setText(String.valueOf(i + 1));
            badge.setGravity(android.view.Gravity.CENTER);
            badge.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_LabelLarge);
            badge.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onPrimary));
            badge.setBackground(createBadgeBackground());

            // Name + sets/reps column
            LinearLayout textCol = new LinearLayout(requireContext());
            textCol.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams textColParams = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            textCol.setLayoutParams(textColParams);

            TextView tvExName = new TextView(requireContext());
            tvExName.setText(exercise.getName());
            tvExName.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleSmall);
            tvExName.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onSurface));

            TextView tvExSets = new TextView(requireContext());
            tvExSets.setText(String.format("%d sets", exercise.getSets().size()));
            tvExSets.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodySmall);
            tvExSets.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onSurfaceVariant));

            textCol.addView(tvExName);
            textCol.addView(tvExSets);
            row.addView(badge);
            row.addView(textCol);
            card.addView(row);
            container.addView(card);
        }
    }

    private android.graphics.drawable.GradientDrawable createBadgeBackground() {
        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        bg.setColor(ContextCompat.getColor(requireContext(), R.color.md_theme_dark_primary));
        return bg;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * requireContext().getResources().getDisplayMetrics().density);
    }
}
