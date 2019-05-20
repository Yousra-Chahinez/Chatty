package com.example.azzem.chatty.Fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.azzem.chatty.Model.User;
import com.example.azzem.chatty.R;
import com.example.azzem.chatty.RegisterActivity;
import com.example.azzem.chatty.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.constraint.Constraints.TAG;

public class HomeFragment extends Fragment
{
    TextView username, username2, user_email, textProgressDialog, aboutText;
    ImageView changeName, settingsIcon, changeProfileImage, changeAbout;
    CircleImageView profile_image;
    FirebaseUser fuser;
    DatabaseReference reference;
    FirebaseFirestore rootRef;
    //---For ProfileImage
    private static final int GALLERY_PICK = 1; //For request.
    // private ColorGenerator generator = ColorGenerator.MATERIAL;
    //Create a storage reference.
    private StorageReference mImageStorage;
    public HomeFragment()
    {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Initialize controllers
        username2 = view.findViewById(R.id.username2);
        username = view.findViewById(R.id.username);
        user_email = view.findViewById(R.id.user_email);
        profile_image = view.findViewById(R.id.profile_image);
        changeName = view.findViewById(R.id.change_name);
        settingsIcon = view.findViewById(R.id.settings_icon);
        changeProfileImage = view.findViewById(R.id.change_profile_image);
        changeAbout = view.findViewById(R.id.change_about);
        aboutText = view.findViewById(R.id.description_about);


        //-----OFFLINE CAPABILITIES-----//
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        rootRef = FirebaseFirestore.getInstance();
        rootRef.setFirestoreSettings(settings);
        //------------------------------//

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        assert fuser != null;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        //Offline capabilities.
        //Keep the data synced as String or integer but not as integer !
        //Store data locally in my device each time i run the app activity without empty image..etc
        reference.keepSynced(true);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        if(fuser == null)
        {
            startActivity(new Intent(getContext(), RegisterActivity.class));
        }
        //----GO TO SETTINGS
        settingsIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent settings_intent = new Intent(Objects.requireNonNull(getActivity()).getBaseContext(), SettingsActivity.class);
                settings_intent.putExtra("user_id_msg", fuser.getUid());
                startActivity(settings_intent);

            }
        });

        //Create method for display user info.
        ReadUserInfo();

        //---CHANGE NAME---//
        changeName.setOnClickListener(new View.OnClickListener()
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
        changeAbout.setOnClickListener(new View.OnClickListener()
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
        changeProfileImage.setOnClickListener(new View.OnClickListener()
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
                final StorageReference filepath = mImageStorage.child("profile_images").child(fuser.getUid() + "jpg");
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

                                    rootRef.collection("Users")
                                           .document(fuser.getUid())
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
                    Log.d(TAG, "CROP_IMAGE ERROR" + error);
                }
            }
        }
    }

    private void ReadUserInfo()
    {
        rootRef.collection("Users")
               .document(fuser.getUid())
               .get()
               .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot)
                    {
                        final User user1 = documentSnapshot.toObject(User.class);
                        if(user1 != null)
                        {
                            username.setText(user1.getUsername());
                            username2.setText(user1.getUsername());
                            aboutText.setText(user1.getAbout());
                            String email_user = fuser.getEmail();
                            user_email.setText(email_user);

                            if(user1.getImageURL().equals("default"))
                            {
                                String letter = String.valueOf(user1.getUsername().charAt(0));
                                int color = 0;
                                //int color = generator.getRandomColor();
                                TextDrawable drawable = TextDrawable.builder().buildRound(letter, color); // radius in px
                                profile_image.setBackground(drawable);
                            }
                            else
                            {
                                profile_image.setBackground(null);
                                Picasso.get().load(user1.getImageURL())
                                        //I want to load the image offline.
                                        //This line of code retrieve the image offline.
                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                        //Callback for checking if the task is successful.
                                        .into(profile_image, new Callback()
                                        {
                                            @Override
                                            public void onSuccess()
                                            {
                                                //If the task isSuccessful --> Any things.
                                            }
                                            @Override
                                            public void onError(Exception e)
                                            {
                                                //if the task is not successful load the image online.
                                                Picasso.get().load(user1.getImageURL()).into(profile_image);
                                            }
                                        });
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

        rootRef.collection("Users")
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
        rootRef.collection("Users")
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
            rootRef.collection("Users")
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
