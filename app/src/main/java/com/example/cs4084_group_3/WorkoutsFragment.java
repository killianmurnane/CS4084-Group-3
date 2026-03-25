package com.example.cs4084_group_3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import androidx.navigation.Navigation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class WorkoutsFragment extends Fragment implements WorkoutCreateDialog.OnWorkoutCreatedListener {

    private ArrayList<Workout> workouts;
    private LinearLayout myWorkoutsContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workouts, container, false);

        ExtendedFloatingActionButton fab = view.findViewById(R.id.fabAddWorkout);
        fab.setOnClickListener(v -> openCreateWorkoutDialog());

        // Load workouts from WorkoutStore
        myWorkoutsContainer = view.findViewById(R.id.myWorkoutsContainer);
        loadAndDisplayWorkouts(myWorkoutsContainer);

        return view;
    }

    private void openCreateWorkoutDialog() {
        WorkoutCreateDialog dialog = new WorkoutCreateDialog();
        dialog.show(getChildFragmentManager(), "CreateWorkoutDialog");
    }

    @Override
    public void onWorkoutCreated(Workout workout) {
        // Refresh the workouts list
        myWorkoutsContainer.removeAllViews();
        loadAndDisplayWorkouts(myWorkoutsContainer);
    }

    private void loadAndDisplayWorkouts(LinearLayout container) {
        // Create WorkoutStore instance
        WorkoutStore.JsonWorkoutStore store = new WorkoutStore.JsonWorkoutStore();
        workouts = store.getWorkouts(requireContext());

        // Display each workout
        for (Workout workout : workouts) {
            MaterialCardView card = createWorkoutCard(workout);
            container.addView(card);
        }
    }

    private MaterialCardView createWorkoutCard(Workout workout) {
        MaterialCardView card = new MaterialCardView(requireContext());
        card.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        card.setUseCompatPadding(true);
        card.setRadius(dpToPx(16));
        int marginTop = dpToPx(12);
        LinearLayout.LayoutParams cardParams = (LinearLayout.LayoutParams) card.getLayoutParams();
        cardParams.topMargin = marginTop;
        card.setLayoutParams(cardParams);

        LinearLayout cardContent = new LinearLayout(requireContext());
        cardContent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        cardContent.setOrientation(LinearLayout.HORIZONTAL);
        cardContent.setGravity(android.view.Gravity.CENTER_VERTICAL);
        int padding = dpToPx(16);
        cardContent.setPadding(padding, padding, padding, padding);

        // Text column (name and meta)
        LinearLayout textCol = new LinearLayout(requireContext());
        textCol.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        textCol.setOrientation(LinearLayout.VERTICAL);

        TextView tvName = new TextView(requireContext());
        tvName.setText(workout.getName());
        tvName.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleSmall);
        tvName.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onSurface));
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        textCol.addView(tvName);

        TextView tvMeta = new TextView(requireContext());
        tvMeta.setText(String.format("%.1f min - %s", workout.getDuration(), workout.getLevel()));
        tvMeta.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodySmall);
        tvMeta.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onSurfaceVariant));
        LinearLayout.LayoutParams metaParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        metaParams.topMargin = dpToPx(4);
        tvMeta.setLayoutParams(metaParams);
        textCol.addView(tvMeta);

        cardContent.addView(textCol);

        // Start button
        MaterialButton btnStart = new MaterialButton(requireContext());
        btnStart.setText(R.string.btn_start);
        btnStart.setOnClickListener(v -> navigateToWorkout(v, workout.getName()));
        cardContent.addView(btnStart);

        card.addView(cardContent);
        return card;
    }

    private void navigateToWorkout(View v, String workoutName) {
        Bundle args = new Bundle();
        args.putString("workoutName", workoutName);
        Navigation.findNavController(v).navigate(R.id.action_workouts_to_activeWorkout, args);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * requireContext().getResources().getDisplayMetrics().density);
    }
}
