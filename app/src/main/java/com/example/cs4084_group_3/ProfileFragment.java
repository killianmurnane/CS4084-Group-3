package com.example.cs4084_group_3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private EditText inputWorkoutSplit, inputPRs, inputGoals,
            inputCurrentGym, inputBodyweight, inputNationality;
    private Button btnSave;

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        inputWorkoutSplit = view.findViewById(R.id.inputWorkoutSplit);
        inputPRs = view.findViewById(R.id.inputPRs);
        inputGoals = view.findViewById(R.id.inputGoals);
        inputCurrentGym = view.findViewById(R.id.inputCurrentGym);
        inputBodyweight = view.findViewById(R.id.inputBodyweight);
        inputNationality = view.findViewById(R.id.inputNationality);

        btnSave = view.findViewById(R.id.btnSaveProfile);

        loadProfileData();

        btnSave.setOnClickListener(v -> saveProfileData());

        return view;
    }

    private void saveProfileData() {
        Profile profile = new Profile();

        profile.workoutSplit = inputWorkoutSplit.getText().toString();
        profile.PRs = inputPRs.getText().toString();
        profile.goals = inputGoals.getText().toString();
        profile.currentGym = inputCurrentGym.getText().toString();
        profile.bodyweight = inputBodyweight.getText().toString();
        profile.nationality = inputNationality.getText().toString();

        ProfileStore.writeProfile(requireContext(), profile);

        Toast.makeText(getContext(), "Profile saved!", Toast.LENGTH_SHORT).show();
    }

    private void loadProfileData() {
        Profile profile = ProfileStore.readProfile(requireContext());

        if (profile != null) {
            inputWorkoutSplit.setText(profile.workoutSplit);
            inputPRs.setText(profile.PRs);
            inputGoals.setText(profile.goals);
            inputCurrentGym.setText(profile.currentGym);
            inputBodyweight.setText(profile.bodyweight);
            inputNationality.setText(profile.nationality);
        }
    }
}