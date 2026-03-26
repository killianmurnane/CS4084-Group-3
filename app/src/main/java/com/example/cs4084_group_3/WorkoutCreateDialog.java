package com.example.cs4084_group_3;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class WorkoutCreateDialog extends DialogFragment {

    private TextInputEditText etWorkoutName;
    private TextInputEditText etDuration;
    private TextInputEditText etDescription;
    private MaterialButton btnCreate;
    private MaterialButton btnCancel;

    private OnWorkoutCreatedListener listener;

    public interface OnWorkoutCreatedListener {
        void onWorkoutCreated(Workout workout);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Try to get listener from parent fragment
        if (getParentFragment() instanceof OnWorkoutCreatedListener) {
            listener = (OnWorkoutCreatedListener) getParentFragment();
        } else if (context instanceof OnWorkoutCreatedListener) {
            // Fallback to context if parent fragment doesn't implement it
            listener = (OnWorkoutCreatedListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_create_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind views
        etWorkoutName = view.findViewById(R.id.etWorkoutName);
        etDuration = view.findViewById(R.id.etDuration);
        etDescription = view.findViewById(R.id.etDescription);
        btnCreate = view.findViewById(R.id.btnCreateWorkout);
        btnCancel = view.findViewById(R.id.btnCancelWorkout);


        // Button listeners
        btnCancel.setOnClickListener(v -> dismiss());
        btnCreate.setOnClickListener(v -> createWorkout());
    }



    private void createWorkout() {
        // Validate inputs
        String name = etWorkoutName.getText().toString().trim();
        String durationStr = etDuration.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a workout name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (durationStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter duration in minutes", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a description", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            float duration = Float.parseFloat(durationStr);

            // Create new Workout
            Workout workout = new Workout();
            workout.setName(name);
            workout.setDuration(duration);
            workout.setDescription(description);



            // Initialize empty exercises array (already done in Workout constructor)

            // Save to WorkoutStore
            WorkoutStore.JsonWorkoutStore store = new WorkoutStore.JsonWorkoutStore();
            ArrayList<Workout> workouts = store.getWorkouts(requireContext());
            workouts.add(workout);
            store.writeWorkouts(requireContext(), workouts);

            // Notify listener
            if (listener != null) {
                listener.onWorkoutCreated(workout);
            }

            dismiss();
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Please enter a valid duration", Toast.LENGTH_SHORT).show();
        }
    }
}
