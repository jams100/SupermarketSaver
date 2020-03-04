package com.example.testwebscrape;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    static String tescoUrl;
    static String superValuUrl;
    EditText editSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editSearch= findViewById(R.id.product_name);
        editSearch.setOnEditorActionListener(onEditorActionListener);

        Button button=findViewById(R.id.search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editSearch.getText().toString().trim().isEmpty()){
                buildTescoUrl();
                buildSupervaluUrl();
                Intent intent=new Intent(MainActivity.this,ProductList.class);
                intent.putExtra("TescoUrl", tescoUrl);
                intent.putExtra("SupervaluUrl", superValuUrl);
                startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this,"Search can not be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Method used to handle enter key event for search
    private TextView.OnEditorActionListener onEditorActionListener=new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            switch(actionId){
                case EditorInfo.IME_ACTION_SEARCH:
                    if(!editSearch.getText().toString().trim().isEmpty()){
                    buildTescoUrl();
                    buildSupervaluUrl();
                    Intent in =new Intent(MainActivity.this,ProductList.class);
                    in.putExtra("TescoUrl", tescoUrl);
                    in.putExtra("SupervaluUrl", superValuUrl);
                    startActivity(in);
                    }else {
                        Toast.makeText(MainActivity.this,"Search can not be empty",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            return false;
        }
    };

    //Used to build the TescoUrl link
    public void buildTescoUrl(){
            tescoUrl="https://www.tesco.ie/groceries/product/search/default.aspx?searchBox=";
            tescoUrl+=buildUrlEnd();
    }

    //used to build the SuperValuUrl link
    public void buildSupervaluUrl(){
        superValuUrl="https://shop.supervalu.ie/shopping/search/allaisles?q=";
        superValuUrl+=buildUrlEnd();
    }

    //building last part of url
    public String buildUrlEnd() {
        String s = editSearch.getText().toString().trim();
        s = s.replace(" ", "+");
        return s;
    }
}