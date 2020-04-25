package com.example.testwebscrape;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.testwebscrape.AdapterHelper.RecycleGridAdapter;
import com.example.testwebscrape.DataModel.Products;
import com.example.testwebscrape.WebScraper.QueryUtil;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.example.testwebscrape.AdapterHelper.RecycleGridAdapter.SPAN_COUNT_ONE;
import static com.example.testwebscrape.AdapterHelper.RecycleGridAdapter.SPAN_COUNT_TWO;

public class ProductList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Products>> {

    private int currentViewMode = 1;
    ArrayList<Products> product;

    ProgressBar progressBar1;
    ImageButton switchLayout;
    int alreadySearched = 0;
    RelativeLayout relLayout;

    //Url links set to blank
    static String TescoUrl = "";
    static String SupervaluUrl = "";
    static String[] filter_words;

    private FirebaseAnalytics myFirebaseAnalytics;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    private static final String TAG = "MyActivity";
    FirebaseAuth myAuth;
    FirebaseUser fireUser;
    DatabaseReference databaseReference;
    //String TAG=ProductList.class.getSimpleName();

    RecyclerView gridRecyclerView;
    private RecycleGridAdapter gridAdapter;
    private GridLayoutManager gridlayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        SharedPreferences sharedPreferences = getSharedPreferences("ViewMode", MODE_PRIVATE);
        currentViewMode = sharedPreferences.getInt("currentViewMode", currentViewMode);

        //Firebase database Reference
        databaseReference= FirebaseDatabase.getInstance().getReference("SavedProducts");
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        //Obtain the FirebaseAnalytics instance.
        myFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setSupportActionBar((Toolbar) findViewById(R.id.product_toolbar));
        progressBar1 = findViewById(R.id.progress_circular);

        switchLayout = findViewById(R.id.layout_switcher);
        relLayout = findViewById(R.id.container_switcher);
        relLayout.setVisibility(View.GONE);

