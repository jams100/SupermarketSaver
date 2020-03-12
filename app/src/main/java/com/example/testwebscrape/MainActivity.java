package com.example.testwebscrape;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static String tescoUrl;
    static String superValuUrl;
    //static EditText editSearch;
    static TextView editSearch;

    private FirebaseAnalytics myFirebaseAnalytics;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    TextView userEmail;
    String user_email;
    Boolean checkLogin;
    MenuItem logOut;
    TextView navUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Obtain the Firebase Analytics instance.
        myFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        editSearch = findViewById(R.id.product_name);
        editSearch.setOnEditorActionListener(onEditorActionListener);

        Button button = findViewById(R.id.search);
        Button customSearch = findViewById(R.id.custom_search);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editSearch.getText().toString().trim().isEmpty()) {
                    buildTescoUrl();
                    buildSupervaluUrl();
                    Intent intent = new Intent(MainActivity.this, ProductList.class);
                    intent.putExtra("TescoUrl", tescoUrl);
                    intent.putExtra("SupervaluUrl", superValuUrl);
                    intent.putExtra("ProductName", editSearch.getText().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Search can not be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        customSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, Search.class);
//                startActivity(intent);
//            }
//        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser= firebaseAuth.getCurrentUser();

        View headerView = navigationView.getHeaderView(0);
        navUsername = (TextView) headerView.findViewById(R.id.user_label_email);
        if (firebaseUser != null) {
            navUsername.setText(firebaseUser.getEmail());
        }
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
                        Intent in = new Intent(MainActivity.this, ProductList.class);
                        in.putExtra("TescoUrl", tescoUrl);
                        in.putExtra("SupervaluUrl", superValuUrl);
                        in.putExtra("ProductName", editSearch.getText().toString());
                        startActivity(in);
                    } else {
                        Toast.makeText(MainActivity.this, "Search can not be empty", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            return false;
        }
    };

    //Used to build the TescoUrl link
    public void buildTescoUrl() {
        tescoUrl = "https://www.tesco.ie/groceries/product/search/default.aspx?searchBox=";
        tescoUrl += buildUrlEnd();
    }

    //Used to build the SuperValuUrl link
    public void buildSupervaluUrl() {
        superValuUrl = "https://shop.supervalu.ie/shopping/search/allaisles?q=";
        superValuUrl += buildUrlEnd();
    }

    //Building last part of url
    public String buildUrlEnd() {
        String s = editSearch.getText().toString().trim();
        s = s.replace(" ", "+");
        return s;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        logOut=menu.findItem(R.id.action_logout);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_logout:
                FirebaseUser User= firebaseAuth.getCurrentUser();
                if (User!=null){
                    firebaseAuth.signOut();
                    checkLogin=false;
                    user_email="user email ";
                    navUsername.setText(user_email);

                    Toast.makeText(this, "Logged out successful", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this, "Swipe Right to Login", Toast.LENGTH_LONG).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handle navigation view item clicks here.
        switch (item.getItemId()){
            case R.id.nav_login:
                Intent i=new Intent(MainActivity.this, LoginPage.class);
                startActivity(i);
                break;
            case R.id.nav_settings:
                Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_share:
                Toast.makeText(MainActivity.this, "Share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_saved:
                Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            user_email=currentUser.getEmail();
            Toast.makeText(MainActivity.this,user_email,Toast.LENGTH_LONG).show();
            checkLogin=true;
            navUsername.setText(user_email);
        }else{
            user_email="user email ";
            checkLogin=false;
            navUsername.setText(user_email);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            user_email=currentUser.getEmail();
            Toast.makeText(MainActivity.this,user_email,Toast.LENGTH_LONG).show();
            checkLogin=true;
            navUsername.setText(user_email);
        }else{
            user_email="user email ";
            navUsername.setText(user_email);
            checkLogin=false;
        }
    }
}