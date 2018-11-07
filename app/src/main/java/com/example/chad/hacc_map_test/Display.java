package com.example.chad.hacc_map_test;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Display extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = formatter.format(todayDate);

        Bundle b = getIntent().getExtras();
        TextView pest = (TextView) findViewById(R.id.pest);
        TextView pesticide = (TextView) findViewById(R.id.pesticide);
        TextView dilutionRate = (TextView) findViewById(R.id.dilutionRate);
        TextView pesticideAmount = (TextView) findViewById(R.id.pesticideAmount);
        TextView crop = (TextView) findViewById(R.id.crop);
        //Thoroughly tested until here
        TextView locationText = (TextView) findViewById(R.id.locationText);
        TextView currentTemperatureField = (TextView) findViewById(R.id.current_temperature_field);
        TextView windField = (TextView) findViewById(R.id.windField);
        TextView area = (TextView) findViewById(R.id.area);
        TextView mLastUpdateTime = (TextView) findViewById(R.id.date);
        TextView results = (TextView) findViewById(R.id.results);

            String underlined = "  RESULTS   ";
            SpannableString content = new SpannableString(underlined);
            content.setSpan(new UnderlineSpan(), 0, underlined.length(), 0);
            results.setText(content);

        pest.setText("Pest: " + b.getString("PEST"));
        pesticide.setText("Pesticide Name: "+ b.getString("PESTICIDE"));
        dilutionRate.setText("Dilution Rate: " + b.getString("DILUTION_RATE"));
        pesticideAmount.setText("Amount of Pesticide: " + b.getString("PESTICIDE_AMOUNT"));
        crop.setText("Crop: " + b.getString("CROP"));

        //Again thoroughly tested until here
        locationText.setText("Address: " + b.getString("LOCATION_TEXT"));
        currentTemperatureField.setText("Current Temperature: " + b.getString("CURRENT_TEMPERATURE_FIELD"));
        windField.setText("Current Windspeed: " + b.getString("WIND_FIELD"));
        //area.setText(b.getDouble("AREA"));
        mLastUpdateTime.setText("Date: " + todayString);


    }

}
