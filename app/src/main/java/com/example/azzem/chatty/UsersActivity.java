package com.example.azzem.chatty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.azzem.chatty.Adapter.ChatsAdapter;
import com.example.azzem.chatty.Model.Chat;
import com.example.azzem.chatty.Model.Messages;
import com.example.azzem.chatty.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersActivity extends AppCompatActivity
{
    private RecyclerView recyclerViewUsers;
    private ChatsAdapter userAdapter;
    private List<User> mUsers;
    private EditText text_search;
    private Intent message_intent;
    private String receiverId, currentTime;

    FirebaseUser firebaseUser;
    FirebaseFirestore userRef, roomRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        //Firestore
        userRef = FirebaseFirestore.getInstance();
        initViews();

        recyclerViewUsers.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UsersActivity.this);
        recyclerViewUsers.setLayoutManager(linearLayoutManager);

        userAdapter = new ChatsAdapter(UsersActivity.this, mUsers);
        recyclerViewUsers.setAdapter(userAdapter);

        //Current user.
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Receiver id.
        SharedPreferences preferences = UsersActivity.this.getSharedPreferences("ReceiverId", Context.MODE_PRIVATE);
        receiverId = preferences.getString("userid", "");
        System.out.println("receiverId 9bel "+receiverId);


        //Firestore.
        roomRef = FirebaseFirestore.getInstance();


        //Fill recyclerView.
        readUsers();

        text_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                searchUsers(charSequence.toString().toLowerCase());
            }
            @Override
            public void afterTextChanged(Editable editable)
            {
            }
        });

}

    private void initViews()
    {
        mUsers = new ArrayList<>();
        recyclerViewUsers = findViewById(R.id.recycler_view_users);
        text_search = findViewById(R.id.search_editText);
    }

    private void readUsers()
    {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef.collection("Users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mUsers.clear();
                        if (text_search.getText().toString().equals(""))
                        {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                            {
                                User user1 = documentSnapshot.toObject(User.class);

                                assert firebaseUser != null;

                                //Add users to RecyclerView excluding the current user
                                if (user1.getId() != null && !user1.getId().equals(firebaseUser.getUid()))
                                {
                                    //Add users to RecyclerView excluding the current user
                                    mUsers.add(user1);
                                    sortArrayList();
                                }
                            }
                            userAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    //Order by name.
    private void sortArrayList()
    {
        Collections.sort(mUsers, new Comparator<User>() {
            @Override
            public int compare(User user, User t1) {
                //Comparing 2 items in arrayList and displaying them
                return user.getUsername().compareTo(t1.getUsername());
            }
        });
    }

    private void searchUsers(String s)
    {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = userRef.collection("Users")
                .orderBy("search")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                    {
                        mUsers.clear();
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                        {
                            User user1 = documentSnapshot.toObject(User.class);
                            if (firebaseUser != null)
                            {
                                if(!user1.getId().equals(firebaseUser.getUid()))
                                {
                                    mUsers.add(user1);
                                }
                            }
                        }
                        userAdapter = new ChatsAdapter(UsersActivity.this, mUsers);
                        recyclerViewUsers.setAdapter(userAdapter);
                    }
                });

    }
}
