package com.example.ecologyproject.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecologyproject.R;
import com.example.ecologyproject.model.Indice;
import com.example.ecologyproject.model.Station;
import com.example.ecologyproject.remote.RetrofitClient;
import com.example.ecologyproject.service.GraphService;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IndiceActivity extends AppCompatActivity {
    private WebView webView;

    TextView indiceCode, indiceValue, indiceElement, indiceColorDesc,
            indiceAddress, indiceTime, indiceDesc, indiceEffect,
            colorLineYellow, colorLineOrange, colorLineRed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indice_activity);

        webView = findViewById(R.id.webView);
        webView.setInitialScale(155);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().getJavaScriptCanOpenWindowsAutomatically();

        setBar();
        setTvs();
    }

    private void setTvs() {
        LinearLayout bg = findViewById(R.id.indice_bg);

        Intent intent = getIntent();
        Station station = (Station) intent.getSerializableExtra("station");
        Indice indice = (Indice) intent.getSerializableExtra("indice");

        indiceCode = findViewById(R.id.indice_code);
        indiceValue = findViewById(R.id.indice_value);
        indiceElement = findViewById(R.id.indice_element);
        indiceColorDesc = findViewById(R.id.indice_color_description);
        indiceAddress = findViewById(R.id.indice_address);
        indiceTime = findViewById(R.id.indice_time);
        indiceDesc = findViewById(R.id.indice_description);
        indiceEffect = findViewById(R.id.indice_effect);

        colorLineYellow = findViewById(R.id.color_line_yellow);
        colorLineOrange = findViewById(R.id.color_line_orange);
        colorLineRed = findViewById(R.id.color_line_red);

        indiceCode.setText(Html.fromHtml(indice.getCode()));
        indiceValue.setText(indice.getValue().toString());
        indiceElement.setText(indice.getElement());
        indiceColorDesc.setText(indice.getColorDescription());
        indiceAddress.setText(station.getCity() + ", " +station.getAddress());
        indiceTime.setText(station.getDate());
        indiceDesc.setText(indice.getDescription());
        indiceEffect.setText(indice.getEffect());
        bg.setBackgroundColor(Color.parseColor(indice.getColor()));

        colorLineYellow.setText(indice.getGreen().toString());
        colorLineOrange.setText(indice.getYellow().toString());
        colorLineRed.setText(indice.getOrange().toString());

        GraphService graphService = RetrofitClient.getRetrofit().create(GraphService.class);
        Call<ResponseBody> call = graphService.getGraph(station.getId(), Html.fromHtml(indice.getCode()).toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String html = response.body().string();
                        // Load the HTML content into the WebView
                        webView.loadData(html, "text/html", "UTF-8");
                        webView.setWebViewClient(new WebViewClient() {
                            @Override
                            public void onPageFinished(WebView view, String url) {
                                super.onPageFinished(view, url);
                                view.loadUrl("javascript:(function() { " +
                                        "var css = 'body { font-family: Roboto !important; }', " +
                                        "head = document.head || document.getElementsByTagName('head')[0], " +
                                        "style = document.createElement('style'); " +
                                        "style.type = 'text/css'; " +
                                        "if (style.styleSheet){ " +
                                        "style.styleSheet.cssText = css; " +
                                        "} else { " +
                                        "style.appendChild(document.createTextNode(css)); " +
                                        "} " +
                                        "head.appendChild(style); " +
                                        "})();");
                            }
                        });
                    } catch (Exception e) {
                        Log.e("web", "Error: " + e.getMessage());
                    }
                } else {
                    Log.e("web", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("web", "Error: " + t.getMessage());
            }
        });
    }

    private void setBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue_header));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_header)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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