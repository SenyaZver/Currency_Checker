package com.example.currencychecker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

public class ConverterActivity extends AppCompatActivity {

    String[] names;
    double[] values;
    int choice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        Bundle bundle = getIntent().getExtras();
        names = bundle.getStringArray("names");
        values = bundle.getDoubleArray("values");

        EditText editText = findViewById(R.id.editText);
        TextView resultText = findViewById(R.id.resultText);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        spinner.setPrompt("Currencies");

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                choice = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        editText.setOnKeyListener((v, keyCode, event) -> {

            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                resultText.clearComposingText();
                String text = editText.getText().toString();


                resultText.setText(getResult(text));


                return true;
            }
            return false;
        });




    }

    String getResult(String text) {
        double result = values[choice] * Double.parseDouble(text);
        return String.valueOf(result);
    }




}