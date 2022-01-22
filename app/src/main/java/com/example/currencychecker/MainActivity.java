package com.example.currencychecker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
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

//TODO make the list sorted with the most popular currencies on top

public class MainActivity extends AppCompatActivity {

    RecyclerView currenciesList;
    private RequestQueue requestQueue;


    //TODO store currencies as a single object
//    HashMap<String, Double> currencies;

    String[] names = new String[34];
    String[] valueStrings = new String[34];
    double[] values = new double[34];


    Long lastUpdateTime;
    Long currentTime;

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currenciesList = findViewById(R.id.CurrenciesList);

        currentTime = System.currentTimeMillis();

        getSavedData();


        Button converterButton = findViewById(R.id.Converter_button);
        Button updateButton = findViewById(R.id.Update_Button);


        requestQueue = Volley.newRequestQueue(getApplicationContext());


        RecycleViewAdapter adapter = new RecycleViewAdapter(this, names, valueStrings);
        currenciesList.setAdapter(adapter);
        currenciesList.setLayoutManager(new LinearLayoutManager(this));



        if (needToUpdate()) {
            Parse();
            adapter.swapItems(names, valueStrings);
            adapter.notifyDataSetChanged();     //one of these might be unnecessary
        }


        updateButton.setOnClickListener(view -> {
            Parse();
            adapter.swapItems(names, valueStrings);
            adapter.notifyDataSetChanged();     //one of these might be unnecessary
        });

        //TODO implement Converter activity (task 2)
        converterButton.setOnClickListener(view -> {
            Intent converterIntent = new Intent(view.getContext(), ConverterActivity.class);
            converterIntent.putExtra("names", names);
            converterIntent.putExtra("values", values);

            view.getContext().startActivity(converterIntent);
        });

    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("names", names);
        outState.putSerializable("values", values);
        outState.putSerializable("valuesStrings", valueStrings);
        outState.putLong("lastUpdateTime", lastUpdateTime);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState !=null) {
            names = (String[]) getIntent().getSerializableExtra("names");
            valueStrings = (String[]) getIntent().getSerializableExtra("valueStrings");
            values = (double[]) getIntent().getSerializableExtra("values");
            lastUpdateTime = (Long) getIntent().getSerializableExtra("lastUpdateTime");
        }
    }



    private void Parse() {
        Log.i("LOOK HERE", "Parsing started");
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

                            //TODO what's the point of having both double and string arrays for values? fix
                            names[i] = name;
                            valueStrings[i] = String.valueOf(value) + " руб.";
                            values[i] = value;

                            i++;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> error.printStackTrace());

        requestQueue.add(request);

        //saving data
        Set<String> savedNames = new HashSet<>(Arrays.asList(names));
        Set<String> savedValues = new HashSet<>(Arrays.asList(valueStrings));
        lastUpdateTime = System.currentTimeMillis();

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putStringSet("names", savedNames);
        editor.putStringSet("values", savedValues);
        editor.putLong("lastUpdateTime", lastUpdateTime);

        editor.apply();
    }

    //TODO doesn't work upon first opening the app
    private boolean needToUpdate() {
        boolean isNull = (names == null || valueStrings == null);
        //update every day
        boolean isTimeToUpdate = ((lastUpdateTime==0) || (currentTime - lastUpdateTime)>=(3600000 * 24));

        return isNull||isTimeToUpdate;
    }


    private void getSavedData() {
        SharedPreferences pref = this.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        //TODO fix this bad inefficient solution that randomises the order of the currencies(noticeable when updating)
        Set<String> namesSet = new HashSet<>(pref.getStringSet("names", new HashSet<>()));
        Set<String> valuesSet = new HashSet<>(pref.getStringSet("values", new HashSet<>()));

        names = namesSet.toArray(new String[34]);
        valueStrings = valuesSet.toArray(new String[34]);
        values = getDoubleValues(valueStrings);

        lastUpdateTime = pref.getLong("lastUpdateTime", 0);
    }

    double[] getDoubleValues(String[] stringValues) {
        if (stringValues[0] == null) {
            return new double[34];
        }
        String[] temp = Arrays.copyOf(stringValues, stringValues.length);

        for (int i = 0; i<temp.length; i++) {
            int index = temp[i].indexOf(" ");
            temp[i] = temp[i].substring(0, index);
        }

    
        return Arrays.stream(temp)
                .mapToDouble(Double::parseDouble)
                .toArray();
    }


}