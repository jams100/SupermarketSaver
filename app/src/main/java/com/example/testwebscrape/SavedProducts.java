package com.example.testwebscrape;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class SavedProducts extends AppCompatActivity {

    FirebaseAuth myAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    Toolbar toolbar;
    RecyclerView recyclerView;
    private GridLayoutManager gridlayoutManager;
    private int currentViewMode=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_products);

        myAuth=FirebaseAuth.getInstance();

        firebaseUser=myAuth.getCurrentUser();
        if (firebaseUser !=null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("SavedProducts").child(firebaseUser.getUid());
        }else{
            finish();
            Toast.makeText(this, "Login to view Saved Products", Toast.LENGTH_SHORT).show();
        }

        toolbar=findViewById(R.id.saved_toolbar);
        setSupportActionBar(toolbar);
        //Displaying back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView=findViewById(R.id.rv_saved);
        gridlayoutManager=new GridLayoutManager(this,currentViewMode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
