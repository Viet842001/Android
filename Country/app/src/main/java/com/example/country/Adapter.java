package com.example.country;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;

//custom lai adapter theo y minh
public class Adapter extends ArrayAdapter<Country> {
    Context context;
    int resource;
    List<Country> objects;
    Drawable mDraw = null;
    public Adapter(Context context, int resource, List<Country> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview,parent,false);
        }
        TextView name = convertView.findViewById(R.id.countryname);
        TextView code = convertView.findViewById(R.id.countrycode);
        TextView currency = convertView.findViewById(R.id.currencycode);
        Button btn = convertView.findViewById(R.id.button);
        ImageView imageView = convertView.findViewById(R.id.imageView);

        Country country = objects.get(position);
        name.setText(country.getCountryName());
        code.setText(country.getCountryCode());
        currency.setText(country.getCurrencyCode());
        String url = "https://img.geonames.org/flags/x/"+objects.get(position).getCountryCode().toLowerCase()+".gif";
        Picasso.get().load(url).into(imageView);

//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(context, "Đang nhấn cái gì đó " + position, Toast.LENGTH_SHORT).show();
//                Country c = new Country();
//                c = objects.get(position);
//                Intent intent = new Intent(getContext(),DetailCountry.class);
//                intent.putExtra("Detail",c);
//                startActivity(intent);
//
//            }
//        });
        return  convertView;
    }
}
