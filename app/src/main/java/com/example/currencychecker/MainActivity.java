package com.example.currencychecker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


//sorry for bad code, had to write it during exams :(

public class MainActivity extends AppCompatActivity {

    RecyclerView currenciesList;
    private RequestQueue queue;


    //TODO store currencies as a single object
//    HashMap<String, Double> currencies;

    String[] names = new String[34];
    String[] valueStrings = new String[34];
    double[] values = new double[34];

    public static final String MY_PREFS_NAME = "MyPrefsFile";

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currenciesList = findViewById(R.id.CurrenciesList);


        getSavedData();

        Button converterButton = findViewById(R.id.Converter_button);
        Button updateButton = findViewById(R.id.Update_Button);

        //TODO make parsing and updating info dependable on the date and existence of said info(task 4)

        queue = Volley.newRequestQueue(this);
        Parse(names, valueStrings, values);


        RecycleViewAdapter adapter = new RecycleViewAdapter(this, names, valueStrings);
        currenciesList.setAdapter(adapter);
        currenciesList.setLayoutManager(new LinearLayoutManager(this));


        //TODO implement Converter activity (task 2)
        converterButton.setOnClickListener(view -> {
            Intent converterIntent = new Intent(view.getContext(), ConverterActivity.class);
            converterIntent.putExtra("names", names);
            converterIntent.putExtra("values", values);

            view.getContext().startActivity(converterIntent);
        });



        updateButton.setOnClickListener(view -> {
            Parse(names, valueStrings, values);
            adapter.swapItems(names, valueStrings);
            adapter.notifyDataSetChanged();     //one of these might be unnecessary
        });

    }


//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putSerializable("names", names);
//        outState.putSerializable("values", values);
//        outState.putSerializable("valuesStrings", valueStrings);
//
//    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState !=null) {
            names = (String[]) getIntent().getSerializableExtra("names");
            valueStrings = (String[]) getIntent().getSerializableExtra("valueStrings");
            values = (double[]) getIntent().getSerializableExtra("values");
        }
    }



    private void Parse(String[] names, String[] valueStrings, double[] values) {

        String url = "https://www.cbr-xml-daily.ru/daily_json.js";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject Valutes = response.getJSONObject("Valute");

                        int i = 0;
                        Iterator<String> iterator = Valutes.keys();
                        while (iterator.hasNext()) {
                            JSONObject currency = Valutes.getJSONObject(iterator.next());


                            String name = currency.getString("Name");
                            double value = currency.getDouble("Value");

                            names[i] = name;
                            valueStrings[i] = String.valueOf(value);
                            values[i] = value;

                            i++;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> error.printStackTrace());

        queue.add(request);
        //saving data
        Set<String> savedNames = new HashSet<>(Arrays.asList(names));
        Set<String> savedValues = new HashSet<>(Arrays.asList(valueStrings));

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putStringSet("names", savedNames);
        editor.putStringSet("values", savedValues);
        editor.apply();
    }

    void getSavedData() {
        SharedPreferences pref = this.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> namesSet = new HashSet<>(pref.getStringSet("names", new HashSet<>()));
        Set<String> valuesSet = new HashSet<>(pref.getStringSet("values", new HashSet<>()));


        names = namesSet.toArray(new String[34]);
        valueStrings = valuesSet.toArray(new String[34]);
    }


}