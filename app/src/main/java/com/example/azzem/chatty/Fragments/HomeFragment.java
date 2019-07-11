package com.example.azzem.chatty.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.amulyakhare.textdrawable.TextDrawable;
import com.example.azzem.chatty.ChangePasswordActivity;
import com.example.azzem.chatty.LoginActivity;
import com.example.azzem.chatty.Model.User;
import com.example.azzem.chatty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

public class HomeFragment extends Fragment
{
    private Button btn_deleteAccount, btn_logout, btn_changeUsername, btn_change_about, btn_changePassword;
    private FloatingActionButton btn_ChangeProfile;
    private TextView textProgressDialog, username;
    private ImageView profileImage;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private static final int GALLERY_PICK = 1; //For request.

    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    FirebaseFirestore userRef;
    StorageReference mImageStorage;

    public HomeFragment()
    {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        //Initialize controllers
        btn_deleteAccount = view.findViewById(R.id.btn_delete_account);
        btn_logout = view.findViewById(R.id.btn_logout);
        btn_changeUsername = view.findViewById(R.id.btn_change_username);
        btn_change_about = view.findViewById(R.id.btn_change_about);
        btn_ChangeProfile = view.findViewById(R.id.change_profile_image);
        username = view.findViewById(R.id.username);
        btn_changePassword = view.findViewById(R.id.btn_change_password);
        profileImage = view.findViewById(R.id.profile_image);
        collapsingToolbarLayout = view.findViewById(R.id.coll_tool_bar);

        Typeface typeface = ResourcesCompat.getFont(inflater.getContext(), R.font.quicksandbold);
        collapsingToolbarLayout.setCollapsedTitleTypeface(typeface);
        collapsingToolbarLayout.setExpandedTitleTypeface(typeface);

        //---Get the current user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseFirestore.getInstance();

        btn_deleteAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ShowDialog();
            }
        });

        //---LISTENER FOR LOGOUT BUTTON
        btn_logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
                builder.setTitle("Are you sure you want to logout ?");
                builder.setMessage("Exiting will limit your availability,"+ "\n" + "and you might not receive messages" + "\n" + "on Chatty.");
                builder.setCancelable(true);

                builder.setPositiveButton("Logout", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Choose alert dialog when this button is clicked
                        dialog.cancel();
                    }
                });
                //create alert dialog
                androidx.appcompat.app.AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        //---LISTENER OF CHANGE PASSWORD BUTTON
        btn_changePassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent change_password = new Intent(getContext(), ChangePasswordActivity.class);
                startActivity(change_password);
            }
        });

        //Create method for display user info.
        ReadUserInfo();

        //---CHANGE NAME---//
        btn_changeUsername.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_username,
                        (ViewGroup) getView(), false);
                final TextView charachter_count = viewInflated.findViewById(R.id.CharacterCount);
                final EditText usernameField = viewInflated.findViewById(R.id.input_username);
                builder.setView(viewInflated);
                builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        String new_username = usernameField.getText().toString().trim();
                        if(!TextUtils.isEmpty(new_username))
                        {
                            changeUsername(new_username);
                        }
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.cancel();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                usernameField.addTextChangedListener(new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                    {
                    }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                    {
                        charachter_count.setText(String.valueOf(15 - charSequence.length()));
                    }
                    @Override
                    public void afterTextChanged(Editable editable)
                    {
                        if(TextUtils.isEmpty(editable))
                        {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }
                        else
                        {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }
                });
            }
        });

        //---CHANGE ABOUT-----//
        btn_change_about.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_about,
                        (ViewGroup) getView(), false);
                final TextView charachter_count2 = viewInflated.findViewById(R.id.CharacterCount2);
                charachter_count2.bringToFront();
                final EditText aboutField = viewInflated.findViewById(R.id.input_about);
                builder2.setView(viewInflated);
                builder2.setPositiveButton("SAVE", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        String new_about = aboutField.getText().toString().trim();
                        if(!TextUtils.isEmpty(new_about))
                        {
                            ChangeAbout(new_about);
                        }
                    }
                });
                builder2.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.cancel();
                    }
                });
                final AlertDialog dialog2 = builder2.create();
                dialog2.show();
                dialog2.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                aboutField.addTextChangedListener(new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                    {
                    }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                    {
                        charachter_count2.setText(String.valueOf(70 - charSequence.length()));
                    }
                    @Override
                    public void afterTextChanged(Editable editable)
                    {
                        if(TextUtils.isEmpty(editable))
                        {
                            dialog2.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }
                        else
                        {
                            dialog2.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }
                });
            }
        });


        //---CHANGE PROFILE IMAGE
        btn_ChangeProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Start intent for piking image from the gallery.
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*"); //*??
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                //create Chooser for open the document...
                //Title for the galleryIntent.
                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"), GALLERY_PICK);
                // start picker to get image for cropping and then use the image in cropping activity
                //Pick the image and let you crop them.
                /*CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(inflater.getContext(), HomeFragment.this);*/

            }
        });
        return view;
    }

    private void ShowDialog()
    {
        final Dialog dialog = new Dialog(getContext());
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
                EditText CurrentPassword = dialog.findViewById(R.id.edit_current_password);
                String password = CurrentPassword.getText().toString();
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
                                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
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

    private boolean validate(EditText CurrentPassword, String password)
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
                    Toast.makeText(getContext(), "User deleted", Toast.LENGTH_SHORT).show();
                    DeleteUserInfo();
                }
                else
                {
                    Toast.makeText(getContext(), "User not deleted", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void DeleteUserInfo()
    {
        userRef.collection("Users")
                .document(firebaseUser.getUid())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(getContext(), "User info has been deleted", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getContext(), "User info not deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        //Delete User Message.
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //Check the request code.
        if(requestCode == GALLERY_PICK && resultCode == Activity.RESULT_OK)
        {
            //I get the result as data.
            //the uri that i will get from the galleryIntent if i selecti image i will get the uri...
            Uri imageUri = data.getData();

            //Start the image CropActivity.
            CropImage.activity(imageUri)
                     //maintain a square pixel image.
                     //crop the image in square pixels.
                     .setAspectRatio(1, 1)
                     .start(getContext(), HomeFragment.this);
        }
        //If the request code pass to the CropActivity.
        //Make sure that the result which are getting is from the CropActivity they have created...
            if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            //Store the result in "result" variable and get the data from CropImageActivity.
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == Activity.RESULT_OK)
            {
                //----Create ProgressDialog.------------------------------------------------------//
                //create AlertDialog which will show this layout with ProgressBar
                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);  //the user wait for some process to finish,
                dialog.setContentView(R.layout.progressdialog);
                textProgressDialog = dialog.findViewById(R.id.text_progressBar);
                textProgressDialog.setText("Uploading image...");
                dialog.show();
                //--------------------------------------------------------------------------------//

                //Give the uri of the data.
                //Uri of the CroppedImage. !
                Uri resultUri = result.getUri();
                //Now store it this Uri in Firebase Storage.
                //The last child is our file.
                //In the last child i want to store the name of the image.
                //To store a really good name store the id of the user every time the user store an image !
                final StorageReference filepath = mImageStorage.child("profile_images").child(firebaseUser.getUid() + "jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            //Store image link in firebase database.
                            //Toast.makeText(getContext(), "Working", Toast.LENGTH_SHORT).show();
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri uri)
                                {
                                    String download_url = uri.toString();
                                    Log.d("HomeFragment", "onSuccess: uri profile image = " + uri.toString());

                                    userRef.collection("Users")
                                           .document(firebaseUser.getUid())
                                           .update("imageURL", download_url)
                                           .addOnCompleteListener(new OnCompleteListener<Void>()
                                           {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task)
                                               {
                                                   if(task.isSuccessful())
                                                   {
                                                       dialog.dismiss();
                                                       Toast.makeText(getContext(), "Success Uploading...", Toast.LENGTH_SHORT).show();
                                                   }
                                                   else
                                                   {
                                                       dialog.dismiss();
                                                       Toast.makeText(getContext(), "Error in Uploading...", Toast.LENGTH_SHORT).show();
                                                   }
                                               }
                                           });
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Error in Uploading.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
            }
            else
            {
                if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                {
                    Exception error = result.getError();
                }
            }
        }
    }

    private void ReadUserInfo()
    {
        userRef.collection("Users")
               .document(firebaseUser.getUid())
               .get()
               .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot)
                    {
                        final User user1 = documentSnapshot.toObject(User.class);
                        if(user1 != null)
                        {
                            //username.setText(user1.getUsername());
                            collapsingToolbarLayout.setTitle(user1.getUsername());

                            if(user1.getImageURL().equals("default"))
                            {
                                String letter = String.valueOf(user1.getUsername().charAt(0));
                                int color = 0;
                                //int color = generator.getRandomColor();
                                TextDrawable drawable = TextDrawable.builder().buildRound(letter, color); // radius in px
                                //profile_image.setBackground(drawable);
                            }
                            else
                            {
//                               // profile_image.setBackground(null);
//                                Picasso.get().load(user1.getImageURL())
//                                        //I want to load the image offline.
//                                        //This line of code retrieve the image offline.
//                                        .networkPolicy(NetworkPolicy.OFFLINE)
//                                        //Callback for checking if the task is successful.
//                                        .into(profile_image, new Callback()
//                                        {
//                                            @Override
//                                            public void onSuccess()
//                                            {
//                                                //If the task isSuccessful --> Any things.
//                                            }
//                                            @Override
//                                            public void onError(Exception e)
//                                            {
//                                                //if the task is not successful load the image online.
//                                                Picasso.get().load(user1.getImageURL()).into(profile_image);
//                                            }
//                                        });
                                Picasso.get().load(user1.getImageURL()).into(profileImage);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                    }
                }) ;
    }


    private void changeUsername(final String new_username)
    {
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

        assert current_user != null;
        String id_current_user = current_user.getUid();

        userRef.collection("Users")
               .document(id_current_user)
               .update("username", new_username)
               .addOnCompleteListener(new OnCompleteListener<Void>()
               {
                   @Override
                   public void onComplete(@NonNull Task<Void> task)
                   {
                       if(task.isSuccessful())
                       {
                           Toast.makeText(getContext(), "Changed" + new_username, Toast.LENGTH_LONG).show();
                       }
                       else
                       {
                           Toast.makeText(getContext(), "problem hna" + new_username, Toast.LENGTH_SHORT).show();
                       }
                   }
               });
        userRef.collection("Users")
                .document(id_current_user)
                .update("search", new_username)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                             System.out.println("Success Changed" + new_username);
                        }
                        else
                        {
                            System.out.println("Failed Changed" + new_username);
                        }
                    }
                });
    }

    private void ChangeAbout(final String new_about)
    {
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        if (current_user != null)
        {
            String id_current_user = current_user.getUid();
            userRef.collection("Users")
                   .document(id_current_user)
                   .update("about", new_about)
                   .addOnCompleteListener(new OnCompleteListener<Void>()
                   {
                       @Override
                       public void onComplete(@NonNull Task<Void> task)
                       {
                           if(task.isSuccessful())
                           {
                               Toast.makeText(getContext(), "Changed" + new_about, Toast.LENGTH_LONG).show();
                           }
                           else
                           {
                               Toast.makeText(getContext(), "problem hna" + new_about, Toast.LENGTH_SHORT).show();
                           }
                       }
                   });
        }
    }
}




