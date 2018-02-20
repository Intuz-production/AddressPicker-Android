//  The MIT License (MIT)

//  Copyright (c) 2018 Intuz Solutions Pvt Ltd.

//  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
//  (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify,
//  merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:

//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
//  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
//  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
//  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.intuz.addresspicker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationPickerActivity extends Activity implements LocationProvider.LocationCallback,OnMapReadyCallback {
    private LocationProvider locationProvider;

    private final String TAG = LocationPickerActivity.class.getSimpleName();
    private View view;
    private String userAddress = "";
    private Marker marker;
    private double mLatitude;
    private double mLongitude;
    private GoogleMap mMap;
    private EditText txtUserAddress;
    private ImageView imgCurrentloc;
    private TextView txtSelectLocation;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 2;
    private boolean mLocationPermissionGranted;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 8088;
    private boolean isFirst = true;
    private ImageView imgSearch;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String city;
    private String state;
    private String country;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);
        txtUserAddress = findViewById(R.id.txtUserAddress);
        imgCurrentloc = findViewById(R.id.imgCurrentloc);
        txtSelectLocation = findViewById(R.id.txtSelectLocation);
        imgSearch =findViewById(R.id.imgSearch);
        getLocationPermission();
        // Try to obtain the map from the SupportMapFragment.
        checkAndRequestPermissions();
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationProvider = new LocationProvider(LocationPickerActivity.this, LocationPickerActivity.this);
        locationProvider.connect();


        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(LocationPickerActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        txtSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("address", txtUserAddress.getText().toString().trim());
                intent.putExtra("lat", mLatitude);
                intent.putExtra("long", mLongitude);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        imgCurrentloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentLocation();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                userAddress =place.getAddress().toString();
                txtUserAddress.setText(""+userAddress);
                mLatitude = place.getLatLng().latitude;
                mLongitude = place.getLatLng().longitude;

                MarkerOptions markerOptions;
                try {
                    mMap.clear();
                    markerOptions = new MarkerOptions().position(new LatLng(mLatitude, mLongitude)).title(userAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_red_800_24dp));
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mLatitude, mLongitude), 14);
                    mMap.animateCamera(cameraUpdate);
                    mMap.addMarker(markerOptions).showInfoWindow();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private boolean checkAndRequestPermissions() {
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarsePermision = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (coarsePermision != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;

    }

    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void CurrentLocation() {
        if (MapUtility.currentLocation != null) {
            LatLng lng = new LatLng(MapUtility.currentLocation.getLatitude(), MapUtility.currentLocation.getLongitude());
            mLatitude = MapUtility.currentLocation.getLatitude();
            mLongitude = MapUtility.currentLocation.getLongitude();
            Log.e("currentLocation", mLatitude + "");
            addMarker(lng);
            new getAddressForLocation(mLatitude, mLongitude).execute();
        }
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            marker = mMap.addMarker(new MarkerOptions()
                    .position(loc)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_red_800_24dp)));
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(loc, 14);
            mMap.animateCamera(cameraUpdate);

        }
    };

    private void addMarker(LatLng coordinate) {
        CameraPosition cameraPosition;
        mLatitude = coordinate.latitude;
        mLongitude = coordinate.longitude;
        if (mMap != null) {

            mMap.addMarker(new MarkerOptions().position(coordinate).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_red_800_24dp)));
            cameraPosition = new CameraPosition.Builder().target(coordinate).zoom(18).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (mMap.isIndoorEnabled()) {
            mMap.setIndoorEnabled(false);
        }

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {
                View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);

                // Getting the position from the marker
                LatLng latLng = arg0.getPosition();
                TextView tvLat = v.findViewById(R.id.address);
                tvLat.setText(userAddress);
                return v;

            }
        });
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        // Setting a click event handler for the map

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                mLatitude = latLng.latitude;
                mLongitude = latLng.longitude;
                Log.e("latlng", latLng + "");
                addMarker(latLng);
                new getAddressForLocation(mLatitude, mLongitude).execute();
            }
        });

        if (checkLocationPermission()) {
            checkAndRequestPermissions();
        } else {
            return;
        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void handleNewLocation(Location location) {
        if (location != null) {

            MapUtility.currentLocation = location;
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            if (isFirst) {
                isFirst = false;
                new getAddressForLocation(MapUtility.currentLocation.getLatitude(), MapUtility.currentLocation.getLongitude()).execute();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class getAddressForLocation extends AsyncTask<Void, Void, Void> {
        Double latitude, longitude;

        getAddressForLocation(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(LocationPickerActivity.this, Locale.getDefault());
                StringBuilder sb = new StringBuilder();

                addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null && addresses.size() > 0) {

                    String address = addresses.get(0).getAddressLine(0);
                    if (address != null)
                        sb.append(address).append(" ");
                    city = addresses.get(0).getLocality();
                    if (city != null)
                        sb.append(city).append(" ");

                    state = addresses.get(0).getAdminArea();
                    if (state != null)
                        sb.append(state).append(" ");
                    country = addresses.get(0).getCountryName();
                    if (country != null)
                        sb.append(country).append(" ");

                    String postalCode = addresses.get(0).getPostalCode();
                    if (postalCode != null)
                        sb.append(postalCode).append(" ");
                    userAddress = sb.toString();

                }
            } catch (IOException e) {
                e.printStackTrace();
                showLocation((new LatLng(latitude, longitude)));

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (userAddress == null || userAddress.equalsIgnoreCase("")) {
                showLocation((new LatLng(latitude, longitude)));
            } else {
                MarkerOptions markerOptions;
                try {
                    mMap.clear();
                    txtUserAddress.setText(""+userAddress);

                    markerOptions = new MarkerOptions().position(new LatLng(latitude, longitude)).title(userAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_red_800_24dp));
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14);
                    mMap.animateCamera(cameraUpdate);
                    mMap.addMarker(markerOptions).showInfoWindow();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void showLocation(final LatLng latLng1) {
        if (!MapUtility.isNetworkAvailable(this)) {
            Toast.makeText(LocationPickerActivity.this, "No internet connection available.", Toast.LENGTH_SHORT).show();
        } else {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MapUtility.showProgress(LocationPickerActivity.this);
                }
            });
            MapServices.MapInterface mapInterface = MapServices.getClient();
            Call<MapRequestResponse> call;
            String s = String.valueOf(latLng1.latitude) + "," + String.valueOf(latLng1.longitude);

            call = mapInterface.MapData(s);
            call.enqueue(new Callback<MapRequestResponse>() {
                @Override
                public void onResponse(Call<MapRequestResponse> call, Response<MapRequestResponse> response) {
                    MapUtility.hideProgress();
                    if (response.isSuccessful()) {
                        MapRequestResponse result = response.body();
                        if (result.getStatus().equalsIgnoreCase("OK")) {
                            if (result.getResults().size() > 0) {
                                final MarkerOptions[] markerOptions = {null};
                                try {
                                    mMap.clear();

                                    userAddress = result.getResults().get(0).getFormatted_address();
                                    txtUserAddress.setText(""+userAddress);


                                    if (userAddress != null) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                markerOptions[0] = new MarkerOptions().position(latLng1).title((userAddress)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_red_800_24dp));
                                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng1, 14);
                                                mMap.animateCamera(cameraUpdate);
                                                mMap.addMarker(markerOptions[0]).showInfoWindow();
                                            }
                                        });

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace(); // getFromLocation() may sometimes fail
                                }
                            }
                        }
                    } else {
                        MapUtility.hideProgress();
                    }
                }

                @Override
                public void onFailure(Call<MapRequestResponse> call, Throwable t) {
                    t.printStackTrace();
                    MapUtility.hideProgress();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    locationProvider = new LocationProvider(LocationPickerActivity.this, LocationPickerActivity.this);
                    locationProvider.connect();
                }
            }

        }
    }

    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }

}

