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

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class WorkoutLogDetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_log_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String workoutName = getArguments() != null
                ? getArguments().getString("workoutName", "") : "";
        String workoutDate = getArguments() != null
                ? getArguments().getString("workoutDate", "") : "";

        WorkoutLog entry = findEntry(workoutName, workoutDate);
        if (entry == null) return;

        TextView tvName = view.findViewById(R.id.tvDetailWorkoutName);
        TextView tvDate = view.findViewById(R.id.tvDetailDate);
        TextView tvDuration = view.findViewById(R.id.tvDetailDuration);
        LinearLayout exercisesContainer = view.findViewById(R.id.detailExercisesContainer);

        tvName.setText(entry.getWorkoutName());
        tvDate.setText(entry.getDate());
        tvDuration.setText(entry.getDurationMinutes() + " min");

        for (WorkoutExercise exercise : entry.getExercises()) {
            MaterialCardView card = buildExerciseCard(exercise);
            exercisesContainer.addView(card);
        }
    }

    private WorkoutLog findEntry(String workoutName, String workoutDate) {
        ArrayList<WorkoutLog> entries = WorkoutLogStore.getEntries(requireContext());
        for (int i = entries.size() - 1; i >= 0; i--) {
            WorkoutLog e = entries.get(i);
            if (e.getWorkoutName().equals(workoutName) && e.getDate().equals(workoutDate)) {
                return e;
            }
        }
        return null;
    }

    private MaterialCardView buildExerciseCard(WorkoutExercise exercise) {
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

        TextView tvName = new TextView(requireContext());
        tvName.setText(exercise.getName());
        tvName.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleSmall);
        tvName.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onSurface));
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        outerCol.addView(tvName);

        LinearLayout headerRow = new LinearLayout(requireContext());
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        headerParams.topMargin = dpToPx(10);
        headerRow.setLayoutParams(headerParams);

        headerRow.addView(makeHeaderCell("Set", dpToPx(48)));
        headerRow.addView(makeHeaderCell("Weight", 0));
        headerRow.addView(makeHeaderCell("Reps", 0));

        outerCol.addView(headerRow);

        if (exercise.getSets() != null) {
            for (int i = 0; i < exercise.getSets().size(); i++) {
                ExerciseSet set = exercise.getSets().get(i);
                outerCol.addView(buildSetRow(i + 1, set));
            }
        }

        card.addView(outerCol);
        return card;
    }

    private TextView makeHeaderCell(String text, int fixedWidth) {
        TextView tv = new TextView(requireContext());
        tv.setText(text);
        tv.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_LabelMedium);
        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onSurfaceVariant));

        LinearLayout.LayoutParams params = fixedWidth > 0
                ? new LinearLayout.LayoutParams(fixedWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
                : new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tv.setLayoutParams(params);
        return tv;
    }

    private LinearLayout buildSetRow(int setNumber, ExerciseSet set) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.topMargin = dpToPx(8);
        row.setLayoutParams(rowParams);

        TextView tvSet = new TextView(requireContext());
        tvSet.setText(String.format("Set %d", setNumber));
        tvSet.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(48), LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView tvWeight = new TextView(requireContext());
        tvWeight.setText(set.getWeight() > 0 ? set.getWeight() + " kg" : "— kg");
        tvWeight.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvReps = new TextView(requireContext());
        tvReps.setText(set.getReps() > 0 ? set.getReps() + " reps" : "— reps");
        tvReps.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        row.addView(tvSet);
        row.addView(tvWeight);
        row.addView(tvReps);

        return row;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * requireContext().getResources().getDisplayMetrics().density);
    }
}