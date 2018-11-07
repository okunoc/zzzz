package com.example.chad.hacc_map_test;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.util.Log;


/* Cynthia's Imports */
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Additional_Info extends FragmentActivity{

    String windField, currentTemperatureField, locationText, mLastUpdateTime;
    //String OPEN_WEATHER_MAP_API = "3d58a04d89afa4a0dab92c4e6490991c";
    LocationManager locationManager;
    EditText pesticide,pest,dilutionRate,pesticideAmount,crop;
    //If anything breaks, its this passing Area from Maps to here
    double area;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional__info);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Bundle b = getIntent().getExtras();

        windField = b.getString("WIND_FIELD");
        currentTemperatureField = b.getString("CURRENT_TEMPERATURE_FIELD");
        locationText = b.getString("LOCATION_TEXT");
        area = b.getDouble("AREA");
        mLastUpdateTime = b.getString("DATE");

        pest = (EditText) findViewById(R.id.pest);
        pesticide = (EditText) findViewById(R.id.pesticide);
        dilutionRate = (EditText) findViewById(R.id.dilutionRate);
        pesticideAmount = (EditText) findViewById(R.id.pesticideAmount);
        crop = (EditText) findViewById(R.id.crop);

        //These last 3 fields I'm actually hiding off screen. Was testing prints
        ///currentTemperatureField = (TextView) findViewById(R.id.current_temperature_field);
        ///locationText = (TextView) findViewById(R.id.locationText);
        ///windField = (TextView) findViewById(R.id.windField);


        //CharSequence Toasty = "Passed Params " + windField +", " + currentTemperatureField +", " + locationText;
        //Toast toast = Toast.makeText(getApplicationContext(), Toasty, Toast.LENGTH_SHORT);
        //toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0 );
        //toast.show();

        //locationText.setText("Created");

        /* GPS location request permission
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }
        */
        //Get and store gps location. May need to wait 50 seconds because this is when the location manager updates location/pulls new location
        //If do not wait, it'll default to 0,0 which is west africa :(
//Checking!



        Button nextButton = findViewById(R.id.Next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //taskLoadUp actually sends the zipcode query to the OpenWeatherMap API

                /* moving all of the data to next class */
                Intent i = new Intent(Additional_Info.this, Display.class);
                Bundle b = new Bundle();
                b.putString("WIND_FIELD", windField);
                b.putString("CURRENT_TEMPERATURE_FIELD", currentTemperatureField);
                b.putString("LOCATION_TEXT", locationText);
                b.putString("DATE",mLastUpdateTime);
                b.putString("PEST", pest.getText().toString());
                b.putString("PESTICIDE", pesticide.getText().toString());
                b.putString("DILUTION_RATE", dilutionRate.getText().toString());
                b.putString("PESTICIDE_AMOUNT", pesticideAmount.getText().toString());
                b.putString("CROP", crop.getText().toString());

                //CHECK AREA PASS
                b.putDouble("AREA",area);

                i.putExtras(b);
                startActivity(i);


            }
        });

    }



}
