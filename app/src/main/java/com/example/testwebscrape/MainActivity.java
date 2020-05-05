package com.example.testwebscrape;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.testwebscrape.Location.GoogleMapsActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static TextView editSearch;

    private FirebaseAnalytics myFirebaseAnalytics;
    FirebaseAuth firebaseAuth;
    FirebaseUser fireUser;
    String user_email;
    Boolean checkLogin;
    MenuItem logOut;
    TextView navUsername;

    ImageView btnlogo;
    Button getLocation, getSupermarketLocation;
    TextView searchProducts, welcomemsg;
    Animation frombottom, slowerfrombottom, fromtop;

    public static final String CHANNEL_ID = "notifications";
    private static final String CHANNEL_NAME = "Simple Message";
    private static final String CHANNEL_DESC = "Supermarket Saver Notifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Animation for Loading Location Button
        getLocation = (Button) findViewById(R.id.locator_button);
        slowerfrombottom = AnimationUtils.loadAnimation(this, R.anim.slowerfrombottom);
        getLocation.setAnimation(slowerfrombottom);

        //Animation for Loading Search TextBox
        searchProducts = (TextView) findViewById(R.id.product_name);
        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        searchProducts.setAnimation(frombottom);

        //Animation for Loading Welcome Message
        welcomemsg = (TextView) findViewById(R.id.welcome);
        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        welcomemsg.setAnimation(fromtop);

        //Animation for Loading Supermarket Saver logo
        btnlogo = (ImageView) findViewById(R.id.logo_image);
        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        btnlogo.setAnimation(fromtop);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Obtain the Firebase Analytics instance.
        myFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        editSearch = findViewById(R.id.product_name);
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        editSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Search.class));
            }
        });

        getSupermarketLocation = findViewById(R.id.locator_button);
        getSupermarketLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GoogleMapsActivity.class));
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        fireUser = firebaseAuth.getCurrentUser();

        View headerView = navigationView.getHeaderView(0);
        navUsername = (TextView) headerView.findViewById(R.id.user_label_email);
        if (fireUser != null) {
            navUsername.setText(fireUser.getEmail());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        logOut = menu.findItem(R.id.action_logout);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout:
                FirebaseUser User = firebaseAuth.getCurrentUser();
                if (User != null) {
                    firebaseAuth.signOut();
                    checkLogin = false;
                    user_email = "User email ";
                    navUsername.setText(user_email);
                    LogOutGoogleSignIn();

                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.log_out), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.click_to_login), Toast.LENGTH_LONG).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void LogOutGoogleSignIn() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_login:
                Intent i = new Intent(MainActivity.this, LoginPage.class);
                startActivity(i);
                break;
            case R.id.nav_share:
                String url = "https://github.com/jams100/SupermarketSaver";
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "I found this cool Supermarket price comparison app on the Play store" +
                        " check it out! \n" + url);
                shareIntent.putExtra(Intent.EXTRA_TEXT, MainActivity.this.getString(R.string.sharing_app_message) + "\n" + url);
                shareIntent.setType("text/plain");
                startActivity(shareIntent);
                break;
            case R.id.nav_saved:
                startActivity(new Intent(MainActivity.this, SavedProducts.class));
                break;
            case R.id.nav_special_offers:
                Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.searching_special_offers), Toast.LENGTH_SHORT).show();
                Intent inte = new Intent(MainActivity.this, ProductList.class);
                inte.putExtra("TescoUrl", "https://www.tesco.ie/groceries/ProductBuylist/default.aspx?id=L00005147&icid=Top_Offers_top");
                inte.putExtra("SupervaluUrl", "https://shop.supervalu.ie/shopping/specialoffers");
                startActivity(inte);
                break;
            case R.id.nav_alcohol:
                Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.searching_alcohol), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ProductList.class);
                intent.putExtra("TescoUrl", "https://www.tesco.ie/groceries/product/search/default.aspx?searchBox=alcohol");
                intent.putExtra("SupervaluUrl", "https://shop.supervalu.ie/shopping/search/allaisles?q=alcohol&departmentId=150100075");
                startActivity(intent);
                break;
            case R.id.nav_fruit:
                Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.searching_fruit), Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(MainActivity.this, ProductList.class);
                intent2.putExtra("TescoUrl", "https://www.tesco.ie/groceries/product/browse/default.aspx?N=4294954026&Ne=4294954028");
                intent2.putExtra("SupervaluUrl", "https://shop.supervalu.ie/shopping/search/allaisles?q=fruit&departmentId=150100001");
                startActivity(intent2);
                break;
            case R.id.nav_baby:
                Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.searching_baby), Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(MainActivity.this, ProductList.class);
                intent3.putExtra("TescoUrl", "https://www.tesco.ie/groceries/product/search/default.aspx?searchBox=baby");
                intent3.putExtra("SupervaluUrl", "https://shop.supervalu.ie/shopping/search/allaisles?q=baby%20items&departmentId=150100060");
                startActivity(intent3);
                break;
            case R.id.nav_gluten_free:
                Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.searching_gluten), Toast.LENGTH_SHORT).show();
                Intent intent4 = new Intent(MainActivity.this, ProductList.class);
                intent4.putExtra("TescoUrl", "https://www.tesco.ie/groceries/product/search/default.aspx?searchBox=gluten%20free");
                intent4.putExtra("SupervaluUrl", "https://shop.supervalu.ie/shopping/search/allaisles?q=gluten%20free");
                startActivity(intent4);
                break;
            case R.id.nav_vegan:
                Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.searching_vegan), Toast.LENGTH_SHORT).show();
                Intent intent5 = new Intent(MainActivity.this, ProductList.class);
                intent5.putExtra("TescoUrl", "https://www.tesco.ie/groceries/product/search/default.aspx?searchBox=vegan");
                intent5.putExtra("SupervaluUrl", "https://shop.supervalu.ie/shopping/search/allaisles?q=vegan");
                startActivity(intent5);
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
            user_email = currentUser.getEmail();
            Toast.makeText(MainActivity.this, user_email, Toast.LENGTH_LONG).show();
            checkLogin = true;
            navUsername.setText(user_email);
        } else {
            user_email = "User email ";
            checkLogin = false;
            navUsername.setText(user_email);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            user_email = currentUser.getEmail();
            checkLogin = true;
            navUsername.setText(user_email);
        } else {
            user_email = "User email ";
            navUsername.setText(user_email);
            checkLogin = false;
        }
    }
}