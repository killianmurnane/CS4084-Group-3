package com.example.cs4084_group_3;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;

public class ProfileFragment extends Fragment {

    private EditText inputName, inputEmail, inputWorkoutSplit, inputGoals,
            inputCurrentGym, inputBodyweight, inputNationality;

    private Button btnSave;
    private ImageView profileImage;

    private static final int PICK_IMAGE_REQUEST = 1;
    private String encodedImage = null;

    public ProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Inputs
        inputName = view.findViewById(R.id.inputName);
        inputEmail = view.findViewById(R.id.inputEmail);
        inputWorkoutSplit = view.findViewById(R.id.inputWorkoutSplit);
        inputGoals = view.findViewById(R.id.inputGoals);
        inputCurrentGym = view.findViewById(R.id.inputCurrentGym);
        inputBodyweight = view.findViewById(R.id.inputBodyweight);
        inputNationality = view.findViewById(R.id.inputNationality);

// UI
        profileImage = view.findViewById(R.id.profileImage);
        ImageView btnEditImage = view.findViewById(R.id.btnEditImage);
        btnSave = view.findViewById(R.id.btnSaveProfile);

// Image picker (NEW)
        btnEditImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
        btnSave = view.findViewById(R.id.btnSaveProfile);


        loadProfileData();

        btnSave.setOnClickListener(v -> saveProfileData());

        return view;
    }

    private void saveProfileData() {
        // Load existing profile first (IMPORTANT FIX)
        Profile profile = ProfileStore.readProfile(requireContext());

        if (profile == null) {
            profile = new Profile();
        }
        profile.name = inputName.getText().toString();
        profile.email = inputEmail.getText().toString();
        profile.workoutSplit = inputWorkoutSplit.getText().toString();
        profile.goals = inputGoals.getText().toString();
        profile.currentGym = inputCurrentGym.getText().toString();
        profile.bodyweight = inputBodyweight.getText().toString();
        profile.nationality = inputNationality.getText().toString();

        // Only overwrite image if a new one was selected
        if (encodedImage != null) {
            profile.profileImageBase64 = encodedImage;
        }

        ProfileStore.writeProfile(requireContext(), profile);

        Toast.makeText(getContext(), "Profile saved!", Toast.LENGTH_SHORT).show();
    }

    private void loadProfileData() {
        Profile profile = ProfileStore.readProfile(requireContext());

        if (profile != null) {
            inputName.setText(profile.name);
            inputEmail.setText(profile.email);
            inputWorkoutSplit.setText(profile.workoutSplit);
            inputGoals.setText(profile.goals);
            inputCurrentGym.setText(profile.currentGym);
            inputBodyweight.setText(profile.bodyweight);
            inputNationality.setText(profile.nationality);

            // Load image if exists
            if (profile.profileImageBase64 != null) {
                byte[] decodedBytes = Base64.decode(profile.profileImageBase64, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                profileImage.setImageBitmap(bitmap);

                encodedImage = profile.profileImageBase64;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST &&
                resultCode == Activity.RESULT_OK &&
                data != null) {

            Uri imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().getContentResolver(),
                        imageUri
                );

                profileImage.setImageBitmap(bitmap);
                encodedImage = encodeToBase64(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String encodeToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}