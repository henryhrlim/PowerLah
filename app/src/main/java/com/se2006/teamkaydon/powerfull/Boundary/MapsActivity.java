package com.se2006.teamkaydon.powerfull.Boundary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.se2006.teamkaydon.powerfull.Control.BatteryLevelReceiver;
import com.se2006.teamkaydon.powerfull.Entity.ChargingStationData;
import com.se2006.teamkaydon.powerfull.Control.ChargingStationDataManager;
import com.se2006.teamkaydon.powerfull.R;

import java.util.ArrayList;

/**
 * Provides Google Maps view for the application and doubles as the main activity of the application.
 *
 * @author Team Kaydon
 * @version 1.0
 * @since 2018-04-17
 */
public class MapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Singapore) and default zoom to use when location permission is not granted.
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

    // Timer button.
    public static Button timer;

    // Variable to store marker name.
    public static String currentlySelectedMarker;

    // Variables for marker click functions
    private Intent intentMarker;
    private Bundle bundleMarker;
    private int distanceThreshold = 5000;

    /**
     * Creates the map, the navigation drawer, the battery level checker and timer overlay.
     * @param savedInstanceState savedInstanceState a Bundle object containing previously saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            CameraPosition mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
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
                if (timer.getText().equals("")) { }
                else {
                    startActivity(intentMarker);
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
     *
     * @param googleMap a Google Map object
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

    /**
     * Request location permission, so that we can get the location of the device.
     * The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    /**
     * Handles callback for result of permission request for location services of device. Calls updateLocationUI() after handling permission request callback.
     * @param requestCode permission request code
     * @param permissions list of permissions
     * @param grantResults list of statuses on whether the permissions were granted or not
     */
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

    /**
     * Initializes the location UI on the Maps interface.
     */
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

    /**
     * Get the best and most recent location of the device, which may be null
     * in rare cases when a location is not available.
     */
    private void getDeviceLocation() {
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


    /**
     * Retrieves charging station data such as index, name, zipcode, latitude and longitude by calling ChargingStationDataManager. Calls createMarker() to
     * pin location marker of each charging station on the map.
     * @param googleMap A Google Map object.
     */
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

    /**
     * Creates marker to be pinned on google maps using the positional values (longitute and latitude) passed to this method.
     * @param googleMap A google map object
     * @param c A ChargingStationData object
     * @return Returns the Marker object created
     */
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

    /**
     * Creates a background thread process that checks for Battery Level of the device every minute. After the first notification of battery level
     * is received, change the delay frequency of check to every 30 minutes.
     */
    private Runnable checkBatteryThread = new Runnable() {
        public void run() {
            if (BatteryLevelReceiver.checkBatt(MapsActivity.this)) {
                // If notified, check again in 30 minutes.
                batteryHandler.postDelayed(this, 1800000);
            }
            else {
                // If not notified, check again in 50 seconds.
                batteryHandler.postDelayed(this, 50000);
            }
        }
    };

    /**Listener for marker click. When user clicks on the marker, this function is called. Checks if its user's first click,
     * if true, marker details will be shown. On user's subsequent click, the marker will instantiate portable charger activity for
     * user to borrow the portable charger at that charging station, provided user is within a set distance threshold of that marker location.
     * @param marker A marker object.
     * @return Return true if conditions bring us to activity for borrowing portable charger, false if user first click on the marker
     * or if user is too far away from the charging station.
     */
    @Override
    public boolean onMarkerClick(Marker marker) {

        float[] distance = {0,0,0};
        boolean closeEnough = false;

        //Retrieve the data from the marker.
        String stationIndex = (String) marker.getTag();
        currentlySelectedMarker = marker.getTitle();

        //getDeviceLocation();
        Location.distanceBetween(mLastKnownLocation.getLatitude(),
                mLastKnownLocation.getLongitude(),
                marker.getPosition().latitude,
                marker.getPosition().longitude,
                distance);

        if (distance[0] <= distanceThreshold) {
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
            startActivityForResult(intentMarker,0);
            return true;
        }
        else {
            firstClick = true;
            Toast.makeText(this, "You are more than 50m away from the charging station and thus cannot borrow a charger.", Toast.LENGTH_LONG).show();
            return false;
        }

        // Return false to indicate that we have not consumed the event and that we wish for the default
        // behaviour to occur (which is for the camera to move such that the marker is centered
        // and for the marker's info window to open, if it has one.
    }

    /**Save instance state of activity, in this case, to put current camera postion on map and location to an outState bundle before moving away from
     * the activity.
     * @param outState Bundle that came along with the intent.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * On back button press, checks if nav drawer is open, if it is close drawer. Else, execute back press function as per normal.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**Creates and inflates memu on action bar
     * @param menu Menu object.
     * @return Returns true if inflation of menu is successful.
     */
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

    /** Handles navigation view item clicks.
     * @param item MenuItem object
     * @return Returns true if selection of Navigation drawer items is successful.
     */
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
