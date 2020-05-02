package com.example.testwebscrape;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testwebscrape.DataModel.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginPage extends AppCompatActivity {

    private static final int GOOGLE_SIGN = 40;
    Button signUpBtn;
    Button loginBtn;
    EditText emailAddress;
    EditText password;
    ProgressBar progressBar;
    String TAG = LoginPage.class.getSimpleName();

    private FirebaseAnalytics myFirebaseAnalytics;
    FirebaseAuth myAuth;
    FirebaseUser firebaseUser;
    TextView errorMessage;
    Button googleSign;
    GoogleSignInClient mGoogleSignInClient;

    public static final String NODE_USERS = "users";//Table that users are saved into
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        //Obtaining the Firebase Analytics instance.
        myFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        loginBtn = findViewById(R.id.login_button);
        signUpBtn = findViewById(R.id.sign_up_button);
        emailAddress = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progress_login_bar);
        errorMessage = findViewById(R.id.error_message);
        Toolbar toolbar = findViewById(R.id.login_toolbar);
        googleSign = findViewById(R.id.google_button);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Login");

        //Used to display the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialising Firebase Auth
        myAuth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!emailAddress.getText().toString().trim().isEmpty() && !password.getText().toString().trim().isEmpty()) {
                    errorMessage.setText(null);
                    progressBar.setVisibility(View.VISIBLE);
                    myAuth.signInWithEmailAndPassword(emailAddress.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        //Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        firebaseUser = myAuth.getCurrentUser();

                                        FirebaseInstanceId.getInstance().getInstanceId()
                                                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                        if (task.isSuccessful()) {
                                                            String token = task.getResult().getToken();
                                                            saveToken(token);
                                                        } else {

                                                        }
                                                    }
                                                });


                                        finish();
                                    } else {
                                        errorMessage.setText(getString(R.string.login_error_message));
                                        //If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginPage.this, LoginPage.this.getString(R.string.authentication_failed), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(LoginPage.this, LoginPage.this.getString(R.string.must_enter_email_password), Toast.LENGTH_LONG).show();
                }
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this, SignUp.class));
            }
        });

        //Used when signing in with google
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        googleSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIntent=mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signIntent, GOOGLE_SIGN);
            }
        });
    }

    //Saving Token To Firebase
    private void saveToken(String token) {
        String email = mAuth.getCurrentUser().getEmail();
        User user = new User(email, token);

        DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference(NODE_USERS);

        dbUsers.child(mAuth.getCurrentUser().getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG ,"User's Token from Login Page Saved");
                    //Toast.makeText(LoginPage.this, "Token Saved", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode==GOOGLE_SIGN){
            // The Task returned from this call is always completed
            Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account=task.getResult(ApiException.class);
                if (account!=null){
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG ,"FIREBASE authentication with google "+ account.getId());

        AuthCredential authCredential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        myAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d(TAG,"signin success");
                    firebaseUser= myAuth.getCurrentUser();
                    Toast.makeText(LoginPage.this, LoginPage.this.getString(R.string.login_successful), Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Log.d(TAG,"signin failed "+ task.getException());
                    Toast.makeText(LoginPage.this, LoginPage.this.getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Listener when an item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Back button used to close this activity
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        //Check if user is signed in and update UI accordingly.
        FirebaseUser currentUser = myAuth.getCurrentUser();
        if (currentUser != null) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = myAuth.getCurrentUser();
        if (currentUser != null) {
            finish();
        }
    }
}