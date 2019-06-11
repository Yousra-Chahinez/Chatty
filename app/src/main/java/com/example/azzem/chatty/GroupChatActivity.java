package com.example.azzem.chatty;


import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.azzem.chatty.Adapter.GroupMessageAdapter;
import com.example.azzem.chatty.Model.GroupsFireStore;
import com.example.azzem.chatty.Model.MessageG;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.firestore.ServerTimestamp;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity {
    private FloatingActionButton btn_send_message;
    private CircleImageView groupImage;
    private TextView group_name, displayUsername;
    private EditText text_send;
    private Toolbar toolbar;
    private String CurrentGroupName, CurrentUserID, CurrentUserName, currentDate, currentTime;
    private final String TAG = "GroupChatActivity";
    private GroupMessageAdapter groupMessageAdapter;
    private List<MessageG> mMessageList;
    private RecyclerView messages_recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private Date currentTime2 = Calendar.getInstance().getTime();
    FirebaseUser fuser;
    CollectionReference messageRef;
    DocumentReference infoGroupRef;
    @ServerTimestamp
    Date date;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        //Initialize controllers
        initViews();
        mMessageList = new ArrayList<>();
        messages_recyclerView = findViewById(R.id.recycler_view_messages_group);
        messages_recyclerView.setHasFixedSize(true);
        groupMessageAdapter = new GroupMessageAdapter(mMessageList, GroupChatActivity.this);
        mLinearLayoutManager = new LinearLayoutManager(GroupChatActivity.this);
        messages_recyclerView.setLayoutManager(mLinearLayoutManager);
        messages_recyclerView.setAdapter(groupMessageAdapter);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for -->

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        assert fuser != null;
        CurrentUserID = fuser.getUid();

        Intent intent = getIntent();
        final String documentId = intent.getStringExtra("documentId");
        String groupName = intent.getStringExtra("groupName");
        String image_group = intent.getStringExtra("groupImage");
        System.out.println("groupImage3 " + image_group);
        System.out.println("document id " + documentId);

        group_name.setText(groupName);
        if (groupImage != null)
        {
            Picasso.get().load(image_group).into(groupImage);
            System.out.println("groupImage2 " + image_group);
        }

        messageRef = FirebaseFirestore.getInstance().collection("/groups" + "/" + documentId + "/"
                + "messages");

        infoGroupRef = FirebaseFirestore.getInstance().collection("/groups").document(documentId);

        infoGroupRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot != null) {
                                if (documentSnapshot.exists()) {
                                    GroupsFireStore groupsFireStore = task.getResult().toObject(GroupsFireStore.class);
                                    Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData().get("imageUrl"));
                                    System.out.println("ndjareb !!! " + documentSnapshot.getData().get("imageUrl"));
                                    if (groupsFireStore != null) {
                                        if (!groupsFireStore.getImageUrl().equals("default")) {
                                            Picasso.get().load(groupsFireStore.getImageUrl()).into(groupImage);
                                        } else {
                                            groupImage.setImageResource(R.drawable.image);
                                        }
                                        System.out.println("nom ta3hom " + groupsFireStore.getParticipants_names());
                                    }
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


        //--------------------------LISTENER TO SEND MESSAGE BUTTON-------------------------------//
        btn_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                sendMessageIntoDatabase();
            }
        });
        DisplayMessages();
        //----------------------------------------------------------------------------------------//
    }

    private void initViews() {
        btn_send_message = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.message_send);
        group_name = findViewById(R.id.group_name_textView);
        displayUsername = findViewById(R.id.display_username);
        groupImage = findViewById(R.id.groupImage);
        toolbar = findViewById(R.id.toolbar);
    }

    private void sendMessageIntoDatabase()
    {
        final String message = text_send.getText().toString();
        if (message.equals(""))
        {
            Toast.makeText(GroupChatActivity.this, "Empty !", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //---get time.
            Calendar calForTime = Calendar.getInstance();
            //And for format pm, am...--> hh, time --> mm and ss for seconds if u want
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm");
            currentTime = currentTimeFormat.format(calForTime.getTime());
            //---------------------
            MessageG messageG = new MessageG(message, fuser.getUid(), "text");
            messageRef.add(messageG)
                      .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "SUCCESS ADDING MESSAGE");
                            Toast.makeText(GroupChatActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "FAILED ADDING MESSAGE");
                        }
                    });
            text_send.setText("");
        }
    }

    private void DisplayMessages()
    {
        messageRef.orderBy("date", Query.Direction.ASCENDING)
                  .addSnapshotListener(this, new EventListener<QuerySnapshot>()
                {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e)
                    {
                        if(e!=null)
                        {
                            return;
                        }
                        for(DocumentChange dc : queryDocumentSnapshots.getDocumentChanges())
                        {
                            if (dc.getType() == DocumentChange.Type.ADDED)
                            {
                                //DocumentSnapshot documentSnapshot = dc.getDocument();

                                MessageG messageG = dc.getDocument().toObject(MessageG.class);
                                mMessageList.add(messageG);
                                groupMessageAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }
}



