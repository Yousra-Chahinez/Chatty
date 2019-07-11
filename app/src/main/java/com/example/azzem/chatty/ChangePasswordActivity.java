package com.example.azzem.chatty;

import android.content.DialogInterface;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ChangePasswordActivity extends AppCompatActivity
{
    private MaterialEditText old_password_text, new_password_text;
    private FloatingActionButton btn_change_password;
    private Toolbar toolbar;
    private Button btn_cancel;
    private FirebaseUser fuser;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        //Initialize controllers
        initViews();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        //----LISTENER FOR CHANGE PASSWORD BUTTON.
        btn_change_password.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final String oldPasswordText = old_password_text.getText().toString();
                final String newPasswordText = new_password_text.getText().toString();
                System.out.println("hada password legdim" + oldPasswordText + "w hada djdid" + newPasswordText);
                validate(oldPasswordText, newPasswordText);
                if(!validate(oldPasswordText, newPasswordText))
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                    builder.setTitle("Failed");
                    builder.setMessage("All the fields are required");
                    builder.setCancelable(true);
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else
                {
                    //ChangePassword(oldPasswordText, newPasswordText);
                    //String email_of_current_user = fuser.getEmail();
                    //assert email_of_current_user != null;
                    //System.out.println("rayha naffichi l password legdim 9bel " + old_password_text + "w email " + email_of_current_user);
                    AuthCredential authCredential  = EmailAuthProvider.getCredential(fuser.getEmail(), old_password_text.getText().toString());
                    //System.out.println("rayha naffichi l password legdim " + old_password_text + "w email" + email_of_current_user);
                    fuser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                fuser.updatePassword(newPasswordText).addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Snackbar snackbar_su = Snackbar.make(btn_cancel, "Password successfully modified",
                                                    Snackbar.LENGTH_SHORT);
                                            snackbar_su.show();
                                        } else {
                                            Snackbar snackbar_fail = Snackbar.make(btn_cancel, "Something went wrong. Please try again later",
                                                    Snackbar.LENGTH_SHORT);
                                            snackbar_fail.show();
                                        }
                                    }
                                });
                            }
                            else
                            {
                                Snackbar snackbar_su = Snackbar.make(btn_cancel, "Authentication Failed",
                                        Snackbar.LENGTH_SHORT);
                                snackbar_su.show();
                            }
                        }
                    });
                }
            }
        });
        //----LISTENER FOR CANCEL BUTTON
//        btn_cancel.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                Intent cancel_intent = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
//                startActivity(cancel_intent);
//                finish();
//            }
//        });
    }

    private void initViews()
    {
        old_password_text = findViewById(R.id.old_password_text);
        new_password_text = findViewById(R.id.new_password_text);
        btn_change_password = findViewById(R.id.btn_change_password);
        btn_cancel = findViewById(R.id.btn_cancel);
        toolbar = findViewById(R.id.toolbar);
    }


    private boolean validate(String oldPasswordText, String newPasswordText)
    {
        boolean valid = true;
        if(oldPasswordText.isEmpty() || oldPasswordText.length() < 6)
        {
            old_password_text.setError("At least 6 characters");
            valid = false;
        }
        else
        {
            old_password_text.setError(null);
        }
        if(newPasswordText.isEmpty() || oldPasswordText.length() < 6)
        {
            new_password_text.setError("At leasr 6 characters");
            valid = false;
        }
        else
        {
            new_password_text.setError(null);
        }
        return valid;
    }
}
/*
removeUser(password: string) {
  this.af.auth.take(1).subscribe(user => {
    const credential = firebase.auth.EmailAuthProvider.credential(user.auth.email, password);
    user.auth.reauthenticate(credential).then(() => {
      user.auth.delete();
    });
  });
}
*/