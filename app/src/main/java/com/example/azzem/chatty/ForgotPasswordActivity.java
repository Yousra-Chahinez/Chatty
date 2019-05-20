package com.example.azzem.chatty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ForgotPasswordActivity extends AppCompatActivity {
    Button btn_reset, btn_cancel;
    MaterialEditText email_address;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        //Initialize controllers
        initViews();
        //-----------LISTENER TO RESET BUTTON-----------//

        firebaseAuth = FirebaseAuth.getInstance();

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text_email = email_address.getText().toString();
                validate(text_email);
                if(!validate(text_email)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                    //builder.setTitle("Authentication failed");
                    builder.setMessage("You can't reset your password without email");
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
                } else {
                    firebaseAuth.sendPasswordResetEmail(text_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                                builder.setTitle("Task successful");
                                builder.setMessage("Please check your E-Mail");
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
                                String error = task.getException().getMessage();
                                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                                //builder.setTitle("Task successful");
                                builder.setMessage(error);
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
        if (text_email.isEmpty()) {
            email_address.setError("Enter a valid email address");
            valid = false;
        } else {
            email_address.setError(null);
        }
        return valid;
    }
}
