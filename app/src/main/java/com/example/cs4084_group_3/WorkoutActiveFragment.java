package com.example.cs4084_group_3;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.text.InputType;
import android.widget.EditText;

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
    private LinearLayout exercisesContainer;

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
        MaterialButton btnAddExercise = view.findViewById(R.id.btnAddExercise);
        TextView tvName               = view.findViewById(R.id.tvWorkoutName);
        TextView tvMeta               = view.findViewById(R.id.tvWorkoutMeta);
        TextView tvDesc               = view.findViewById(R.id.tvWorkoutDescription);
        exercisesContainer = view.findViewById(R.id.exercisesContainer);

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
                for (WorkoutExercise exercise : currentWorkout.getExercises()){
                    addExerciseCard(exercise);
                }
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

        //add exercise
        btnAddExercise.setOnClickListener(v -> {
            AddExerciseDialog dialog = new AddExerciseDialog();
            dialog.setOnExerciseSelectedListener(exerciseName -> {
                WorkoutExercise exercise = currentWorkout.addExercise(exerciseName);
                addExerciseCard(exercise);
            });
            dialog.show(getChildFragmentManager(), "AddExerciseDialog");
        });

        // Finish — stop timer, save workout and go back
        btnFinish.setOnClickListener(v -> {
            isRunning = false;
            handler.removeCallbacks(timerRunnable);
            saveWorkout();
            updateProgress();
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
        if(currentWorkout == null){
            currentWorkout = new Workout(workoutName);
        }
    }

    private void updateTimerDisplay() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }
  
      // ── UPDATE PROGRESS ───────────────────────────────────────────────────────────────

    private void updateProgress() {
        Progress progress = ProgressStore.readProgress(requireContext());

        progress.workoutsCompleted++;

        if (currentWorkout != null) {
            for (WorkoutExercise exercise : currentWorkout.getExercises()) {
                progress.totalSets += exercise.getSets().size();

                for (ExerciseSet set : exercise.getSets()) {
                    progress.totalVolume += set.getWeight() * set.getReps();
                }
            }
        }

        ProgressStore.writeProgress(requireContext(), progress);
    }


    private void saveWorkout() {
        if (currentWorkout == null) return;


        List<WorkoutExercise> exercises = currentWorkout.getExercises();
        for (int i = 0; i < exercises.size(); i++) {
            WorkoutExercise exercise = exercises.get(i);
            MaterialCardView card = (MaterialCardView) exercisesContainer.getChildAt(i);
            if (card == null) continue;

            LinearLayout outerCol = (LinearLayout) card.getChildAt(0);
            LinearLayout setsContainer = (LinearLayout) outerCol.getChildAt(1);

            exercise.getSets().clear();
            for (int s = 0; s < setsContainer.getChildCount(); s++) {
                View setRow = setsContainer.getChildAt(s);
                if (!(setRow instanceof LinearLayout)) continue;
                LinearLayout row = (LinearLayout) setRow;

                EditText weightEdit = (EditText) row.getChildAt(1);
                EditText repsEdit = (EditText) row.getChildAt(2);

                String weightStr = weightEdit.getText().toString().trim();
                String repsStr = repsEdit.getText().toString().trim();

                double weight = weightStr.isEmpty() ? 0 : Double.parseDouble(weightStr);
                int reps = repsStr.isEmpty() ? 0 : Integer.parseInt(repsStr);
                exercise.addSet(weight, reps);
            }
        }

        WorkoutStore.JsonWorkoutStore store = new WorkoutStore.JsonWorkoutStore();
        ArrayList<Workout> workouts = store.getWorkouts(requireContext());
        for (int i = 0; i < workouts.size(); i++) {
            if (workouts.get(i).getName().equals(currentWorkout.getName())) {
                workouts.set(i, currentWorkout);
                break;
            }
        }
        store.writeWorkouts(requireContext(), workouts);
    }

    private void addExerciseCard(WorkoutExercise exercise) {
        MaterialCardView card = new MaterialCardView(requireContext());
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.topMargin = dpToPx(12);
            card.setLayoutParams(cardParams);
            card.setRadius(dpToPx(16));
            card.setCardElevation(dpToPx(2));

        LinearLayout outerCol = new LinearLayout(requireContext());
        outerCol.setOrientation(LinearLayout.VERTICAL);
        outerCol.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        // Header row: name + remove button
        LinearLayout headerRow = new LinearLayout(requireContext());
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(android.view.Gravity.CENTER_VERTICAL);

        TextView tvName = new TextView(requireContext());
        tvName.setText(exercise.getName());
        tvName.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleSmall);
        tvName.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onSurface));
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tvName.setLayoutParams(nameParams);

        MaterialButton btnRemoveExercise = new MaterialButton(requireContext(),
                null, com.google.android.material.R.style.Widget_Material3_Button_TextButton);
        btnRemoveExercise.setText(R.string.btn_remove_exercise);
        btnRemoveExercise.setOnClickListener(v -> {
            int index = exercisesContainer.indexOfChild(card);
            currentWorkout.removeExercise(index);
            exercisesContainer.removeView(card);
        });

        headerRow.addView(tvName);
        headerRow.addView(btnRemoveExercise);

        // Sets container
        LinearLayout setsContainer = new LinearLayout(requireContext());
        setsContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams setsParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        setsParams.topMargin = dpToPx(12);
        setsContainer.setLayoutParams(setsParams);

        // First set added automatically
        addSetRow(setsContainer, 1);

        // Add Set button
        MaterialButton btnAddSet = new MaterialButton(requireContext(),
                null, com.google.android.material.R.style.Widget_Material3_Button_TextButton);
        btnAddSet.setText(R.string.btn_add_set);
        LinearLayout.LayoutParams addSetParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        addSetParams.topMargin = dpToPx(8);
        btnAddSet.setLayoutParams(addSetParams);
        btnAddSet.setOnClickListener(v -> {
            int nextSetNumber = setsContainer.getChildCount() + 1;
            addSetRow(setsContainer, nextSetNumber);
        });

        outerCol.addView(headerRow);
        outerCol.addView(setsContainer);
        outerCol.addView(btnAddSet);
        card.addView(outerCol);
        exercisesContainer.addView(card);
    }

    private void addSetRow(LinearLayout setsContainer, int setNumber) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.topMargin = dpToPx(8);
        row.setLayoutParams(rowParams);

        TextView setLabel = new TextView(requireContext());
        setLabel.setText(String.format("Set %d", setNumber));
        setLabel.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_LabelMedium);
        setLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onSurface));
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                dpToPx(48), LinearLayout.LayoutParams.WRAP_CONTENT);
        setLabel.setLayoutParams(labelParams);

        EditText weightEdit = new EditText(requireContext());
        weightEdit.setHint("kg");
        weightEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        LinearLayout.LayoutParams weightParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        weightParams.setMarginStart(dpToPx(8));
        weightEdit.setLayoutParams(weightParams);

        EditText repsEdit = new EditText(requireContext());
        repsEdit.setHint("reps");
        repsEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams repsParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        repsParams.setMarginStart(dpToPx(8));
        repsEdit.setLayoutParams(repsParams);

        MaterialButton btnRemoveSet = new MaterialButton(requireContext(),
                null, com.google.android.material.R.style.Widget_Material3_Button_TextButton);
        btnRemoveSet.setText(R.string.btn_remove_set);
        btnRemoveSet.setOnClickListener(v -> {
            setsContainer.removeView(row);
            renumberSets(setsContainer);
        });

        row.addView(setLabel);
        row.addView(weightEdit);
        row.addView(repsEdit);
        row.addView(btnRemoveSet);
        setsContainer.addView(row);
    }

    private void renumberSets(LinearLayout setsContainer) {
        for (int i = 0; i < setsContainer.getChildCount(); i++) {
            LinearLayout row = (LinearLayout) setsContainer.getChildAt(i);
            TextView label = (TextView) row.getChildAt(0);
            label.setText(String.format("Set %d", i + 1));
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
