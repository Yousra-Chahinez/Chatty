package com.example.azzem.chatty;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ImageButton btn_cancel;
    private FloatingActionButton btn_reset;
    private EditText email_address;
    private ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        //Initialize controllers
        initViews();
        //-----------LISTENER TO RESET BUTTON-----------//

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text_email = email_address.getText().toString();
                validate(text_email);
                if(!isNetworkAvailble())
                {
                    AlertDialog.Builder networkDialog = new AlertDialog.Builder(ForgotPasswordActivity.this);
                    networkDialog.setTitle("No Internet Connection");
                    networkDialog.setMessage("Sorry, No Internet connectivity detected. Please reconnect " +
                            "and try again.");
                    networkDialog.setCancelable(true);
                    networkDialog.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Choose alert dialog when this button is clicked
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = networkDialog.create();
                    alertDialog.show();
                }
                else
                if(!validate(text_email))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                    //builder.setTitle("Authentication failed");
                    builder.setMessage("You can't reset your password without email.");
                    builder.setCancelable(true);
                    builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    //create alert dialog
                    //Remarque: le constructeur show appel create.
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else
                    {
                        progressDialog.setTitle("Registering...");
                        progressDialog.setMessage("Please wait while we create your account.");
                        progressDialog.show();
                    firebaseAuth.sendPasswordResetEmail(text_email).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                                builder.setTitle("Check your email");
                                builder.setMessage("We have sent you a reset password link on your registered email address.");
                                builder.setCancelable(true);
                                builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Choose alert dialog when this button is clicked
                                        dialog.cancel();
                                        //---Go To Login
                                        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                });
                                //create alert dialog
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                            else
                                {
                                    progressDialog.dismiss();
                                String error = task.getException().getMessage();
                                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                                //builder.setTitle("");
                                builder.setMessage("Make sure you enter the correct e-mail address");
                                builder.setCancelable(true);
                                builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
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
        });
        //----------------------------------------------//
        //-----------GO TO LOGIN-----------//
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                finish();
            }
        });
        //---------------------------------//
    }
    private void initViews()
    {
        btn_reset = findViewById(R.id.btn_reset);
        email_address = findViewById(R.id.email_address);
        btn_cancel = findViewById(R.id.btn_cancel);
    }


    public boolean validate(String text_email)
    {
        boolean valid = true;
        //Verify email !!
        if (text_email.isEmpty())
        {
            email_address.setError("Field can't be empty !");
            valid = false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(text_email).matches())
        {
            email_address.setError("Enter a valid email address");
        }
        else
        {
            email_address.setError(null);
        }
        return valid;
    }

    private boolean isNetworkAvailble()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
