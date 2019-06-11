package com.example.azzem.chatty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.example.azzem.chatty.Model.Chat;
import com.example.azzem.chatty.Model.Messages;
import com.example.azzem.chatty.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private CircleImageView profileImage;
    private TextView username;
    private Button sendMsgBtn;
    private Intent intent;
    private Date currentTime2 = Calendar.getInstance().getTime();


    private String userid, name, currentTime, imageURL;

    FirebaseUser firebaseUser;
    FirebaseFirestore userRef, chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initView();

        //From ChatAdapter.
        intent = getIntent();
        userid = intent.getStringExtra("userid");
        name = intent.getStringExtra("name");
        imageURL = intent.getStringExtra("imageURL");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseFirestore.getInstance();
        chatRef = FirebaseFirestore.getInstance();

        userRef.collection("Users")
                .document(userid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user1 = documentSnapshot.toObject(User.class);
                        if (user1 != null) {
                            username.setText(user1.getUsername());

                            if (user1.getImageURL().equals("default"))
                            {
                                String letter = String.valueOf(name.charAt(0));
                                int color = 0;
                                TextDrawable drawable1 = TextDrawable.builder().buildRound(letter, color);
                                profileImage.setBackground(drawable1);
                            }

                            else
                                {
                                Picasso.get().load(user1.getImageURL()).into(profileImage);
                                }
                        }
                    }
                });

        sendMsgBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CreateChatRoom();
            }
        });
    }

    private void initView()
    {
        profileImage = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        sendMsgBtn = findViewById(R.id.SendMessageButton);
    }

    private void CreateChatRoom()
    {
        List<String> listOfParticipants = new ArrayList<>();
        listOfParticipants.add(firebaseUser.getUid());
        listOfParticipants.add(userid);
        System.out.println("id ta3 receiver "+userid);

        final Chat chat = new Chat(listOfParticipants, name, imageURL, userid);

        chatRef.collection("Chats")
                .add(chat)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>()
                {
                    @Override
                    public void onSuccess(final DocumentReference documentReference)
                    {
                        System.out.println("document id here "+ documentReference.getId());

                        Log.d("ProfileActivity", "onSuccess: SUCCESS ADDING CHAT PARTICIPANTS");

                        chat.setDocumentid(documentReference.getId());

                        //---Get time.
                        Calendar calForTime = Calendar.getInstance();
                        //And for format pm, am...--> hh, time --> mm and ss for seconds if u want
                        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm");
                        currentTime = currentTimeFormat.format(calForTime.getTime());
                        //---------------------

                        Messages messages = new Messages("Hi !", false, currentTime, "text", firebaseUser.getUid());

                        documentReference.collection("Messages")
                                .document()
                                .set(messages)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Log.d("ProfileActivity", "SUCCESS ADDING CHAT MESSAGE");
                                        }
                                        else
                                        {
                                            Log.d("ProfileActivity", "FAILED ADDING CHAT MESSAGE");
                                        }
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d("ProfileActivity", "onFailure: FAILED ADDING CHAT PARTICIPANTS");
            }
        });
    }
}