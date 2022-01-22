package com.example.currencychecker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ConverterActivity extends AppCompatActivity {

    String[] names;
    double[] values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        Bundle bundle = getIntent().getExtras();
        names = bundle.getStringArray("names");
        values = bundle.getDoubleArray("values");


    }
}