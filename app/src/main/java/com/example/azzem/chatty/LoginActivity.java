package com.example.azzem.chatty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends AppCompatActivity {
    private Button login_btn;
    private MaterialEditText email_address, password;
    private TextView register_textView, forgot_psw;
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
        register_textView.setOnClickListener(new View.OnClickListener() {
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

    }
    private void initViews()
    {
        login_btn = findViewById(R.id.btn_login);
        email_address = findViewById(R.id.email_address);
        password = findViewById(R.id.password);
        register_textView = findViewById(R.id.register_textView);
        forgot_psw = findViewById(R.id.forgot_psw);
    }

    public boolean validate(String text_email, String text_password)
    {
        boolean valid = true;
        //Verify email !!
        if(text_email.isEmpty()){
            email_address.setError("Enter a valid email address");
            valid = false;
        } else {
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
                    startActivity(main_intent);
                    finish();
                }
                else
                    {
                    progressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Authentication failed");
                    builder.setMessage("Verify that you have entered the correct email or password.");
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
  }
