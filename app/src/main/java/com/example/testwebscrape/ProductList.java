package com.example.testwebscrape;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;

import com.example.testwebscrape.AdapterHelper.RecycleGridAdapter;
import com.example.testwebscrape.AdapterHelper.RecycleListAdapter;
import com.example.testwebscrape.WebScraper.QueryUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.example.testwebscrape.AdapterHelper.RecycleGridAdapter.SPAN_COUNT_ONE;
import static com.example.testwebscrape.AdapterHelper.RecycleGridAdapter.SPAN_COUNT_TWO;

public class ProductList extends AppCompatActivity  implements  LoaderManager.LoaderCallbacks<ArrayList<Products>> {

    private int currentViewMode=1;
    ArrayList<Products> product;
    TextView emptyState;

    ProgressBar progressBar1;
    int alreadySearched=0;

    //Url links set to blank
    static String TescoUrl="";
    static String SupervaluUrl ="";

    RecyclerView gridRecyclerView;
    private RecycleGridAdapter gridAdapter;
    private GridLayoutManager gridlayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        //Getting url links for websites
        Bundle bundle=getIntent().getExtras();
        TescoUrl=bundle.getString("TescoUrl");
        SupervaluUrl=bundle.getString("SupervaluUrl");

        progressBar1=findViewById(R.id.progress_circular);

        gridRecyclerView=findViewById(R.id.rv);
        gridlayoutManager=new GridLayoutManager(this, currentViewMode);

        //Checking the default view saved in the shared preference
        SharedPreferences sharedPreferences=getSharedPreferences("ViewMode",MODE_PRIVATE);
        currentViewMode=sharedPreferences.getInt("currentViewMode", SPAN_COUNT_ONE);

        //Displays back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Checking the network connectivity
        ConnectivityManager conManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=conManager.getActiveNetworkInfo();
        //Checking network connectivity
        if (networkInfo !=null && networkInfo.isConnected()){

            if(alreadySearched==0) {
                getSupportLoaderManager().initLoader(100, null, this).forceLoad();
                alreadySearched=1;
            }
        }else{
            progressBar1.setVisibility(View.GONE);
//            emptyState.setText("No network Connection");
        }
    }

    //saving instance for before rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("alreadySearch",alreadySearched);
        super.onSaveInstanceState(outState);
    }

    //restoring instaces after rotation
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        alreadySearched=savedInstanceState.getInt("alreadySearch");
        super.onRestoreInstanceState(savedInstanceState);
    }

    //used to set the Adapter
    private void setAdapter() {
        gridAdapter=new RecycleGridAdapter(product, gridlayoutManager);
        gridRecyclerView.setLayoutManager(gridlayoutManager);
        gridRecyclerView.setAdapter(gridAdapter);

        gridAdapter.setOnItemClickListener(new RecycleGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Products pro=product.get(position);
                String url=pro.getUrlLink();

                Intent i=new Intent(ProductList.this,webView.class);
                i.putExtra("UrlWebLink",url);
                startActivity(i);
            }

            @Override
            public void onShareClick(int position) {
                Products pro=product.get(position);
                String url=pro.getUrlLink();

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "I found this item in price Compare App \n"+url);
                shareIntent.setType("text/plain");
                startActivity(shareIntent);
                Toast.makeText(ProductList.this,"the share postion "+position,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSaveClick(int position) {
                product.get(position);
                Toast.makeText(ProductList.this,"the postion "+position,Toast.LENGTH_SHORT).show();
            }
        });
    }

    //setting the menu with the switch mode option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //listener when an item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_switch_layout:
                if (SPAN_COUNT_ONE==currentViewMode){
                    currentViewMode=SPAN_COUNT_TWO;
                    gridlayoutManager.setSpanCount(currentViewMode);
                }else{
                    currentViewMode=SPAN_COUNT_ONE;
                    gridlayoutManager.setSpanCount(currentViewMode);
                }

                //switch the view
                switchIcon(item);
                SwitchLayout();

                //save
                SharedPreferences sharedPreferences=getSharedPreferences("ViewMode",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putInt("currentViewMode",currentViewMode);
                editor.commit();
                break;

            //used to close the activity
            case R.id.search:
                finish();
                break;

            //back button used to close the activity
            case android.R.id.home:
                finish();
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
    private void switchIcon(MenuItem item) {
        if (gridlayoutManager.getSpanCount() == SPAN_COUNT_TWO) {
            item.setIcon(getResources().getDrawable(R.drawable.ic_grid_icon));
        } else {
            item.setIcon(getResources().getDrawable(R.drawable.ic_list_icon));
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
        if (data!=null){
            UpdateUi(data);
        }else {

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
            }else{
                forceLoad();
            }
        }

        @Override
        public void deliverResult(ArrayList<Products> data) {
            // We’ll save the data for later retrieval
            produ = data;
            super.deliverResult(data);
        }

        @Nullable
        @Override
        public ArrayList<Products> loadInBackground() {
            ArrayList<Products>  prod= (ArrayList<Products>) QueryUtil.fetchWebsiteData(TescoUrl, SupervaluUrl);

            if(prod!=null){

                //Used to sorting
                Collections.sort(prod, new Comparator<Products>() {
            @Override
            public int compare(Products o1, Products o2) {
                String p1 = o1.PriceNew;
                String p2 = o2.PriceNew;

                return p1.compareTo(p2);
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
}