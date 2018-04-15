package com.se2006.teamkaydon.powerlah;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry points to the Places API.
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when
    // location permission is not granted.
    private final LatLng mDefaultLocation = new LatLng(1.3439166, 103.7540051);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // ArrayList of Charging Stations.
    private ArrayList<ChargingStationData> ChargingStationList;
    private boolean firstClick = true;

    // Handler to check battery level.
    private Handler batteryHandler = new Handler();

    public static Button timer;

    private Intent intentMarker;
    private Bundle bundleMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Construct a FusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Check battery level every minute.
        batteryHandler.postDelayed(checkBatteryThread, 60000);

        timer = (Button) findViewById(R.id.timerButton);
        timer.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (timer.getText().equals("")) {

                }
                else {
                    startActivity(intentMarker);
                }
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

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        // Create all markers.
        retrieveChargingStationData(mMap);

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the device.
         * The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null
         * in rare cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    public void retrieveChargingStationData(GoogleMap googleMap) {
        ChargingStationList = new ArrayList<>();
        ChargingStationDataManager csdm = new ChargingStationDataManager(this);
        csdm.createDatabase();
        csdm.open();
        Cursor cursor = csdm.retrieveData();
        if (cursor.moveToFirst()) {
            do {
                ChargingStationData c = new ChargingStationData();
                c.setIndex(Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id"))));
                c.setName(cursor.getString(cursor.getColumnIndex("Name")));
                c.setInfo(cursor.getString(cursor.getColumnIndex("Info")));
                c.setZip(Integer.parseInt(cursor.getString(cursor.getColumnIndex("Zip"))));
                c.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex("Latitude"))));
                c.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex("Longitude"))));
                c.setChargers(Integer.parseInt(cursor.getString(cursor.getColumnIndex("Chargers"))));
                createMarker(googleMap,c);
                ChargingStationList.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public Marker createMarker(GoogleMap googleMap, ChargingStationData c) {
        int stationIndex = c.getIndex();
        String sStationIndex = String.valueOf(stationIndex);
        double latitude = c.getLatitude();
        double longitude = c.getLongitude();
        String title = stationIndex + " " + c.getName();
        String snippet = c.getInfo() + " (S)" + c.getZip();
        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet));
        m.setTag(sStationIndex);
        return m;
    }

    private Runnable checkBatteryThread = new Runnable() {
        public void run() {
            if (BatteryLevelReceiver.checkBatt(MapsActivity.this)) {
                // If notified, check again in 30 minutes.
                System.out.println("30 mins");
                batteryHandler.postDelayed(this, 1800000);
            }
            else {
                // If not notified, check again in 1 minute.
                System.out.println("1 min");
                batteryHandler.postDelayed(this, 60000);
            }
        }
    };

    /* Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(Marker marker) {

        float[] distance = {0,0,0};
        boolean closeEnough = false;

        //Retrieve the data from the marker.
        String stationIndex = (String) marker.getTag();

        getDeviceLocation();
        Location.distanceBetween(mLastKnownLocation.getLatitude(),
                mLastKnownLocation.getLongitude(),
                marker.getPosition().latitude,
                marker.getPosition().longitude,
                distance);

        if (distance[0] <= 200) {
            closeEnough = true;
        }

        if(firstClick){
            firstClick = false;
            return false;
        }
        else if (closeEnough){
            firstClick = true;
            intentMarker = new Intent(MapsActivity.this, PortableChargerActivity.class);
            bundleMarker = new Bundle();
            bundleMarker.putString("stationIndex", stationIndex);
            intentMarker.putExtras(bundleMarker);
            startActivity(intentMarker);
            return true;
        }
        else {
            firstClick = true;
            Toast.makeText(this, "You are more than 200m away from the charging station and thus cannot borrow a charger.", Toast.LENGTH_LONG).show();
            return true;
        }

        // Return false to indicate that we have not consumed the event and that we wish for the default
        // behaviour to occur (which is for the camera to move such that the marker is centered
        // and for the marker's info window to open, if it has one.
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            // Handle the camera action
            startActivity(new Intent(MapsActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(MapsActivity.this, WalletActivity.class));

        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(MapsActivity.this, BatteryActivity.class));

        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(MapsActivity.this,TimerActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
