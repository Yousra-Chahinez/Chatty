package com.example.azzem.chatty;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.azzem.chatty.Adapter.MessagesAdapter;
import com.example.azzem.chatty.Model.Messages;
import com.example.azzem.chatty.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity
{

    private CircleImageView profile_image, receiver_image;
    private TextView username, receiver_name;
    private FloatingActionButton btn_send;
    private ImageButton btn_add;
    private EditText text_send;
    private String documentId, receiverId,receiverName, currentTime, currentUserId;
    private Intent message_intent;
    private RecyclerView mMessagesList;
    private Toolbar toolbar;
    private MessagesAdapter messagesAdapter;
    private LinearLayoutManager mLinearLayout;
    private List<Messages> messagesList = new ArrayList<>();
    private Date currentTime2 = Calendar.getInstance().getTime();
    private static final int GALLERY_PICK = 1;
    private ProgressBar progressBar;

    FirebaseUser fuser;
    StorageReference mImageStorage;
    FirebaseFirestore deleteHistoryRef;
    CollectionReference messageRef, receiverRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initViews();

        progressBar = findViewById(R.id.progressBar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

        //From UserAdapter.
        message_intent = getIntent();

        mImageStorage = FirebaseStorage.getInstance().getReference();
        receiverId = message_intent.getStringExtra("receiverId");
        receiverName = message_intent.getStringExtra("receiver_name");
        documentId = message_intent.getStringExtra("documentId");
        String receiverImage = message_intent.getStringExtra("receiverImage");
        receiverRef = FirebaseFirestore.getInstance().collection("Users");

        mLinearLayout = new LinearLayoutManager(MessageActivity.this);
        //Assign to our messageList
        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);
        messagesAdapter = new MessagesAdapter(messagesList, MessageActivity.this, receiverImage, documentId, receiverName);
        mMessagesList.setAdapter(messagesAdapter);

        messageRef = FirebaseFirestore.getInstance().collection("/Chats" + "/" + documentId + "/" +
                 "Messages");

        deleteHistoryRef = FirebaseFirestore.getInstance();

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = fuser.getUid();

        receiverRef.document(receiverId)
                   .get()
                   .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user1 = documentSnapshot.toObject(User.class);
                        username.setText(user1.getUsername());

                        receiver_name.setText(user1.getUsername());

                        if (user1.getImageURL().equals("default"))
                        {
                            String letter = String.valueOf(user1.getUsername().charAt(0));
                            int color = R.color.colorPrimaryDark;
                            TextDrawable drawable1 = TextDrawable.builder().buildRound(letter, color);
                            profile_image.setBackground(drawable1);
                            receiver_image.setBackground(drawable1);
                        }
                        else
                        {
                            Picasso.get().load(user1.getImageURL()).into(profile_image);
                            Picasso.get().load(user1.getImageURL()).into(receiver_image);
                        }
                    }
                });

        btn_send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String message = text_send.getText().toString();
                System.out.println("message without to String "+text_send.getText());
                System.out.println("message with to String "+text_send.getText().toString());

                if(TextUtils.isEmpty(message))
                {
                    Toast.makeText(MessageActivity.this, "Empty !", Toast.LENGTH_SHORT)
                            .show();
                }
                else
                {
                    SendMessage(message);
                }
            }
        });

        loadMessages();

        //Open file chooser
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "Select image"), GALLERY_PICK);//?
            }
        });
    }

    private void initViews()
    {
        toolbar = findViewById(R.id.toolbar);
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        mMessagesList = findViewById(R.id.Recycle_view);
        receiver_name = findViewById(R.id.receiverName);
        receiver_image = findViewById(R.id.receiverImage);
        btn_add = findViewById(R.id.btn_add);
    }

    private void SendMessage(final String message)
    {
        //---Get time
        Calendar calForTime = Calendar.getInstance();
        //And for format pm, am...--> hh, time --> mm and ss for seconds if u want
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm");
        currentTime = currentTimeFormat.format(calForTime.getTime());
        //---------------------

        Messages messages = new Messages(message, false, currentTime, "text", currentUserId);

        messageRef.add(messages)
                  .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("MessageActivity", "SUCCESS ADDING MESSAGE");
                            Toast.makeText(MessageActivity.this, "SUCCESS ADDING MESSAGE TEXT " +
                                    message, Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Log.d("MESSAGE ACTIVITY", "FAILED ADDING MESSAGE");
                    Toast.makeText(MessageActivity.this,"FAILED ADDING" +
                        "MESSAGE "+ message, Toast.LENGTH_LONG).show();
            }
        });
        text_send.setText("");
    }

    //----------------------------------SEND IMAGE MESSAGE----------------------------------------//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ///To identify the request i use GALLERY_PICK
        //If the user is actually pick an image
        //Now get the time
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK)
        {
            //I get the image uri from the user has selected
            assert data != null;
            Uri image_uri = data.getData();

            CropImage.activity(image_uri)
                    //maintain a square pixel image.
                    //crop the image in square pixels.
                    .setAspectRatio(1, 1)
                    .start(MessageActivity.this);
        }

        //If the request code pass to the CropActivity.
        //Make sure that the result which are getting is from the CropActivity they have created...
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                //Give the uri of the data.
                //Uri of the CroppedImage. !

                Uri resultUri = result.getUri();

                //Now store it this Uri in Firebase Storage.
                //The last child is our file.
                //In the last child i want to store the name of the image.
                //To store a really good name store the id of the user every time the user store an image !

                //---PUSHING IMAGE INTO STORAGE---
                final StorageReference filepath = mImageStorage.child("message_images").child(documentId + ".jpg");

                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String download_url = uri.toString();
                                Log.d("MessageActivity", "onSuccess: uri= " + uri.toString());
                                Toast.makeText(getApplicationContext(), "Your picture Saved" +
                                        " successfully", Toast.LENGTH_SHORT).show();

                                //---Get time
                                Calendar calForTime = Calendar.getInstance();
                                //And for format pm, am...--> hh, time --> mm and ss for seconds if u want
                                SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm");
                                currentTime = currentTimeFormat.format(calForTime.getTime());
                                //---------------------

                                Messages messages = new Messages(download_url, false, currentTime, "image", currentUserId);

                                messageRef.add(messages)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d("MessageActivity", "SUCCESS ADDING IMAGE");

                                                Toast.makeText(MessageActivity.this, "SUCCESS ADDING IMAGE"
                                                        , Toast.LENGTH_LONG).show();
                                            }
                                        })

                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("MessageActivity", "FAILED ADDING MESSAGE");
                                                Toast.makeText(MessageActivity.this, "FAILED ADDING" +
                                                        "IMAGE " + download_url, Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        });
                    }
                });
            } else {
                Toast.makeText(MessageActivity.this, "Error in Uploading.", Toast.LENGTH_SHORT).show();
            }
        }
    }