//    private TextView username, username2, user_email, textProgressDialog, aboutText;
//    ImageView changeName, settingsIcon, changeProfileImage, changeAbout;
//    CircleImageView profile_image;
//    FirebaseUser fuser;
//    DatabaseReference reference;
//    FirebaseFirestore rootRef;
//    // private ColorGenerator generator = ColorGenerator.MATERIAL;
//        username2 = view.findViewById(R.id.username2);
//        user_email = view.findViewById(R.id.user_email);
//        profile_image = view.findViewById(R.id.profile_image);
//        changeName = view.findViewById(R.id.change_name);
//        settingsIcon = view.findViewById(R.id.settings_icon);
//        changeProfileImage = view.findViewById(R.id.change_profile_image);
//        changeAbout = view.findViewById(R.id.change_about);
//        aboutText = view.findViewById(R.id.description_about);
//        btn = view.findViewById(R.id.test);
//
//        btn.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                startActivity(new Intent(getContext(), Main2Activity.class));
//            }
//        });
//
//        //-----OFFLINE CAPABILITIES-----//
//        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setPersistenceEnabled(true)
//                .build();
//        rootRef = FirebaseFirestore.getInstance();
//        rootRef.setFirestoreSettings(settings);
//        //------------------------------//
//
//        fuser = FirebaseAuth.getInstance().getCurrentUser();
//
//        assert fuser != null;
//        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
//
//        //Offline capabilities.
//        //Keep the data synced as String or integer but not as integer !
//        //Store data locally in my device each time i run the app activity without empty image..etc
//        reference.keepSynced(true);
//
//        mImageStorage = FirebaseStorage.getInstance().getReference();
//
//        if(fuser == null)
//        {
//            startActivity(new Intent(getContext(), RegisterActivity.class));
//        }
