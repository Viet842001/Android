package com.example.country;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Money extends AppCompatActivity {

    private ImageView Image1;
    private ImageView Image2;
    private Spinner spinner1;
    private Spinner spinner2;
    private EditText number1;
    private EditText number2;
    private Button bt;
    private ArrayList list = new ArrayList<String>();
    String s1 = "";
    String s2 = "";
    String fullfrom = "";
    String fullto = "";
    String secondfrom = "";
    String secondto = "";
    public ArrayList<Country> ListCountry = new ArrayList<Country>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);
        Image1 = findViewById(R.id.first);
        Image2 = findViewById(R.id.second);
        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);
        number1 = findViewById(R.id.number1);
        number2 = findViewById(R.id.number2);
        bt = findViewById(R.id.ex);
        ListCountry = MainActivity.ListCountry;
        for (int i = 0; i < ListCountry.size(); i++) {
            list.add(ListCountry.get(i).getCurrencyCode());
        }
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_selected_item, list);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String url = "https://img.geonames.org/flags/x/" + ListCountry.get(i).getCountryCode().toLowerCase() + ".gif";
                Picasso.get().load(url).into(Image1);
                s1 = ListCountry.get(i).getCurrencyCode().toLowerCase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String url = "https://img.geonames.org/flags/x/" + ListCountry.get(i).getCountryCode().toLowerCase() + ".gif";
                Picasso.get().load(url).into(Image2);
                s2 = ListCountry.get(i).getCurrencyCode().toLowerCase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnButtonPressed();
            }
        });

    }

    public void OnButtonPressed() {
        if(s1.equals(s2))
        {
            Toast.makeText(Money.this, "2 loai tien te giong nhau", Toast.LENGTH_SHORT).show();
        }
        else
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String url = "https://" + s1 + ".fxexchangerate.com/" + s2 + ".xml";
                    loadfromnetwork(url);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            exchange();
                        }
                    });
                }
            }).start();


        }

    }

    private void loadfromnetwork(String url) {
        String data = null;
        HttpURLConnection httpUrlConnection = null;

        try {
            // 1. Get connection. 2. Prepare request (URI)
            httpUrlConnection = (HttpURLConnection) new URL(url).openConnection();

            // 3. This app does not use a request body
            // 4. Read the response
            InputStream in = new BufferedInputStream(httpUrlConnection.getInputStream());

            data = readStream(in);

            // parse XML to String
            parseXML(data);

        } catch (MalformedURLException exception) {
            Log.e("Money", "MalformedURLException");
        } catch (IOException exception) {
            Log.e("Money", "IOException");
        } finally {
            if (null != httpUrlConnection) {
                // 5. Disconnect
                httpUrlConnection.disconnect();
            }
        }

        //return String data;
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
            Log.e("Money", "IOException");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("Money", "IOException");
                }
            }
        }
        return data.toString();
    }


    private void parseXML(String data) {

        XMLDOMParser parser = new XMLDOMParser();
        Document document = parser.getDocument(data);
        NodeList nodeList = document.getElementsByTagName("item");
        String tygia = "";
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            NodeList DescriptionNode = element.getElementsByTagName("description");
            Element DescriptionEle = (Element) DescriptionNode.item(i);
            tygia = Html.fromHtml(DescriptionEle.getFirstChild().getNodeValue().trim()).toString();
            tygia = tygia.substring(0, tygia.lastIndexOf(System.lineSeparator()));
            tygia = tygia.substring(0, tygia.lastIndexOf(System.lineSeparator()));
            StringTokenizer token = new StringTokenizer(tygia, "=", false);
            fullfrom = token.nextToken().trim();
            fullto = token.nextToken().trim();
            token = new StringTokenizer(fullfrom, " ", false);
            secondfrom = token.nextToken().trim();
            token = new StringTokenizer(fullto, " ", false);
            secondto = token.nextToken().trim();
        }
    }

    private void exchange()
    {
        float n1,n2,u1,f;
        n1=Float.parseFloat(secondfrom);
        n2=Float.parseFloat(secondto);
        System.out.println(n1);
        System.out.println(n2);
        u1=Float.parseFloat(number1.getText().toString());
        System.out.println(u1);
        f=(u1*n2)/n1;
        System.out.println(f);
        String temp= Float.toString(f);
        number2.setText(temp);

    }
}

