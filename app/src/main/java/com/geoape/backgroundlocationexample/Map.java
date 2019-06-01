package com.geoape.backgroundlocationexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class Map extends AppCompatActivity {

    WebView webMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        webMap = (WebView) findViewById(R.id.webMap);

        WebSettings webSettings = webMap.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webMap.loadUrl("file:///android_asset/index.html");

    }


    public void ejecutarFuncion(View view){

        webMap.evaluateJavascript("javascript: hola()",null);
//        webMap.evaluateJavascript("javascript: " + "updateFromAndroid(\"Holaaa\")", null);

        Toast.makeText(getApplicationContext(), "Se ha ejecutado la función.",Toast.LENGTH_LONG).show();

    }




}
