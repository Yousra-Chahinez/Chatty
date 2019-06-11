package com.example.azzem.chatty;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.azzem.chatty.Adapter.MessagesAdapter;
import com.example.azzem.chatty.Model.Messages;
import com.example.azzem.chatty.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private EditText text_send;
    private String receiverName, receiverId, currentTime, currentUserId;
    private Intent message_intent;
    private RecyclerView mMessagesList;
    private Toolbar toolbar;
    private MessagesAdapter messagesAdapter;
    private LinearLayoutManager mLinearLayout;
    private List<Messages> messagesList = new ArrayList<>();
    private Date currentTime2 = Calendar.getInstance().getTime();


    FirebaseUser fuser;
    StorageReference mImageStorage;
    CollectionReference messageRef, receiverRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initViews();

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

        mImageStorage = FirebaseStorage.getInstance().getReference();
        receiverRef = FirebaseFirestore.getInstance().collection("Users");

        //From UserAdapter.
        message_intent = getIntent();
        receiverId = message_intent.getStringExtra("receiverId");
        String documentId = message_intent.getStringExtra("documentId");
        System.out.println("conversationIdMessage "+documentId);
        String receiverImage = message_intent.getStringExtra("receiverImage");


        mLinearLayout = new LinearLayoutManager(MessageActivity.this);
        //Assign to our messageList
        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);
        //mLinearLayout.setStackFromEnd(true);
        messagesAdapter = new MessagesAdapter(messagesList, MessageActivity.this, receiverImage);
        mMessagesList.setAdapter(messagesAdapter);

        messageRef = FirebaseFirestore.getInstance().collection("/Chats" + "/" + documentId + "/" +
                     "Messages");

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
                                  messagesList.add(messages);
                                  messagesAdapter.notifyDataSetChanged();
                              }
                          }
                      }
                  });
    }

}