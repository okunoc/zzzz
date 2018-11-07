package com.example.chad.hacc_map_test;

// references:
// https://javapapers.com/android/android-location-fused-provider/

import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.location.Geocoder;

import org.json.JSONException;
import org.json.JSONObject;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 100 * 5;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
    private SensorManager sensorManager;
    private int toastDuration = Toast.LENGTH_SHORT;
    private ArrayList locationsList;
    private double northernMostLat = 0;
    private double southernMostLat = 0;
    private double northSouth;
    private double westernMostLon = 0;
    private double easternMostLon = 0;
    private double diagonalDist;
    private double eastWest;
    private double area;

    // TextView areaText;
    private boolean logGPS = false;


    /* Trying to save my life - Cynthia's things */
    String windField, currentTemperatureField, locationText;
    String zipcode;
    String OPEN_WEATHER_MAP_API = "3d58a04d89afa4a0dab92c4e6490991c";


    /**
     *
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // list of all locations
        locationsList = new ArrayList();


        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        /* GPS location request permission */
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e("HACC", "Location permission not granted");
            return;
        } else {
            Log.e("HACC", "Location permission granted");
        }


        //TODO testing only, use sensors if needed
        // Get list of sensors
//        sensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);
//        List<Sensor> msensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
//        Log.d("HACC", msensorList.toString());

        /* Start buttons!!! */
        Button startButton = findViewById(R.id.Start);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // trigger logging of gps and sensors
                logGPS = true;
                //onLocationChanged(location);

                Log.d("HACC", "start button pressed");
                CharSequence startText = "Starting Tracking";
                Toast toast = Toast.makeText(getApplicationContext(), startText, toastDuration);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0 );
                toast.show();
                taskLoadUp(zipcode);

            }
        });

        Button stopButton = findViewById(R.id.Stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // stop logging of gps and sensors
                logGPS = false;

                Log.d("HACC", "stop button pressed");
                CharSequence stopText = "Stopping Tracking";
                Toast toast = Toast.makeText(getApplicationContext(), stopText, toastDuration);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0 );
                toast.show();

                Intent i = new Intent(MapsActivity.this, Additional_Info.class);
                Bundle b = new Bundle();

                b.putDouble("AREA", area);
                b.putString("ZIPCODE",zipcode);
                b.putString("WIND_FIELD", windField);
                b.putString("CURRENT_TEMPERATURE_FIELD", currentTemperatureField);
                b.putString("LOCATION_TEXT", locationText);
                b.putString("DATE",mLastUpdateTime);

                i.putExtras(b);

                startActivity(i);

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
    }

    /**
     * Triggered when GPS or Network location changes
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d("HACC", "onLocationChanged");

        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        Log.d("HACC", "last update: " + mLastUpdateTime);

        double lat = location.getLatitude();
        double lon = location.getLongitude();

        //recording max and min lat and lon, currently assumes user is in Northern and Western hemisphere
        if (northernMostLat == 0) {
            northernMostLat = lat;
        } else if (lat > northernMostLat) {
            northernMostLat = lat;
            Log.d("HACC", "northernmost lat set to: " + lat);
        }

        if (southernMostLat == 0) {
            southernMostLat = lat;
        } else if (lat < southernMostLat) {
            southernMostLat = lat;
            Log.d("HACC", "southernmost lat set to: " + lat);
        }

        if (easternMostLon == 0) {
            easternMostLon = lon;
        } else if (lon > easternMostLon ) {
            easternMostLon = lon;
            Log.d("HACC", "easternmost lon set to: " + lon);
        }

        if (westernMostLon == 0) {
            westernMostLon = lon;
        } else if (lon < westernMostLon ) {
            westernMostLon = lon;
            Log.d("HACC", "westernmost lon set to: " + lon);
        }

        // TODO need to fix distance calculation
        northSouth = (northernMostLat - southernMostLat) * 113000;
        diagonalDist = meterDistanceBetweenPoints((float) northernMostLat, (float) westernMostLon,
                (float) southernMostLat, (float) easternMostLon);

        Log.d("HACC", "northernmost: " + northernMostLat + ", southernmost: "
                + southernMostLat);
        Log.d("HACC", "easternmost: " + easternMostLon + ", westernmost: "
                + westernMostLon);
        Log.d ("HACC", "North-South difference: " + northSouth + " m, diagonal dist: " +
        diagonalDist);

        double aSquare = Math.pow(diagonalDist, 2) - Math.pow(northSouth, 2);
        eastWest = Math.sqrt(aSquare);
        area = eastWest * northSouth;
        Log.d("HACC", "area: " + area);


        LatLng latLng = new LatLng( lat, lon );
        Log.d("HACC", "lat: " + lat + ", lon: " + lon);
        Log.d("HACC", "location list: " + locationsList.size());

        if (logGPS) {
            Log.d("HACC", "GPS being logged");
            //TODO, consider replacing arraylist with db
            locationsList.add(location);
            mMap.addMarker(new MarkerOptions().position(latLng).title("current position"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        } else {
            Log.d("HACC", "GPS NOT being logged");
        }


        /* !!!!!!!! ------------- all of my code will be below Chad's !!!!!!!! ------------- */
        //Geocoder to grab postal code for weather api
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            zipcode = addresses.get(0).getPostalCode();

            // Gets the address for later.
            locationText=addresses.get(0).getAddressLine(0);

            //locationText.setText(locationText.getText()+ "\n Zipcode:" + zipcode);
        }catch(Exception e)
        {

        }
    }

    private double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f/Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

    protected void startLocationUpdates() {


        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d("HACC", "Location update started");
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped");
    }

    /**
     *
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("HACC", "onConnected - isConnected: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    /**
     *
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     *
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("HACC", "onStart");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("HACC", "onStop");
        mGoogleApiClient.disconnect();
        Log.d("HACC", "isConnected: " + mGoogleApiClient.isConnected());
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed");
        }
    }



    /* Begin WeatherAPI loading shenanigans */

    public void taskLoadUp(String query) {
        if (Function.isNetworkAvailable(getApplicationContext())) {
            MapsActivity.DownloadWeather task = new MapsActivity.DownloadWeather();
            task.execute(query);
            //locationText.setText("In taskloadup");
            //Toast.makeText(getApplicationContext(), "WE IN LOADUP!!!", Toast.LENGTH_LONG).show();


        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }


    class DownloadWeather extends AsyncTask< String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected String doInBackground(String...args) {

            //Does query using zipcode
            String xml = Function.excuteGet("http://api.openweathermap.org/data/2.5/weather?zip=" + args[0] +
                    ",us&units=imperial&appid=" + OPEN_WEATHER_MAP_API);
            return xml;
        }
        @Override
        protected void onPostExecute(String xml) {

            try {
                JSONObject json = new JSONObject(xml);
                if (json != null) {
                    //Grab xml and depending on which fields you want, pull from either
                    //The "main" group of fields. For us we also need "wind" group
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    JSONObject wind = json.getJSONObject("wind");

                    currentTemperatureField= String.format("%.2f", main.getDouble("temp"));
                    windField = wind.getString("speed");


                }
            } catch (JSONException e) {
                // This error will never happen since we don't allow user input anymore
                 //Toast.makeText(getApplicationContext(), "Something went wrong :(", Toast.LENGTH_SHORT).show();
            }

        }

    }


}
