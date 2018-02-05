package com.example.dell_5548.eventmusicpestyah_hunyi.Activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell_5548.eventmusicpestyah_hunyi.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
    private TextView resutText;
    public Marker addMarker;
    public Button okBT;
    private boolean mButtonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        resutText = (TextView) findViewById(R.id.dragg_result);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mButtonClicked = false;
        okBT = findViewById(R.id.maps_ok_bt);
        okBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButtonClicked = true;
                if (addMarker == null){
                    Intent resultIntent = new Intent();
// TODO Add extras or a data URI to this intent as appropriate.
//                    resultIntent.putExtra("longitude", Double.MAX_VALUE);
//                    resultIntent.putExtra("latitude", Double.MIN_VALUE);
                    setResult(Activity.RESULT_CANCELED, resultIntent);
                    finish();
                }else{
                    Intent resultIntent = new Intent();
// TODO Add extras or a data URI to this intent as appropriate.
                    double latitude, longitude;
                    longitude = addMarker.getPosition().longitude;
                    latitude = addMarker.getPosition().latitude;
                    resultIntent.putExtra("longitude", longitude);
                    resultIntent.putExtra("latitude", latitude);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

//        addMarker = new Marker(LtnLong );
        configureCameraIdle();

    }

    private void configureCameraIdle() {
        onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                LatLng latLng = mMap.getCameraPosition().target;
                Geocoder geocoder = new Geocoder(MapsActivity.this);

                try {
                    List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        String locality = addressList.get(0).getAddressLine(0);
                        String country = addressList.get(0).getCountryName();
                        if (!locality.isEmpty() && !country.isEmpty())
                            resutText.setText(locality + "  " + country);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setOnCameraIdleListener(onCameraIdleListener);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                if (addMarker != null)
                    addMarker.remove();
                addMarker = mMap.addMarker(new MarkerOptions().position(point).title("Custom location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                Toast.makeText(MapsActivity.this,"COORDINATES Selected:"+ point.latitude + point.longitude, Toast.LENGTH_SHORT).show();
            }
        });

        //mMap.setlon
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mButtonClicked) {
            Intent resultIntent = new Intent();
// TODO Add extras or a data URI to this intent as appropriate.
            setResult(Activity.RESULT_CANCELED, resultIntent);
        }
    }
}