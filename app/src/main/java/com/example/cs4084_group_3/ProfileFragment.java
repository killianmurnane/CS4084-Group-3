package com.example.cs4084_group_3;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private EditText inputWorkoutSplit, inputPRs, inputGoals, inputCurrentGym, inputBodyweight, inputNationality;
    private Button btnSave;

    private static final String PREFS_NAME = "ProfilePrefs";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Find all EditTexts
        inputWorkoutSplit = view.findViewById(R.id.inputWorkoutSplit);
        inputPRs = view.findViewById(R.id.inputPRs);
        inputGoals = view.findViewById(R.id.inputGoals);
        inputCurrentGym = view.findViewById(R.id.inputCurrentGym);
        inputBodyweight = view.findViewById(R.id.inputBodyweight);
        inputNationality = view.findViewById(R.id.inputNationality);

        btnSave = view.findViewById(R.id.btnSaveProfile);

        // Load saved values
        loadProfileData();

        // Save button click listener
        btnSave.setOnClickListener(v -> saveProfileData());

        return view;
    }

    private void loadProfileData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        inputWorkoutSplit.setText(prefs.getString("workoutSplit", ""));
        inputPRs.setText(prefs.getString("PRs", ""));
        inputGoals.setText(prefs.getString("goals", ""));
        inputCurrentGym.setText(prefs.getString("currentGym", ""));
        inputBodyweight.setText(prefs.getString("bodyweight", ""));
        inputNationality.setText(prefs.getString("nationality", ""));
    }

    private void saveProfileData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("workoutSplit", inputWorkoutSplit.getText().toString());
        editor.putString("PRs", inputPRs.getText().toString());
        editor.putString("goals", inputGoals.getText().toString());
        editor.putString("currentGym", inputCurrentGym.getText().toString());
        editor.putString("bodyweight", inputBodyweight.getText().toString());
        editor.putString("nationality", inputNationality.getText().toString());

        editor.apply(); // Save asynchronously
    }
}