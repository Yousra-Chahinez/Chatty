package com.example.azzem.chatty;

import android.app.Dialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SettingsActivity extends AppCompatActivity
{
    private Button changePassword, btn_logout, btn_deleteAccount;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    FirebaseFirestore rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //----Init Views
        changePassword = findViewById(R.id.btn_change_password);
        btn_logout = findViewById(R.id.btn_logout);
        btn_deleteAccount = findViewById(R.id.btn_delete_account);
        rootRef = FirebaseFirestore.getInstance();

        //---LISTENER OF CHANGE PASSWORD BUTTON
        changePassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent change_password = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(change_password);
                finish();
            }
        });
        //---LISTENER FOR LOGOUT BUTTON
        btn_logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final Dialog logoutDialog = new Dialog(SettingsActivity.this);
                logoutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                logoutDialog.setCancelable(false);
                logoutDialog.setContentView(R.layout.confirmation_dialog);
                //logoutDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                TextView textViewConfirmation = logoutDialog.findViewById(R.id.confirmation_text);
                textViewConfirmation.setText("Are you sure you want to logout ?");
                TextView textViewDescription = logoutDialog.findViewById(R.id.description_confirmation);
                textViewDescription.setText("Exiting will limit your availability,"+ "\n" + "and you might not receive messages" + "\n" + "on Chatty.");
                Button positive_btn = logoutDialog.findViewById(R.id.btn_oui);
                positive_btn.setText("Logout");

                positive_btn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        logoutDialog.dismiss();
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
                Button btn_cancel_logout = logoutDialog.findViewById(R.id.btn_cancel);
                btn_cancel_logout.setText("Cancel");

                btn_cancel_logout.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        logoutDialog.dismiss();
                    }
                });
                logoutDialog.show();
            }
        });
        //---LISTENER FOR DELETE ACCOUNT BUTTON
        btn_deleteAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ShowDialog();
            }
        });
        //---Get the current user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void ShowDialog()
    {
        final Dialog dialog = new Dialog(SettingsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_delete_account);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        final Button deleteButton = dialog.findViewById(R.id.btn_delete_account2);
        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                MaterialEditText CurrentPassword = dialog.findViewById(R.id.edit_current_password);
                String password = CurrentPassword.getText().toString();
                System.out.println("password " + password);
                validate(CurrentPassword, password);
                if(!validate(CurrentPassword, password))
                {
                    //deleteButton.setEnabled(false);
                }
                else
                {
                    //deleteButton.setEnabled(true);
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                dialog.dismiss();
                                DeleteAccount();
                                Toast.makeText(SettingsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(SettingsActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        Button cancelDelete_btn = dialog.findViewById(R.id.btn_cancel_delete_account2);
        cancelDelete_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private boolean validate(MaterialEditText CurrentPassword, String password)
    {
        boolean valid = true;
        if(password.isEmpty() || password.length()<6)
        {
            CurrentPassword.setError("At least 6 characters");
            valid = false;
        }
        else
        {
            CurrentPassword.setError(null);
        }
        return valid;
    }

    private void DeleteAccount()
    {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(SettingsActivity.this, "User deleted", Toast.LENGTH_SHORT).show();
                    DeleteUserInfo();
                }
                else
                {
                    Toast.makeText(SettingsActivity.this, "User not deleted", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void DeleteUserInfo()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        rootRef.collection("Users")
               .document(firebaseUser.getUid())
               .delete()
               .addOnCompleteListener(new OnCompleteListener<Void>()
               {
                   @Override
                   public void onComplete(@NonNull Task<Void> task)
                   {
                       if(task.isSuccessful())
                       {
                           Toast.makeText(SettingsActivity.this, "User info has been deleted", Toast.LENGTH_SHORT).show();
                       }
                       else
                       {
                           Toast.makeText(SettingsActivity.this, "User info not deleted", Toast.LENGTH_SHORT).show();
                       }
                   }
               });

        //Delete user message:
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("messages").child(firebaseUser.getUid());
        reference1.removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(SettingsActivity.this, "User messages has been deleted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SettingsActivity.this, RegisterActivity.class));
                    finish();
                }
                else
                {
                    Toast.makeText(SettingsActivity.this, "User messages not deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