        Spinner mySpinner=findViewById(R.id.sort_product);
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String selectedText = null;

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedText = parentView.getItemAtPosition(position).toString();
                switch (selectedText)
                {
                    case "Price (Low to High)":
                        sortPriceOrder(0);
                        gridAdapter.notifyDataSetChanged();
                        break;
                    case "Price (High to Low)":
                        sortPriceOrder(1);
                        gridAdapter.notifyDataSetChanged();
                        break;
                    case "Precio (Bajo a Alto)":
                        sortPriceOrder(0);
                        gridAdapter.notifyDataSetChanged();
                        break;
                    case "Precio (Alto a Bajo)":
                        sortPriceOrder(1);
                        gridAdapter.notifyDataSetChanged();
                        break;
                    case "Prix (Bas en Haut)":
                        sortPriceOrder(0);
                        gridAdapter.notifyDataSetChanged();
                        break;
                    case "Prix (Haut en Bas)":
                        sortPriceOrder(1);
                        gridAdapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        //Setting spinner input
        ArrayAdapter<String> sortAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.sort));
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mySpinner.setAdapter(sortAdapter);

        //Used to switch the layout
        switchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SPAN_COUNT_ONE == currentViewMode) {
                    currentViewMode = SPAN_COUNT_TWO;
                    gridlayoutManager.setSpanCount(currentViewMode);
                } else {
                    currentViewMode = SPAN_COUNT_ONE;
                    gridlayoutManager.setSpanCount(currentViewMode);
                }

                switchIcon(switchLayout);
                //switch the view
                SwitchLayout();

                //save the current view mode
                SharedPreferences sharedPreferences = getSharedPreferences("ViewMode", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("currentViewMode", currentViewMode);
                editor.commit();
            }
        });
        //Initializing Firebase Auth
        myAuth = FirebaseAuth.getInstance();

        //Getting url links for websites
        Bundle bundle = getIntent().getExtras();
        TescoUrl = bundle.getString("TescoUrl");
        SupervaluUrl = bundle.getString("SupervaluUrl");

        gridRecyclerView = findViewById(R.id.rv);
        gridlayoutManager = new GridLayoutManager(this, currentViewMode);

        //Displays back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Checking the network connectivity
        ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        //Checking network connectivity
        if (networkInfo != null && networkInfo.isConnected()) {

            if (alreadySearched == 0) {
                getSupportLoaderManager().initLoader(100, null, this).forceLoad();
                alreadySearched = 1;
            }
        } else {
            progressBar1.setVisibility(View.GONE);
            relLayout.setVisibility(View.GONE);
        }

        //Checking the default view saved in the shared preference
        //SharedPreferences sharedPreferences=getSharedPreferences("ViewMode",MODE_PRIVATE);
        //currentViewMode=sharedPreferences.getInt("currentViewMode", SPAN_COUNT_ONE);

        //Use to switch the display Icon
        switchIcon(switchLayout);
    }

    //Saving instance for before rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("alreadySearch", alreadySearched);
        super.onSaveInstanceState(outState);
    }

    //Restoring instances after rotation
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        alreadySearched = savedInstanceState.getInt("alreadySearch");
        super.onRestoreInstanceState(savedInstanceState);
    }

    //Used to set the Adapter
    private void setAdapter() {
        gridAdapter = new RecycleGridAdapter(product, gridlayoutManager);
        gridRecyclerView.setLayoutManager(gridlayoutManager);
        gridRecyclerView.setAdapter(gridAdapter);

        gridAdapter.setOnItemClickListener(new RecycleGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Products pro = product.get(position);
                String url = pro.getUrlLink();

                Intent i = new Intent(ProductList.this, webView.class);
                i.putExtra("UrlWebLink", url);
                startActivity(i);
            }

            @Override
            public void onShareClick(int position) {
                Products pro = product.get(position);
                String url = pro.getUrlLink();

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                //shareIntent.putExtra(Intent.EXTRA_TEXT, "I found this item on the SupermarketSaver App, check it out. \n" + url);
                shareIntent.putExtra(Intent.EXTRA_TEXT, ProductList.this.getString(R.string.item_found) + "\n" + url);

                shareIntent.setType("text/plain");
                startActivity(shareIntent);
                Toast.makeText(ProductList.this, ProductList.this.getString(R.string.searching_product), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSaveClick(final int position) {
                Products prod = product.get(position);
                fireUser = myAuth.getCurrentUser();

                if (fireUser != null) {
                    if (!prod.isImageChanged()) {
                        //Used to save a product
                        String userid = fireUser.getUid();
                        String id = databaseReference.push().getKey();

                        Products products = new Products(prod.getProductDescription(), prod.getPriceOld(), prod.getImageProduct(),
                                prod.getUrlLink(), prod.getImageLogo(), prod.getPriceNew());

                        assert id != null;
                        databaseReference.child(userid).child(id).setValue(products);
                        Toast.makeText(ProductList.this, ProductList.this.getString(R.string.product_saved), Toast.LENGTH_SHORT).show();
                        gridAdapter.changeImage(position);

                }else {
                    //Method used to delete a saved product by unclicking save button
                    DatabaseReference ref =FirebaseDatabase.getInstance().getReference("SavedProducts").child(fireUser.getUid());
                    Query deleteQuery = ref.orderByChild("urlLink").equalTo(prod.getUrlLink());

                    deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot deleteSnapShot : dataSnapshot.getChildren()){
                                deleteSnapShot.getRef().removeValue();
                                Toast.makeText(ProductList.this, ProductList.this.getString(R.string.item_removed), Toast.LENGTH_SHORT).show();

                                gridAdapter.removeImage(position);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled", databaseError.toException());
                        }
                    });
                }
                } else
                    {
                        Toast.makeText(ProductList.this, ProductList.this.getString(R.string.loginOrSignup_toSave), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ProductList.this, LoginPage.class));
                }
            }
        });
    }

    //Setting the menu with the switch mode option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //Listener for when an item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            //Back button used to close the activity
            case android.R.id.home:
                mBundleRecyclerViewState = null;
                MainActivity.editSearch.setFocusable(true);
                finish();
                break;

            case R.id.saved_items:
                fireUser = myAuth.getCurrentUser();
                if (fireUser != null) {
                    startActivity(new Intent(ProductList.this, SavedProducts.class));
                } else {
                    startActivity(new Intent(ProductList.this, LoginPage.class));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //used to switch the layout
    private void SwitchLayout() {
        gridlayoutManager.setSpanCount(currentViewMode);
        gridAdapter.notifyItemRangeChanged(0, gridAdapter.getItemCount());
    }

    //used to switch the icons
    private void switchIcon(ImageButton item) {
        if (gridlayoutManager.getSpanCount() == SPAN_COUNT_TWO) {
            item.setBackground(getResources().getDrawable(R.drawable.ic_grid_icon));
        } else {
            item.setBackground(getResources().getDrawable(R.drawable.ic_list_icon));
        }
    }

    @NonNull
    @Override
    public Loader<ArrayList<Products>> onCreateLoader(int id, @Nullable Bundle args) {
        return new ProductAsyncLoader(ProductList.this);
    }

    //Call when background thread has finished loading
    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Products>> loader, ArrayList<Products> data) {
        progressBar1.setVisibility(View.GONE);
        if (data != null) {
            UpdateUi(data);
            relLayout.setVisibility(View.VISIBLE);
        } else {

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Products>> loader) {
        UpdateUi(null);
    }

    private static class ProductAsyncLoader extends AsyncTaskLoader<ArrayList<Products>> {
        private ArrayList<Products> produ;

        ProductAsyncLoader(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            if (produ != null) {
                // Use cached data
                deliverResult(produ);
            } else {
                forceLoad();
            }
        }

        @Override
        public void deliverResult(ArrayList<Products> data) {
            super.deliverResult(data);
            // We’ll save the data for later retrieval
            produ = data;
        }

        @Nullable
        @Override
        public ArrayList<Products> loadInBackground() {
            ArrayList<Products> prod = (ArrayList<Products>) QueryUtil.fetchWebsiteData(TescoUrl, SupervaluUrl);
            //If there is no data then do not sort
            if (prod != null) {

                //Used to sort by price from cheapest to most expensive
                Collections.sort(prod, new Comparator<Products>() {
                            @Override
                            public int compare(Products o1, Products o2) {
                                String p1 = o1.getPriceNew().trim();
                                String p2 = o2.getPriceNew().trim();

                                p1 = p1.replace("€", "");
                                p1 = p1.replace(".", "");
                                p1 = p1.trim();
                                Log.i(TAG, "SaveAdapter.getView() — get item number " + p1 + p2);

                                p2 = p2.replace("€", "");
                                p2 = p2.replace(".", "");
                                p2 = p2.trim();

                                return Integer.valueOf(p1) - (Integer.valueOf(p2));
                            }
                        });
            }
            produ = prod;
            return prod;
        }
    }

    //Used to update xml layouts
    private void UpdateUi(ArrayList<Products> data) {
        product = data;
        setAdapter();
    }

    public void sortPriceOrder(final int order) {
        progressBar1.setVisibility(View.VISIBLE);
        if (product != null)
        {
            //Used for sorting
            Collections.sort(product, new Comparator<Products>() {
                @Override
                public int compare(Products o1, Products o2) {
                    String p1 = o1.getPriceNew().trim();
                    String p2 = o2.getPriceNew().trim();

                    p1 = p1.replace("€", "");
                    p1 = p1.replace(".", "");
                    p1 = p1.trim();

                    p2 = p2.replace("€", "");
                    p2 = p2.replace(".", "");
                    p2 = p2.trim();

                    switch (order) {
                        case 0:
                            return Integer.valueOf(p1) - (Integer.valueOf(p2));
                        case 1:
                            return Integer.valueOf(p2) - (Integer.valueOf(p1));
                        default:
                            return Integer.valueOf(p1) - (Integer.valueOf(p2));
                    }
                }
            });
        }
        progressBar1.setVisibility(View.GONE);//Remove circular progress when finished
    }
}