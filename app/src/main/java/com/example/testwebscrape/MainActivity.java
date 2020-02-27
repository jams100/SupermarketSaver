package com.example.testwebscrape;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    static String tescoUrl;
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
                buildTescoUrl();
                Intent intent=new Intent(MainActivity.this,ProductList.class);
                intent.putExtra("url", tescoUrl);
                startActivity(intent);
            }
        });
    }

    //Method used to handle enter key event for search
    private TextView.OnEditorActionListener onEditorActionListener=new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            switch(actionId){
                case EditorInfo.IME_ACTION_SEARCH:
                    buildTescoUrl();
                    Intent in =new Intent(MainActivity.this,ProductList.class);
                    in.putExtra("url", tescoUrl);
                    startActivity(in);
                    break;
            }
            return false;
        }
    };

    //Used to build the Tesco url link
    public void buildTescoUrl(){
            tescoUrl="https://shop.supervalu.ie/shopping/search/allaisles?q=";
            String s = editSearch.getText().toString();
            s = s.replace(" ", "+");
            tescoUrl += s;
    }
}
