package com.example.alarmapp.settings;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.alarmapp.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SettingsFragment extends Fragment {

    private TextView locationTextView;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_tab_fragment, container, false);

        locationTextView = view.findViewById(R.id.locationTextView);

        // Initialize location manager
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        // Initialize location listener
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Update UI with the new location
                updateLocationUI(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        // Check for location permission and request if not granted
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Request location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Set up a click listener for refreshing location
        view.findViewById(R.id.refreshButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLocation();
            }
        });

        return view;
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, request location updates
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    // Update UI with the current location
    private void updateLocationUI(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String locationString = "Latitude: " + latitude + "\nLongitude: " + longitude;

            // Convert coordinates to address
            String address = getAddressFromCoordinates(latitude, longitude);
            if (address != null && !address.isEmpty()) {
                locationString += "\nAddress: " + address;
            }

            locationTextView.setText(locationString);
        }
    }

    // Method to convert coordinates to address
    private String getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to manually refresh location
    private void refreshLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                updateLocationUI(lastKnownLocation);
            } else {
                Toast.makeText(requireContext(), "Location not available", Toast.LENGTH_SHORT).show();
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
}
