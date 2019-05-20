package com.example.azzem.chatty;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.azzem.chatty.Adapter.MessagesAdapter;
import com.example.azzem.chatty.Model.Messages;
import com.example.azzem.chatty.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity
{

    CircleImageView profile_image;
    ImageView emodjiImageView;
    TextView username, mLastSeenView;
    ImageButton btn_add;
    FloatingActionButton btn_send;
    EditText text_send;
    FirebaseUser fuser;
    DatabaseReference reference, refernce2, mRootRef, reference3;
    FirebaseAuth mAuth;
    private String mCurrentUserId;
    private final String TAG = "MessageActivity";
    private String userid, name;
    private String currentTime;
    Intent intent, message_intent2;
    RecyclerView mMessagesList;
    MessagesAdapter mAdapter;
    private static final int GALLERY_PICK=1;
    private final List<Messages> messagesList = new ArrayList<>();
    private ColorGenerator generator = ColorGenerator.MATERIAL;
    private LinearLayoutManager mLinearLayout;
    private DatabaseReference mMessageDatabase;
    StorageReference mImageStorage;
    FirebaseFirestore rootRef;
    //Notification
    //APIService apiService;
    boolean notify = false;

    //for seen feature.
    ValueEventListener seenListener;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });
        //Notification
        //apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        mLastSeenView = findViewById(R.id.last_seen); //--> ATT CRASH !!!
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        btn_add = findViewById(R.id.btn_add);
        emodjiImageView = findViewById(R.id.emodji);
        //emoji
//        EmojIconActions emojIcon = new EmojIconActions(this,rootView,emojiconEditText,emojiImageView);
//        emojIcon.ShowEmojIcon();


        mImageStorage=  FirebaseStorage.getInstance().getReference();
        mRootRef = FirebaseDatabase.getInstance().getReference();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build();
        rootRef = FirebaseFirestore.getInstance();
        rootRef.setFirestoreSettings(settings);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
//---------------
        intent = getIntent();
        userid = intent.getStringExtra("userid");
        name = intent.getStringExtra("name");
//----------------

        //-------ADD OPTIONS MENU TO MESSAGE ACTIVITY

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        reference3 = FirebaseDatabase.getInstance().getReference("messages")
                .child(fuser.getUid())
                .child(userid);

        rootRef.collection("Users")
               .document(userid)
               .get()
               .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
               {
                   @Override
                   public void onSuccess(DocumentSnapshot documentSnapshot)
                   {
                       User user1 = documentSnapshot.toObject(User.class);
                       username.setText(user1.getUsername());

                       //readMessages(fuser.getUid(), userid, user.getImageURL());
                       mAdapter = new MessagesAdapter(messagesList, getApplicationContext(), user1.getImageURL());
                       mMessagesList = findViewById(R.id.Recycle_view);
                       mLinearLayout = new LinearLayoutManager(getApplicationContext());
                       //Assign to our messageList
                       mMessagesList.setHasFixedSize(true);
                       mMessagesList.setLayoutManager(mLinearLayout);
                       //mLinearLayout.setStackFromEnd(true);
                       mMessagesList.setAdapter(mAdapter);

                       if(user1.getImageURL().equals("default"))
                       {
                           String letter = String.valueOf(name.charAt(0));
                           int color = 0;
                           TextDrawable drawable1 = TextDrawable.builder().buildRound(letter, color);
                           profile_image.setBackground(drawable1);
                       }
                       else
                       {
                           Picasso.get().load(user1.getImageURL()).into(profile_image);
                       }
                   }
               });

        //LoadData
        /*mRootRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.hasChild(userid))
                {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", "false");
                    chatAddMap.put("time", ServerValue.TIMESTAMP);
                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + mCurrentUserId + "/" + userid, chatAddMap);
                    chatUserMap.put("Chat/" + userid + "/" + mCurrentUserId, chatAddMap);
                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener()
                    {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference)
                        {
                            if(databaseError == null)
                            {
                                Toast.makeText(getApplicationContext(), "Successfully Added chats feature", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Cannot Add chats feature", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });*/

        btn_send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                notify = true;
                 //sendMessage(fuser.getUid(), userid, msg);
                sendMessage();
            }
        });
        loadMessages();

        //Open file chooser
        btn_add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"Select image"), GALLERY_PICK);//?
            }
        });



    }

    //-----------------------------------DISPLAY MESSAGES-----------------------------------------//
    private void loadMessages()
    {
        //retrieve data
        mRootRef.child("messages").child(mCurrentUserId).child(userid).addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                //When we get the messages we will receive it like datasnapshot
                Messages messages = dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
            {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });
    }
    //--------------------------------------------------------------------------------------------//
    private void seenMessage(final String userid)
    {
        seenListener = reference3.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Messages messages = snapshot.getValue(Messages.class);
                    if (messages != null)
                    {
                        if(!messages.getFrom().equals(fuser.getUid()) && messages.getFrom().equals(userid))
                        {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("seen", true);
                            snapshot.getRef().updateChildren(hashMap);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });
    }
    //---------------------------SEND MESSAGES INTO FIREBASE DATABASE-----------------------------//
    private void sendMessage() {
        String msg = text_send.getText().toString();
        if (!TextUtils.isEmpty(msg)) {
            String current_user_ref = "messages/" + mCurrentUserId + "/" + userid;
            //ref for the receiver
            String chat_user_ref = "messages/" + userid + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages").child(mCurrentUserId)
                    .child(userid).push();
            //Get this push id
            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();

            messageMap.put("message", msg);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            //Store the id of the user who is sending the message
            messageMap.put("from", mCurrentUserId);

            //Add push id for the message which we are sending
            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);
            //Store this data in RootRef
            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e("CHAT_ACTIVITY", "Cannot add message to database");
                    } else {
                        Toast.makeText(getApplicationContext(), "Message sent",
                                Toast.LENGTH_SHORT).show();
                        text_send.setText("");
                    }
                }
            });

            //Add User To ChatFragment.
            final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chats")
                    .child(fuser.getUid())
                    .child(userid);

            chatRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    System.out.println("dataSnapshot wech djib " + dataSnapshot.toString());
                    if (!dataSnapshot.exists()) {
                        chatRef.child("id").setValue(userid);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }
    }
        //----------------------------------------------------------------------------------------//

