package com.example.ecologyproject.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ecologyproject.R;
import com.example.ecologyproject.model.Indice;
import com.example.ecologyproject.model.Station;

public class IndiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indice_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        LinearLayout bg = findViewById(R.id.indice_bg);

        Intent intent = getIntent();
        Station station = (Station) intent.getSerializableExtra("station");
        Indice indice = (Indice) intent.getSerializableExtra("indice");

        TextView indiceCode = findViewById(R.id.indice_code);
        TextView indiceValue = findViewById(R.id.indice_value);
        TextView indiceElement = findViewById(R.id.indice_element);
        TextView indiceColorDesc = findViewById(R.id.indice_color_description);
        TextView indiceAddress = findViewById(R.id.indice_address);
        TextView indiceTime = findViewById(R.id.indice_time);
        TextView indiceDesc = findViewById(R.id.indice_description);
        TextView indiceEffect = findViewById(R.id.indice_effect);

        indiceCode.setText(Html.fromHtml(indice.getCode()));
        indiceValue.setText(indice.getValue().toString());
        indiceElement.setText(indice.getElement());
        indiceColorDesc.setText(indice.getColorDescription());
        indiceAddress.setText(station.getCity() + ", " +station.getAddress());
        indiceTime.setText(station.getDate());
        indiceDesc.setText(indice.getDescription());
        indiceEffect.setText(indice.getEffect());
        bg.setBackgroundColor(Color.parseColor(indice.getColor()));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}