//    //--------------------------------------------------------------------------------------------//

    private void loadMessages()
    {
        messageRef.orderBy("date", Query.Direction.ASCENDING)
                  .addSnapshotListener(this, new EventListener<QuerySnapshot>()
                  {
                      @Override
                      public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                          @javax.annotation.Nullable FirebaseFirestoreException e)
                      {
                          if(e!=null)
                          {
                              return;
                          }
                          for(DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges())
                          {
                              if(documentChange.getType() == DocumentChange.Type.ADDED)
                              {
                                  //DocumentSnapshot documentSnapshot = documentChange.getDocument();
                                  Messages messages = documentChange.getDocument().toObject(Messages.class);
                                  messages.setDocumentId(documentChange.getDocument().getId());

                                  messagesList.add(messages);
                                  messagesAdapter.notifyDataSetChanged();
                                  mMessagesList.scrollToPosition(messagesAdapter.getItemCount() - 1);
                              }
                          }
                      }
                  });
    }

    //Delete Chat History.


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.delete_history:
                AlertDialog.Builder delete_history_dialog = new AlertDialog.Builder(MessageActivity.this);

                delete_history_dialog.setTitle("Clear Chat History");
                delete_history_dialog.setMessage("Are you sure you want to delete the" + "\n" + "chat history ?");

//                TextView msgTxt = (TextView) delete_history_dialog.setMessage("Are you sure you want to delete the" + "\n"
//                        + "chat history ?").findViewById(android.R.id.message);

                //msgTxt.setTypeface(Typeface.createFromAsset(getAssets(), "font/quicksandregular"));

                delete_history_dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        deleteHistoryRef.collection("Chats")
                                        .document(documentId)
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if(task.isSuccessful())
                                                {
                                                    System.out.println("DELETING");
                                                }
                                                else
                                                {
                                                    System.out.println("NOT DELETING");
                                                }
                                            }
                                        });
                    }
                });

                delete_history_dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

                delete_history_dialog.create();
                delete_history_dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}