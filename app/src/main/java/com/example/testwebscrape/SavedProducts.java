package com.example.testwebscrape;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.annotation.NonNull;

import com.example.testwebscrape.AdapterHelper.SaveAdapter;
import com.example.testwebscrape.DataModel.Products;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SavedProducts extends AppCompatActivity {

    FirebaseAuth myAuth;
    FirebaseUser fireUser;
    DatabaseReference databaseReference;
    Toolbar toolbar;
    RecyclerView recyclerView;
    private SaveAdapter gridAdapter;
    private GridLayoutManager gridlayoutManager;
    private int currentViewMode = 1;
    ArrayList<Products> product = new ArrayList<Products>();
    String TAG = SavedProducts.class.getSimpleName();
    TextView nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_products);
        myAuth = FirebaseAuth.getInstance();

        fireUser = myAuth.getCurrentUser();
        if (fireUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("SavedProducts").child(fireUser.getUid());
        } else {
            finish();
            Toast.makeText(SavedProducts.this, SavedProducts.this.getString(R.string.login_for_saved_items), Toast.LENGTH_SHORT).show();
        }

        toolbar = findViewById(R.id.saved_toolbar);
        setSupportActionBar(toolbar);
        //Displaying back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Will remove the "No Items saved" in saved products page if user has a saved a product(s)
        nodata = findViewById(R.id.Hint);
        nodata.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.rv_saved);
        gridlayoutManager = new GridLayoutManager(this, currentViewMode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (product != null) {
                    product.clear();

                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        Products pro = productSnapshot.getValue(Products.class);
                        product.add(pro);
                    }
                    if (product.size() == 0) {
                        nodata.setVisibility(View.VISIBLE);
                    } else {
                        nodata.setVisibility(View.GONE);
                    }
                }
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //Used to set the Adapter
    private void setAdapter() {
        gridAdapter = new SaveAdapter(product, gridlayoutManager);
        recyclerView.setLayoutManager(gridlayoutManager);
        recyclerView.setAdapter(gridAdapter);

        gridAdapter.setOnItemClickListener(new SaveAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int position) {
                Products pro = product.get(position);
                String url = pro.getUrlLink();

                Intent i = new Intent(SavedProducts.this, webView.class);
                i.putExtra("UrlWebLink", url);
                startActivity(i);
            }

            @Override
            public void onShareClick(int position) {
                Products pro = product.get(position);
                String url = pro.getUrlLink();

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, SavedProducts.this.getString(R.string.item_found) + "\n" + url);
                shareIntent.setType("text/plain");
                startActivity(shareIntent);
            }

            @Override
            public void onDeleteClick(int position) {
                Products pro = product.get(position);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("SavedProducts").child(fireUser.getUid());
                Query deleteQuery = ref.orderByChild("urlLink").equalTo(pro.getUrlLink());

                deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot deleteSnapShot : dataSnapshot.getChildren()) {
                            deleteSnapShot.getRef().removeValue();
                            Toast.makeText(SavedProducts.this, SavedProducts.this.getString(R.string.item_deleted), Toast.LENGTH_SHORT).show();
                        }
                        gridAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException());
                    }
                });
            }
        });
    }
}