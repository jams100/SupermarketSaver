package com.example.testwebscrape;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

public class webView extends AppCompatActivity {
    private FirebaseAnalytics myFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        //Obtaining the FirebaseAnalytics instance.
        myFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        WebView web = findViewById(R.id.web);

        web.setWebViewClient(new WebViewClient());
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);

        Bundle bundle = getIntent().getExtras();
        web.loadUrl(bundle.getString("UrlWebLink"));
    }
}
