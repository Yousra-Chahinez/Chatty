package com.example.azzem.chatty.Fragments;

import android.content.Intent;
import android.os.Bundle;
import com.example.azzem.chatty.Adapter.UserAdapter;
import com.example.azzem.chatty.Adapter.UserAdapterHoriz;
import com.example.azzem.chatty.Model.Chat;
import com.example.azzem.chatty.Model.User;
import com.example.azzem.chatty.UsersActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;

import com.example.azzem.chatty.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class ChatsFragment extends Fragment
{
    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView, recyclerViewHoriz;
    private List<Chat> mChats;
    private List<User> mUsers;
    private FloatingActionButton addUser;

    private UserAdapter userAdapter;
    private UserAdapterHoriz userAdapterHoriz;

    FirebaseUser firebaseUser;
    FirebaseFirestore rootRef;
    CollectionReference chatRef, userRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_chats, container, false);

        //initViews
        swipeRefreshLayout = view.findViewById(R.id.refresh_recyclerView);
        addUser = view.findViewById(R.id.add_user);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerViewHoriz = view.findViewById(R.id.recycler_view_horizontal);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //----Vertical recycler view-----------//
        mChats = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        //-----------------------------------------//

        //---Horizontal recycler view.
        mUsers = new ArrayList<>();
        LinearLayoutManager horizontalLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL
        , false);

        recyclerViewHoriz.setLayoutManager(horizontalLinearLayoutManager);

        userAdapterHoriz = new UserAdapterHoriz(getContext(), mUsers);
        recyclerViewHoriz.setAdapter(userAdapterHoriz);

        //-----------------------------------------//

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        chatRef = FirebaseFirestore.getInstance().collection("Chats");
        userRef = FirebaseFirestore.getInstance().collection("Users");

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), UsersActivity.class));
            }
        });


        userAdapter = new UserAdapter(getContext(), mChats);
        recyclerView.setAdapter(userAdapter);
        readReceivers();

        readUsers();

        return view;
    }

    private void readReceivers()
    {
        chatRef.whereArrayContains("participants", firebaseUser.getUid())
               .addSnapshotListener(new EventListener<QuerySnapshot>()
               {
                   @Override
                   public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
                   {
                       mChats.clear();
                       for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                       {
                           Chat chat = documentSnapshot.toObject(Chat.class);
                           chat.setDocumentid(documentSnapshot.getId());
                           mChats.add(chat);
                           //sortArrayList();
                           userAdapter.notifyDataSetChanged();
                       }
                   }
               });
    }

    private void readUsers()
    {
        userRef.addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
            {

                mUsers.clear();
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    User user = documentSnapshot.toObject(User.class);

                    if (!firebaseUser.getUid().equals(user.getId()))
                    {
                        mUsers.add(user);
                    }

                    userAdapterHoriz.notifyDataSetChanged();
                }
            }
        });
    }

}