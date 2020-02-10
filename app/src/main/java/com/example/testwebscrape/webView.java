package com.example.testwebscrape;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class webView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        WebView web=findViewById(R.id.web);

        WebSettings webSettings=web.getSettings();
        webSettings.setJavaScriptEnabled(true);

        Bundle bundle=getIntent().getExtras();
        web.loadUrl(bundle.getString("url"));
    }
}
