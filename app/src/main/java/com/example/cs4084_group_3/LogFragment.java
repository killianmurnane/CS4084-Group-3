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
import androidx.navigation.Navigation;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Collections;

public class LogFragment extends Fragment {

    private LinearLayout logContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logContainer = view.findViewById(R.id.logContainer);
        loadAndDisplayLog();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (logContainer != null) {
            logContainer.removeAllViews();
            loadAndDisplayLog();
        }
    }

    private void loadAndDisplayLog() {
        ArrayList<WorkoutLog> entries = WorkoutLogStore.getEntries(requireContext());

        Collections.reverse(entries);

        TextView tvEmpty = requireView().findViewById(R.id.tvEmptyLog);

        if (entries.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            logContainer.setVisibility(View.GONE);
            return;
        }

        tvEmpty.setVisibility(View.GONE);
        logContainer.setVisibility(View.VISIBLE);

        for (WorkoutLog entry : entries) {
            MaterialCardView card = buildLogCard(entry);
            logContainer.addView(card);
        }
    }

    private MaterialCardView buildLogCard(WorkoutLog entry) {
        MaterialCardView card = new MaterialCardView(requireContext());

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.topMargin = dpToPx(16);
        card.setLayoutParams(cardParams);

        card.setRadius(dpToPx(20));
        card.setUseCompatPadding(true);
        card.setClickable(true);
        card.setFocusable(true);

        card.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("workoutName", entry.getWorkoutName());
            args.putString("workoutDate", entry.getDate());

            Navigation.findNavController(v)
                    .navigate(R.id.action_log_to_logDetail, args);
        });

        LinearLayout content = new LinearLayout(requireContext());
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dpToPx(20), dpToPx(18), dpToPx(20), dpToPx(18));
        content.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout headerRow = new LinearLayout(requireContext());
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView tvDate = new TextView(requireContext());
        tvDate.setText(entry.getDate());
        tvDate.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_LabelMedium);
        tvDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_dark_primary));

        LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );
        tvDate.setLayoutParams(dateParams);

        TextView tvDuration = new TextView(requireContext());
        tvDuration.setText(entry.getDurationMinutes() + " min");
        tvDuration.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_LabelMedium);
        tvDuration.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onSurfaceVariant));

        headerRow.addView(tvDate);
        headerRow.addView(tvDuration);

        TextView tvName = new TextView(requireContext());
        tvName.setText(entry.getWorkoutName());
        tvName.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleMedium);
        tvName.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onSurface));
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);

        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        nameParams.topMargin = dpToPx(4);
        tvName.setLayoutParams(nameParams);

        View divider = new View(requireContext());
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(1)
        );
        dividerParams.topMargin = dpToPx(12);
        dividerParams.bottomMargin = dpToPx(12);
        divider.setLayoutParams(dividerParams);
        divider.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onSurfaceVariant) & 0x33FFFFFF
        );

        LinearLayout exerciseList = new LinearLayout(requireContext());
        exerciseList.setOrientation(LinearLayout.VERTICAL);
        exerciseList.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        for (WorkoutExercise exercise : entry.getExercises()) {
            TextView tvExercise = new TextView(requireContext());

            int setCount = exercise.getSets() != null ? exercise.getSets().size() : 0;
            String label = exercise.getName() + "  —  " + setCount + (setCount == 1 ? " set" : " sets");

            tvExercise.setText(label);
            tvExercise.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyMedium);
            tvExercise.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onSurfaceVariant));

            LinearLayout.LayoutParams exParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            exParams.topMargin = dpToPx(4);
            tvExercise.setLayoutParams(exParams);

            exerciseList.addView(tvExercise);
        }

      ;

        content.addView(headerRow);
        content.addView(tvName);
        content.addView(divider);
        content.addView(exerciseList);

        card.addView(content);

        return card;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * requireContext().getResources().getDisplayMetrics().density);
    }
}