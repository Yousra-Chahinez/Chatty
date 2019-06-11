package com.example.azzem.chatty.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.azzem.chatty.Adapter.UserAdapter;
import com.example.azzem.chatty.Model.Chats;
import com.example.azzem.chatty.Model.User;
import com.example.azzem.chatty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PeopleFragment extends Fragment
{
    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<User> mUsers;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    private List<Chats> usersList;
    private String receiver;
    public PeopleFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_people, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_people);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        SharedPreferences preferences = inflater.getContext().getSharedPreferences("Receiver_id", Context.MODE_PRIVATE);
        receiver = preferences.getString("userid", "");

        reference = FirebaseDatabase.getInstance().getReference("Chats").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
//                usersList.clear();
//                for(DataSnapshot snapshot : dataSnapshot.getChildren())
//                {
//                    Chats chats = snapshot.getValue(Chats.class);
//                    System.out.println("wech fiha chat.getValue "+ snapshot.getValue());
//                    usersList.add(chats);
//                }
//                ChatList();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });
        return view;
    }

    private void ChatList()
    {
//        mUsers = new ArrayList<>();
//        reference = FirebaseDatabase.getInstance().getReference("Users");
//        reference.addValueEventListener(new ValueEventListener()
//        {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
//            {
//                mUsers.clear();
//                for(DataSnapshot snapshot : dataSnapshot.getChildren())
//                {
//                    User user = snapshot.getValue(User.class);
//                    for(Chats chats : usersList)
//                    {
//                        if (user != null && user.getId().equals(chats.getId()))
//                        {
//                            mUsers.add(user);
//                        }
//                    }
//                }
//                userAdapter = new UserAdapter(getContext(), mUsers, true);
//                recyclerView.setAdapter(userAdapter);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError)
//            {
//            }
//        });
    }
}
