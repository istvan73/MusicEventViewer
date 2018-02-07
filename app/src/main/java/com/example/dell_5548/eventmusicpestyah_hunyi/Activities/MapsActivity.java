package com.example.dell_5548.eventmusicpestyah_hunyi.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell_5548.eventmusicpestyah_hunyi.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String COORDINATES_KEY = "COORDINATES_KEY";
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
    private TextView resultText;
    public Marker addMarker;
    public Button okBT;
    private boolean mButtonClicked = false;
    private String mCoordinates;
    private Context ctx = this;
    private double XCoord;
    private double YCoord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mCoordinates = getTextFromBundle(COORDINATES_KEY,savedInstanceState);
        if (mCoordinates != null && mCoordinates.length()!=0) {
            String xTemp = mCoordinates.substring(mCoordinates.lastIndexOf('X') + 2, mCoordinates.lastIndexOf('Y') - 1);
            String yTemp = mCoordinates.substring(mCoordinates.lastIndexOf("Y") + 2);

            XCoord = Double.parseDouble(xTemp);
            YCoord = Double.parseDouble(yTemp);
        }else{
            XCoord = 10;
            YCoord = 10;
        }
        resultText = (TextView) findViewById(R.id.dragg_result);

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
                            resultText.setText(locality + "  " + country);
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

        addMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(XCoord, YCoord))
                .title("Previous Location"));



        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(XCoord,YCoord)));

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

    /**
     *<h2>Description:</h2><br>
     * <ul>
     *     <li>From a bundle, or saved instance state, this method will get the text and return it</li>
     *     <li>Only works with strings</li>
     * </ul>
     * @param key
     * @param savedInstanceState
     * @return
     */
    private String getTextFromBundle(String key, Bundle savedInstanceState){
        String textToReturn;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                textToReturn = "";
            } else {
                textToReturn = extras.getString(key);
            }
        } else {
            textToReturn = savedInstanceState.getString(key);
        }
        return textToReturn;
    }

    /**
     *<h2>Description:</h2><br>
     * <ul>
     *     <li>When the user clicks on the "Search" button, the map will focus on their inputted location, if exists</li>
     *     <li>The marker will be moved to this location, as well as the camera position</li>
     * </ul>
     * @param view
     */
    public void onSearch(View view){
        EditText searchField = (EditText) findViewById(R.id.mapSearchBar);
        String location = searchField.getText().toString().trim();
        List<Address> addresses = null;
        if (location != null || !location.equals("")){
            Geocoder geocoder = new Geocoder(ctx);
            try {
                addresses =  geocoder.getFromLocationName(location,1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses!=null || addresses.size()==0){
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                addMarker.remove();
                addMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Selected Location"));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }


        }
    }
}