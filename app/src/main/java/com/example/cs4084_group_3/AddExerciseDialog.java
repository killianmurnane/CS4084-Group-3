package com.example.cs4084_group_3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AddExerciseDialog extends DialogFragment {

    public interface OnExerciseSelectedListener {
        void onExerciseSelected(String exerciseName);
    }

    private OnExerciseSelectedListener listener;

    public void setOnExerciseSelectedListener(OnExerciseSelectedListener listener){
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.dialog_add_exercise, container, false);

        Spinner muscleSpinner = view.findViewById(R.id.muscleSpinner);
        Spinner exerciseSpinner = view.findViewById(R.id.exerciseSpinner);
        MaterialButton btnAdd = view.findViewById(R.id.btnAdd);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancel);

        // Setup muscle group spinner
        ExerciseData.MuscleGroup[] groups = ExerciseData.getMuscleGroups();
        String[] groupNames = new String[groups.length];
        for (int i = 0; i < groups.length; i++) {
            groupNames[i] = groups[i].name();
        }

        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                groupNames);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        muscleSpinner.setAdapter(groupAdapter);


        // update spinner when muscle group changes
        muscleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                updateExerciseSpinner(exerciseSpinner, groups[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        updateExerciseSpinner(exerciseSpinner, groups[0]);

        // listeners for button clicks
        btnCancel.setOnClickListener(v -> dismiss());

        btnAdd.setOnClickListener(v -> {
            String selectedExercise = (String) exerciseSpinner.getSelectedItem();
            if (selectedExercise != null && listener != null) {
                listener.onExerciseSelected(selectedExercise);
            }
            dismiss();
        });

        return view;

    }

    private void updateExerciseSpinner(Spinner spinner, ExerciseData.MuscleGroup group) {
        List<String> exercises = ExerciseData.getExerciseForGroup(group);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                exercises);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }




}
