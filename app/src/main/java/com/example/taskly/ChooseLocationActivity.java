package com.example.taskly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

import java.util.Locale;

public class ChooseLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static Marker LastMarker;
    public static LatLng LastLatLng;

    private GoogleMap mMap;
    private Button endButton;

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
                finish();
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng atlanta = new LatLng(33.762, -84.395);
        float zoom = 13;
        mMap.addMarker(new MarkerOptions().position(atlanta).title("Marker in Atlanta"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(atlanta, zoom));

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
}