//    //------------------------------------SEND NOTIFICATION---------------------------------------//
//    private void sendNotification(String receiver, final String username, final String m)
//    {
//        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
//        Query query = tokens.orderByKey().equalTo(receiver);
//        query.addValueEventListener(new ValueEventListener()
//        {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
//            {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren())
//                {
//                    Token token = snapshot.getValue(Token.class);
//                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username +
//                            ": "+ m, "New message", userid);
//                    assert token != null;
//                    Sender sender = new Sender(data, token.getToken());
//
//                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>()
//                    {
//                        @Override
//                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response)
//                        {
//                            if(response.code() == 200){
//                                if(response.body().sucess == 1)
//                                {
//                                    Toast.makeText(getApplicationContext(), "failed!",
//                                            Toast.LENGTH_LONG).show();
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<MyResponse> call, Throwable t) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//    //--------------------------------------------------------------------------------------------//
    private String getFileExtension(Uri uri)
    {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    //----------------------------------SEND IMAGE MESSAGE----------------------------------------//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        ///To identify the request i use GALLERY_PICK
        //If the user is actually pick an image
        //Now get the time
        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK)
        {
            //I get the image uri from the user has selected
            assert data != null;
            Uri imageuri = data.getData();
            final String current_user_ref = "/messages" + "/" + mCurrentUserId + "/" + userid;
            final String chat_user_ref = "/messages" + "/" + userid + "/" + mCurrentUserId;
            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserId).child(userid).push();
            final String push_id = user_message_push.getKey();

            //---PUSHING IMAGE INTO STORAGE---
            final StorageReference filepath = mImageStorage.child("message_images").child(push_id + ".jpg");

            filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {

                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(Uri uri)
                        {
                            String download_url = uri.toString();
                            Log.d(TAG, "onSuccess: uri= "+ uri.toString());
                    Toast.makeText(getApplicationContext(),"Your picture Saved successfully",Toast.LENGTH_SHORT).show();

                        Map messageMap = new HashMap();
                        messageMap.put("message", download_url);
                        messageMap.put("seen", false);
                        messageMap.put("type", "image");
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("from", mCurrentUserId);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);
                        //text_send.setText("");
                        mRootRef.updateChildren(messageUserMap,
                                new DatabaseReference.CompletionListener()
                        {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference)
                            {
                                if(databaseError != null)
                                {
                                    Log.d("CHAT_LOG", databaseError.getMessage());
                                }
                            }
                        });
                        }
                    });
                }
            });
        }
    }
    //--------------------------------------------------------------------------------------------//

    //Delete History Of Messages.
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        //second parameter is the menu of onCreateOptionsMenu
        menuInflater.inflate(R.menu.option_menu_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.delete_history:
                final Dialog confirmationDialog = new Dialog(getApplicationContext());
                confirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                confirmationDialog.setCancelable(false);
                confirmationDialog.setContentView(R.layout.confirmation_dialog);

                TextView confirmationText = confirmationDialog.findViewById(R.id.confirmation_text);
                TextView descriptionText = confirmationDialog.findViewById(R.id.description_confirmation);
                Button positiveBtn = confirmationDialog.findViewById(R.id.btn_oui);
                Button negativeBtn = confirmationDialog.findViewById(R.id.btn_cancel);

                confirmationText.setText("Clear Chat History");
                descriptionText.setText("Are you sure you want to delete the" + "\n" + "chat history ?");
                positiveBtn.setText("Clear");
                negativeBtn.setText("Cancel");

                positiveBtn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                final DatabaseReference deleteHistoryRef = FirebaseDatabase.getInstance().getReference("messages")
                        .child(fuser.getUid()).child(userid);
                deleteHistoryRef.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren())
                        {
                            Messages messages = snapshot.getValue(Messages.class);
                            assert messages != null;
                            DatabaseReference deleteHistoryRef2 = deleteHistoryRef.child(Objects.requireNonNull(snapshot.getKey()));
                            deleteHistoryRef2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        confirmationDialog.dismiss();
                                        mAdapter.notifyDataSetChanged();
                                        Toast.makeText(MessageActivity.this, "Messages deleted", Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        confirmationDialog.dismiss();
                                        Toast.makeText(MessageActivity.this, "Error ici", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {
                    }
                });

        }
    });
                negativeBtn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        confirmationDialog.dismiss();
                    }
                });
                confirmationDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

//    private void status(String status)
//    {
//        refernce2 = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("status", status);
//        //Save data
//        refernce2.updateChildren(hashMap);
//    }
//
//    @Override
//    protected void onResume()
//    {
//        super.onResume();
//        status("online");
//    }
//
//    @Override
//    protected void onPause()
//    {
//        super.onPause();
//        //???
//        //reference3.removeEventListener(seenListener);
//        status("offline");
//    }
}
