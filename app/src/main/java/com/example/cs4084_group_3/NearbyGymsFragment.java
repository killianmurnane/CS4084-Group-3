package com.example.cs4084_group_3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NearbyGymsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getDeviceLocationAndMove();
                } else {
                    Toast.makeText(requireContext(),
                            "Location permission denied. Showing default location.",
                            Toast.LENGTH_LONG).show();
                    // Fall back to Limerick city centre
                    moveToLocation(new LatLng(52.6638, -8.6267));
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nearby_gyms, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        addGymMarkers();

        // Check if we already have permission
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getDeviceLocationAndMove();
        } else {
            // Request permission
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void getDeviceLocationAndMove() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        moveToLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                    } else {
                        // Last location can be null if device location was recently turned on
                        Toast.makeText(requireContext(),
                                "Couldn't get location. Showing default.",
                                Toast.LENGTH_SHORT).show();
                        moveToLocation(new LatLng(52.6638, -8.6267));
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(),
                            "Location error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    moveToLocation(new LatLng(52.6638, -8.6267));
                });
    }

    private void moveToLocation(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f));
    }

    private void addGymMarkers() {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.6653, -8.6235))
                .title("Snap Fitness Limerick"));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.6702, -8.6219))
                .title("Limerick Sports and Fitness"));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.6741, -8.5706))
                .title("UL Sport Arena"));
    }
}