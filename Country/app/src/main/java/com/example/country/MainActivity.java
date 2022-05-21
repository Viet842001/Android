package com.example.country;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "Country";
    private Button bt; //Load Data
    private Button bt2; //Money
    Drawable mDraw = null;
    private static final String url = "http://api.geonames.org/countryInfoJSON?username=duyviet";
    // tao ra 1 arraylist de lay du lieu tu json
    public static ArrayList<Country> ListCountry = new ArrayList<Country>();
    ListView listView;
    ArrayAdapter<Country> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //bat su kien button
        bt = findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed();
                bt2.setEnabled(true);
                bt2.setClickable(true);
            }
        });
        bt2 = findViewById(R.id.money);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,Money.class);
                startActivity(i);
            }
        });

    }

    public void LoadJsonDone(ArrayList<Country> ListJson)
    {
        ListCountry = ListJson;
        listView = findViewById(R.id.idListView);
        adapter =  new Adapter(MainActivity.this,R.layout.listview,ListCountry);
        listView.setAdapter(adapter);
        //su kien click vao khoang trong
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_SHORT).show();
                Country c = new Country();
                c = ListCountry.get(i);
                Intent intent = new Intent(MainActivity.this,DetailCountry.class);
                intent.putExtra("Detail",c);
                startActivity(intent);
            }
        });

    }
    //Khi bam vao nut loaddata(button)
    public void onButtonPressed()
    {
        Toast.makeText(this, "Loading Data", Toast.LENGTH_SHORT).show();
        //lay giu tu network thi phai chay thread khac ko duoc trong main theard
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Theard phu chay lay de lay du lieu json
                final ArrayList<Country> result = loadFromNetwork();
                //Dua giu lieu json lay duoc vao thread chinh de su dung
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadJsonDone(result);
                    }
                });
            }
        }).start();
    }


    private ArrayList<Country> loadFromNetwork() {
        String data = null;
        ArrayList<Country> result = null;
        HttpURLConnection httpUrlConnection = null;

        try {
            // 1. Get connection. 2. Prepare request (URI)
            httpUrlConnection = (HttpURLConnection) new URL(url).openConnection();

            // 3. This app does not use a request body
            // 4. Read the response
            InputStream in = new BufferedInputStream(httpUrlConnection.getInputStream());

            data = readStream(in);

            // parse json string
            result = parseJsonString(data);

        } catch (MalformedURLException exception) {
            Log.e(TAG, "MalformedURLException");
        } catch (IOException exception) {
            Log.e(TAG, "IOException");
        } finally {
            if (null != httpUrlConnection) {
                // 5. Disconnect
                httpUrlConnection.disconnect();
            }
        }

        //return String data;

        return result;
    }

//

    private ArrayList<Country> parseJsonString(String data) {
        ArrayList<Country> result = new ArrayList<Country>();

        try {
            // Get top-level JSON Object - a Map
            JSONObject responseObject = (JSONObject) new JSONTokener(data).nextValue();

            // Extract value of "earthquakes" key -- a List
            JSONArray earthquakes = responseObject.getJSONArray("geonames");

            // Iterate over earthquakes list
            for (int idx = 0; idx < earthquakes.length(); idx++) {

                // Get single earthquake mData - a Map
                JSONObject c = (JSONObject) earthquakes.get(idx);
                if(c.getString("currencyCode").equals("")) {
                    continue;
                }
                // Summarize earthquake mData as a string and add it to
                // result
                //result.add(new Country(c.getString("countryName"), c.getString("countryCode")));
                result.add(new Country(c.getString("continent"),
                        c.getString("capital"),
                        c.getString("population"),
                        c.getString("countryCode"),
                        c.getString("areaInSqKm"),
                        c.getString("countryName"),
                        c.getString("continentName"),
                        c.getString("currencyCode")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder data = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "IOException");
                }
            }
        }
        return data.toString();
    }



}