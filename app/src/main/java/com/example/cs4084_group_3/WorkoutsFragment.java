package com.example.cs4084_group_3;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

public class WorkoutsFragment extends Fragment implements WorkoutCreateDialog.OnWorkoutCreatedListener {

    private ArrayList<Workout> workouts;
    private LinearLayout myWorkoutsContainer;
    private LinearLayout preMadeWorkoutsContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workouts, container, false);

        ExtendedFloatingActionButton fab = view.findViewById(R.id.fabAddWorkout);
        fab.setOnClickListener(v -> openCreateWorkoutDialog());

        myWorkoutsContainer = view.findViewById(R.id.myWorkoutsContainer);
        preMadeWorkoutsContainer = view.findViewById(R.id.preMadeWorkoutsContainer);

        refreshWorkoutLists();

        return view;
    }

    private void openCreateWorkoutDialog() {
        WorkoutCreateDialog dialog = new WorkoutCreateDialog();
        dialog.show(getChildFragmentManager(), "CreateWorkoutDialog");
    }

    @Override
    public void onWorkoutCreated(Workout workout) {
        refreshWorkoutLists();
    }

    private void refreshWorkoutLists() {
        myWorkoutsContainer.removeAllViews();
        preMadeWorkoutsContainer.removeAllViews();

        loadAndDisplayWorkouts();
        loadAndDisplayPreMadeWorkouts();
    }

    private void loadAndDisplayWorkouts() {
        WorkoutStore.JsonWorkoutStore store = new WorkoutStore.JsonWorkoutStore();
        workouts = store.getWorkouts(requireContext());

        for (Workout workout : workouts) {
            MaterialCardView card = createWorkoutCard(workout, false);
            myWorkoutsContainer.addView(card);
        }
    }

    private void loadAndDisplayPreMadeWorkouts() {
        ArrayList<Workout> preMadeWorkouts = PreMadeWorkout.getPreMadeWorkouts();

        for (Workout workout : preMadeWorkouts) {
            MaterialCardView card = createWorkoutCard(workout, true);
            preMadeWorkoutsContainer.addView(card);
        }
    }

    private MaterialCardView createWorkoutCard(Workout workout, boolean isPreMade) {
        MaterialCardView card = new MaterialCardView(requireContext());

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.topMargin = dpToPx(12);
        card.setLayoutParams(cardParams);

        card.setUseCompatPadding(true);
        card.setRadius(dpToPx(16));

        LinearLayout cardContent = new LinearLayout(requireContext());
        cardContent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        cardContent.setOrientation(LinearLayout.HORIZONTAL);
        cardContent.setGravity(android.view.Gravity.CENTER_VERTICAL);

        int padding = dpToPx(16);
        cardContent.setPadding(padding, padding, padding, padding);

        LinearLayout textCol = new LinearLayout(requireContext());
        textCol.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f));
        textCol.setOrientation(LinearLayout.VERTICAL);

        TextView tvName = new TextView(requireContext());
        tvName.setText(workout.getName());
        tvName.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleSmall);
        tvName.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onSurface));
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        textCol.addView(tvName);

        TextView tvMeta = new TextView(requireContext());
        tvMeta.setText(String.format("%.1f min • %d exercises",
                workout.getDuration(),
                workout.getExercises().size()));
        tvMeta.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodySmall);
        tvMeta.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onSurfaceVariant));

        LinearLayout.LayoutParams metaParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        metaParams.topMargin = dpToPx(4);
        tvMeta.setLayoutParams(metaParams);
        textCol.addView(tvMeta);

        if (workout.getDescription() != null && !workout.getDescription().isEmpty()) {
            TextView tvDescription = new TextView(requireContext());
            tvDescription.setText(workout.getDescription());
            tvDescription.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodySmall);
            tvDescription.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onSurfaceVariant));

            LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            descParams.topMargin = dpToPx(4);
            tvDescription.setLayoutParams(descParams);
            textCol.addView(tvDescription);
        }

        cardContent.addView(textCol);

        LinearLayout buttonCol = new LinearLayout(requireContext());
        buttonCol.setOrientation(LinearLayout.VERTICAL);

        MaterialButton btnStart = new MaterialButton(requireContext());
        btnStart.setText(R.string.btn_start);

        if (isPreMade) {
            btnStart.setOnClickListener(v -> navigateToWorkout(v, workout.getName(), true));
        } else {
            btnStart.setOnClickListener(v -> navigateToWorkout(v, workout.getName(), false));

            MaterialButton btnRename = new MaterialButton(requireContext());
            btnRename.setText("Rename");
            btnRename.setOnClickListener(v -> openRenameDialog(workout));
            buttonCol.addView(btnRename);

            MaterialButton btnDelete = new MaterialButton(requireContext());
            btnDelete.setText("Delete");
            btnDelete.setOnClickListener(v -> openDeleteDialog(workout));
            buttonCol.addView(btnDelete);
        }

        buttonCol.addView(btnStart);
        cardContent.addView(buttonCol);

        card.addView(cardContent);
        return card;
    }

    private void openRenameDialog(Workout workout) {
        EditText input = new EditText(requireContext());
        input.setText(workout.getName());
        input.setSelection(input.getText().length());

        new AlertDialog.Builder(requireContext())
                .setTitle("Rename Workout")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        renameWorkout(workout.getName(), newName);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void renameWorkout(String oldName, String newName) {
        WorkoutStore.JsonWorkoutStore store = new WorkoutStore.JsonWorkoutStore();
        ArrayList<Workout> savedWorkouts = store.getWorkouts(requireContext());

        for (Workout workout : savedWorkouts) {
            if (workout.getName().equals(oldName)) {
                workout.setName(newName);
                break;
            }
        }

        store.writeWorkouts(requireContext(), savedWorkouts);
        refreshWorkoutLists();
    }

    private void navigateToWorkout(View v, String workoutName, boolean isPreMade) {
        Bundle args = new Bundle();
        args.putString("workoutName", workoutName);
        args.putBoolean("isPreMade", isPreMade);

        Navigation.findNavController(v).navigate(R.id.action_workouts_to_activeWorkout, args);
    }

    private void openDeleteDialog(Workout workout) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Workout")
                .setMessage("Are you sure you want to delete " + workout.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteWorkout(workout.getName()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteWorkout(String workoutName) {
        WorkoutStore.JsonWorkoutStore store = new WorkoutStore.JsonWorkoutStore();
        ArrayList<Workout> savedWorkouts = store.getWorkouts(requireContext());

        for (int i = savedWorkouts.size() - 1; i >= 0; i--) {
            if (savedWorkouts.get(i).getName().equals(workoutName)) {
                savedWorkouts.remove(i);
                break;
            }
        }

        store.writeWorkouts(requireContext(), savedWorkouts);
        refreshWorkoutLists();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * requireContext().getResources().getDisplayMetrics().density);
    }
}