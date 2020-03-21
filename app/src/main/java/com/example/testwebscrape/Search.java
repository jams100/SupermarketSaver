package com.example.testwebscrape;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

public class Search extends AppCompatActivity {

    static EditText editSearch;
    ImageButton voiceSearch;
    static String superValuUrl;
    static String tescoUrl;
    private static final int RECOGNIZER_RESULT=1;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Obtaining the Firebase Analytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Toolbar toolbar = findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Material search");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        //Used to display the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editSearch = findViewById(R.id.search_product);
        editSearch.setOnEditorActionListener(onEditorActionListener);
        voiceSearch = findViewById(R.id.voice_search);

        //Used for voice
        voiceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Connecting to the Google api
                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                //Choosing which speech model to use
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice Search");
                try {
                    startActivityForResult(speechIntent, RECOGNIZER_RESULT);
                } catch (Exception e) {
                    Toast.makeText(Search.this, "Something went wrong with starting the voice search try again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

        //Result from voice search
        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
            if (requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK && data != null) {
                ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                editSearch.setText(matches.get(0).toString());
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

        //listener when an item is selected
        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            switch (item.getItemId()) {
                //Back button
                case android.R.id.home:
                    finish();
                    break;
            }
            return super.onOptionsItemSelected(item);
        }

        //Method used to handle enter key event for search
        private TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        if (!editSearch.getText().toString().trim().isEmpty()) {
                            buildTescoUrl();
                            buildSupervaluUrl();
                            Intent in = new Intent(Search.this, ProductList.class);
                            in.putExtra("TescoUrl", tescoUrl);
                            in.putExtra("SupervaluUrl", superValuUrl);
                            in.putExtra("ProductName", editSearch.getText().toString());
                            startActivity(in);
                        } else {
                            Toast.makeText(Search.this, "Search can not be empty", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return false;
            }
        };

        //Used to build the TescoUrl link
        public void buildTescoUrl () {
            tescoUrl = "https://www.tesco.ie/groceries/product/search/default.aspx?searchBox=";
            tescoUrl += buildUrlEnd();
        }

        //Used to build the SuperValuUrl link
        public void buildSupervaluUrl () {
            superValuUrl = "https://shop.supervalu.ie/shopping/search/allaisles?q=";
            superValuUrl += buildUrlEnd();
        }

        //Building last part of url
        public String buildUrlEnd () {
            String s = editSearch.getText().toString().trim();
            s = s.replace(" ", "+");
            return s;
        }
    }