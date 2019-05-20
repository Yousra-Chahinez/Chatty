package com.example.azzem.chatty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private MaterialEditText username, email_address, password;
    private Button btn_register;
    private TextView login_textView;
    private ProgressDialog progressDialog;
    FirebaseFirestore rootRef;
    DocumentReference uiRef;
    //Firebase
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initialize controllers
        initViews();
        //-------GO TO LOGIN----------//
        login_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_login = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent_login);
                finish();
            }
        });
        //----------------------------//

        //Init ProgressDialog
        progressDialog = new ProgressDialog(this);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseFirestore.getInstance();

        //-------LISTENER TO REGISTER BUTTON----------//
        btn_register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                final String name = username.getText().toString();
                final String text_email = email_address.getText().toString();
                final String text_password = password.getText().toString();

                validate(name, text_email, text_password);

                if (!validate(name, text_email, text_password)) {
                    //Error
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Authentication failed");
                    builder.setMessage("Please try again...");
                    //builder.setIcon(R.drawable.ic_action_name);
                    builder.setCancelable(true); //by default it is cancelable
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
                } else {
                    progressDialog.setTitle("Registering...");
                    progressDialog.setMessage("Please wait while we create your account.");
                    progressDialog.show();
                    SignUpSucess(name, text_email, text_password);
                }
            }
        });
        //--------------------------------------------//
    }

    private void initViews()
    {
        username = findViewById(R.id.username);
        email_address = findViewById(R.id.email_address);
        password = findViewById(R.id.password);
        btn_register = findViewById(R.id.btn_register);
        login_textView = findViewById(R.id.login_textView);
    }
    public boolean validate(String name, String text_email, String text_password)
    {
        final boolean[] valid = {true};
        //Verify text_email !!
        if(text_email.isEmpty())
        {
            email_address.setError("Enter a valid email address");
            valid[0] = false;
        } else
            {
            email_address.setError(null);
            }
        if(text_password.isEmpty() || text_password.length() < 6){
            password.setError("at least 6 characters");
            valid[0] = false;
        }
        else
            {
            password.setError(null);
            }
        Query unique_username_query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").equalTo(name);
        unique_username_query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getChildrenCount()>0)
                {
                    username.setError("Choose another name please");
                    valid[0] = false;
                    System.out.println("dok hona wech taffichi " + dataSnapshot.getChildrenCount());
                }
                else
                {
                    username.setError(null);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });
        if(name.isEmpty() || name.length() < 3)
        {
            username.setError("at least 3 characters");
            valid[0] = false;
        }
        else
        {
            username.setError(null);
        }
        return valid[0];
    }

    private void SignUpSucess(final String name, final String text_email, String text_password)
    {
        firebaseAuth.createUserWithEmailAndPassword(text_email, text_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    //getCurrentUser method to get the user's account data.
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    //Create a tree
                    assert firebaseUser != null;
                    String userid = firebaseUser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                    //--------------------------------FIRESTORE-----------------------------------//
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", userid);
                    hashMap.put("username", name);
                    hashMap.put("imageURL", "default");
                    hashMap.put("status", "offline");
                    hashMap.put("search", name.toLowerCase());
                    hashMap.put("about", "Hey there: i am using Chatty App");

                    rootRef.collection("Users").document(userid)
                            .set(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                @Override
                                public void onSuccess(Void aVoid)
                                {
                                    Log.d("RegisterActivity", "DocumentSnapshot" +
                                            "successfully written!");
                                    Intent main_intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    /*FLAG_ACTIVITY_CLEAR_TASK -->  toutes les tâches existantes qui seraient associées
                                      à l'activité seront effacées avant le démarrage de l'activité.
                                      C'est-à-dire que toutes les anciennes activités sont terminées.
                                      FLAG_ACTIVITY_NEW_TASK -, cette activité deviendra le début d'une nouvelle tâche sur
                                      cette pile d'historique.*/
                                    main_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(main_intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    Log.w("RegisterActivity", "Error writing document",
                                            e);
                                    progressDialog.dismiss();
                                    //Toast
                                    System.out.println("Erreur");
                                }
                            });
                    //----------------------------------------------------------------------------//
                }
            }
        });
    }
}
