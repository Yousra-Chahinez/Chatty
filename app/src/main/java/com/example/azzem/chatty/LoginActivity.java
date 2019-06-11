package com.example.azzem.chatty;

import android.app.ActivityOptions;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FloatingActionButton login_btn;
    private EditText email_address, password;
    private Button register_button, forgot_psw;
    private ImageButton btn_cancel;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;

    @Override
    protected void onStart()
    {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        //----------APP CRASH----------//
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
        //-----------------------------//

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //initialize controllers
        initViews();
        //------GO TO REGISTER-------//
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register_intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(register_intent);
                finish();
            }
        });
        //-------------------------//

        progressDialog = new ProgressDialog(this);

        //-------LISTENER TO LOGIN BUTTON----------//
        login_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                String text_email = email_address.getText().toString();
                String text_password = password.getText().toString();
                validate(text_email, text_password);
                if(!isNetworkAvailable())
                {
                    //erreur
                    AlertDialog.Builder networkDialog = new AlertDialog.Builder(LoginActivity.this);
                    networkDialog.setTitle("No Internet Connection");
                    networkDialog.setMessage("Sorry, No Internet connectivity detected. Please reconnect " +
                            "and try again.");
                    //builder.setIcon(R.drawable.ic_action_name);
                    networkDialog.setCancelable(true); //by default it is cancelable
                    //set Negative/No button click listener
                    //Or builder.setNegativeButton("Ok",null);
                    networkDialog.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Choose alert dialog when this button is clicked
                            dialog.cancel();
                        }
                    });
                    //create alert dialog
                    AlertDialog alertDialog = networkDialog.create();
                    alertDialog.show();
                } else
                if(!validate(text_email, text_password))
                {
                    //erreur
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Authentication failed");
                    builder.setMessage("Please try again...");
                    //builder.setIcon(R.drawable.ic_action_name);
                    builder.setCancelable(true); //by default it is cancelable
                    //set Negative/No button click listener
                    //Or builder.setNegativeButton("Ok",null);
                    builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Choose alert dialog when this button is clicked
                            dialog.cancel();
                        }
                    });
                    //create alert dialog
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else
                    {
                    progressDialog.setTitle("Logging in...");
                    progressDialog.setMessage("Please wait while we check your credentials.");
                    progressDialog.show();
                    LoginSuccess(text_email, text_password);
                    }
            }
        });
        //-----------------------------------------//

        //------RESET PASSWORD-------//
        forgot_psw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reset_psw_intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(reset_psw_intent);
                finish();
            }
        });
        //--------------------------//

        //-----GO TO REGISTER ACTIVITY-----//
        btn_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
        //--------------------------//

    }
    private void initViews()
    {
        login_btn = findViewById(R.id.btn_login);
        email_address = findViewById(R.id.email_address);
        password = findViewById(R.id.password);
        register_button = findViewById(R.id.register_button);
        forgot_psw = findViewById(R.id.forgot_psw);
        btn_cancel = findViewById(R.id.btn_cancel);
    }

    public boolean validate(String text_email, String text_password)
    {
        boolean valid = true;
        //Verify email !!
        if(text_email.isEmpty()){
            email_address.setError("Field can't be empty !");
            valid = false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(text_email).matches()){
            email_address.setError("Please enter a valid email address");
        }
        else {
            email_address.setError(null);
        }
        if(text_password.isEmpty() || text_password.length() < 6){
            password.setError("At least 6 characters");
            valid = false;
        } else {
            password.setError(null);
        }
        return valid;
    }

    private void LoginSuccess(String text_email, String text_password)
    {
        firebaseAuth.signInWithEmailAndPassword(text_email, text_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    Intent main_intent = new Intent(LoginActivity.this, MainActivity.class);
                    main_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(main_intent, ActivityOptions.makeSceneTransitionAnimation(LoginActivity
                    .this).toBundle());
                    finish();
                }
                else
                    {
                    progressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Authentication failed");
                    builder.setMessage("Please check that you have entered the correct email or password.");
                    //builder.setIcon(R.drawable.ic_action_name);
                    builder.setCancelable(true); //by default it is cancelable
                    //set positive /Yes button clck listener
                    /*builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Choose the activity when this button is clicked
                            //I can do something else too
                        }
                    });*/
                    //set Negative/No button click listener
                    builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Choose alert dialog when this button is clicked
                            dialog.cancel();
                        }
                    });
                    //create alert dialog
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    }
            }
        });
    }
    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
  }
