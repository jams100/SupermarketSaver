package com.example.testwebscrape;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {

    Button signUp;
    EditText email;
    EditText password;
    EditText confirmPassword;
    TextView errorMessage;
    ProgressBar progressBar;
    Toolbar toolbar;
    private FirebaseAnalytics myFirebaseAnalytics;
    FirebaseAuth firebaseAuth;
    String TAG=SignUp.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Obtaining the Firebase Analytics instance.
        myFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        signUp=findViewById(R.id.signup_btn);
        email=findViewById(R.id.signup_email);
        password=findViewById(R.id.signup_password);
        confirmPassword=findViewById(R.id.signp_confirm_password);
        errorMessage=findViewById(R.id.error_signup);
        progressBar=findViewById(R.id.signup_progress);
        toolbar=findViewById(R.id.sign_up_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Sign Up");

        //Used to display the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialising Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorMessage.setText(null);
                progressBar.setVisibility(View.VISIBLE);
                if (!email.getText().toString().trim().isEmpty() && !password.getText().toString().trim().isEmpty()
                        && !confirmPassword.getText().toString().trim().isEmpty()){

                    if (password.getText().toString().matches(confirmPassword.getText().toString())){
                        firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressBar.setVisibility(View.GONE);
                                        if (task.isSuccessful()) {
                                            //If the sign up is successful, update UI with the signed-in user's information
                                            Log.d(TAG, "createUserWithEmail:success");
                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                            Toast.makeText(SignUp.this, SignUp.this.getString(R.string.account_created), Toast.LENGTH_LONG).show();
                                            finish();

                                        } else {
                                            //If the sign up fails, display a message to the user.
                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(SignUp.this, SignUp.this.getString(R.string.authentication_failed), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }else {
                        errorMessage.setText(getString(R.string.password_dont_match));
                    }
                }else{
                    Toast.makeText(SignUp.this, SignUp.this.getString(R.string.must_enter_email_password), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Listener when an item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //Back button used to close the activity
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}