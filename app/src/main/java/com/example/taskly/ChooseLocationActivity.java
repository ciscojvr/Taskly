package com.example.taskly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class ChooseLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    //Google Maps android object
    private GoogleMap mMap;
    private float mapDefaultZoom = 13;

    //Marker tracking members
    public static Marker LastMarker;
    public static LatLng LastLatLng = new LatLng(1.0, 1.0);

    //Location access members
    private boolean locationPermissionGranted = false;
    private LocationRequest lr;
    private boolean locationRequested = false;

    //UI Elements
    private Button endButton;
    private Button getLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        endButton = (Button)findViewById(R.id.endButton);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send the lat long info back to the Task creator
                AddTaskActivity.SetInfoFromLocationChooser(LastLatLng.latitude, LastLatLng.longitude);
                finish();
            }
        });

        getLocationButton = (Button)findViewById(R.id.getCurrentLocationButton);
        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!locationPermissionGranted) {
                    getLocationPermission();
                }
                else {
                    getDeviceLocation();
                }
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng atlanta = new LatLng(33.762, -84.395);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(atlanta, mapDefaultZoom));
        mMap.getUiSettings().setZoomControlsEnabled(true);

        getLocationPermission();

        setMapLongClick(googleMap);
        setPoiClick(googleMap);
    }

    private void setMapLongClick(final GoogleMap map) {
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                String snippet = String.format(Locale.getDefault(),
                        "Lat: %1$.5f, Long: %2$.5f",
                        latLng.latitude,
                        latLng.longitude);

                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Dropped Pin")
                        .snippet(snippet));

                LastLatLng = latLng;
            }
        });
    }

    private void setPoiClick(final GoogleMap map) {
        map.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest poi) {
                Marker poiMarker = mMap.addMarker(new MarkerOptions()
                .position(poi.latLng).title(poi.name));
                poiMarker.showInfoWindow();

                if (LastMarker != null) {
                    LastMarker.remove();
                }
                LastMarker = poiMarker;
                LastLatLng = poi.latLng;
            }
        });
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        }
        else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        locationRequested = true;
        long UPDATE_INTERVAL = 10 * 1000;
        long FASTEST_INTERVAL = 2000;

        lr = new LocationRequest();
        lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        lr.setInterval(UPDATE_INTERVAL);
        lr.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder lsrb = new LocationSettingsRequest.Builder();
        lsrb.addLocationRequest(lr);
        LocationSettingsRequest lsr = lsrb.build();

        //Check if location settings are satisfied
        SettingsClient sc = LocationServices.getSettingsClient(this);
        sc.checkLocationSettings(lsr);

        getFusedLocationProviderClient(this).requestLocationUpdates(lr, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location lastLocation = locationResult.getLastLocation();

                //Center map on the device's lat long, and place a marker there
                if (locationRequested) {
                    LastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(LastLatLng));
                    String snippet = String.format(Locale.getDefault(),
                            "Lat: %1$.5f, Long: %2$.5f",
                            LastLatLng.latitude,
                            LastLatLng.longitude);
                    mMap.addMarker(new MarkerOptions()
                            .position(LastLatLng)
                            .title("Device Location")
                            .snippet(snippet));

                    locationRequested = false;
                }
            }
        }, Looper.myLooper());
    }
}