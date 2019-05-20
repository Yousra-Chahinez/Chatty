package com.example.azzem.chatty.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.example.azzem.chatty.Adapter.ChatsAdapter;
import com.example.azzem.chatty.Model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.example.azzem.chatty.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class ChatsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ChatsAdapter userAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    int number = 0;
    /*la liste de Users -->  utiliser pour remplir notre RecyclerView*/
    private List<User> mUsers;
    private EditText search_users;
    FirebaseUser firebaseUser;
    DatabaseReference refernce2;
    private SearchView searchView;
    private EditText text_search;
    private FloatingActionButton btn_search;
    FirebaseFirestore rootRef;
    DocumentReference usersRef;
//    private SearchView searchView = null;
//    private SearchView.OnQueryTextListener queryTextListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_chats, container, false);
        Toolbar mToolbar = view.findViewById(R.id.toolbar3);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setTitle("Chatty");
        //setHasOptionsMenu(true);

        rootRef = FirebaseFirestore.getInstance();

        //-----------------------------------SWIPE REFRESH----------------------------------------//
        /*swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                number++;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 4000);
            }
        });*/
        //----------------------------------------------------------------------------------------//

        //Init ArrayList
        mUsers = new ArrayList<>();
        /* Initialize Controllers */
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        /* LinearLayoutManager est utilisé pour afficher les éléments en défilement horizontal et vertical.*/
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        userAdapter = new ChatsAdapter(getContext(), mUsers);
        recyclerView.setAdapter(userAdapter);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //Remplir recyclerView
        readUsers();

        text_search = view.findViewById(R.id.search_editText);
        btn_search = view.findViewById(R.id.btn_search);

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

        //updateToken(FirebaseInstanceId.getInstance().getToken());

        btn_search.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                colseKeyboard(view);
            }
        });

        return view;
    }

    private void searchUsers(String s)
    {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = rootRef.collection("Users")
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
                     userAdapter = new ChatsAdapter(getContext(), mUsers);
                     recyclerView.setAdapter(userAdapter);
                 }
             });

    }

    private void readUsers()
    {
                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                rootRef.collection("Users")
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

    //In order
    private void sortArrayList()
    {
        Collections.sort(mUsers, new Comparator<User>()
        {
            @Override
            public int compare(User user, User t1)
            {
                //Comparing 2 items in arrayList and displaying them
                return user.getUsername().compareTo(t1.getUsername());
            }
        });
        //userAdapter.notifyDataSetChanged();
//
//        String lastHeader = "";
//        int size = mUsers.size();
//
//        for(int i=0; i<size; i++)
//        {
//            User user = mUsers.get(i);
//            String header = String.valueOf(user.getUsername().charAt(0)).toUpperCase();
//            if(!TextUtils.equals(lastHeader, header))
//            {
//                lastHeader = header;
//                mUsers.a
//            }

            //DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//            reference.addValueEventListener(new ValueEventListener()
//            {
//                @Override
//                public void onD
// ataChange(@NonNull DataSnapshot dataSnapshot)
//                {
//                    for(DataSnapshot snapshot : dataSnapshot.getChildren())
//                    {
//                        String header = String.valueOf(user.)
//                    }
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError)
//                {
//                }
//            });
        }
//    //---SEND NOTIFICATION
//    private void updateToken(String token)
//    {
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
//        Token token1 = new Token(token);
//        databaseReference.child(firebaseUser.getUid()).setValue(token1);
//    }
    //------------------

    private void colseKeyboard(View v)
    {
        //When the keyboard is open means --> the view has a focus.
        //Save the view that has focus in view.
        //Save the view which has currently the focus in this activity layout.
        v = getActivity().getCurrentFocus();
        if(v != null)
        {
            InputMethodManager methodManager = (InputMethodManager)(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
            methodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

//    private void status(String status)
//    {
//        refernce2 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("status", status);
//        //Save data
//        refernce2.updateChildren(hashMap);
//    }
//
//    @Override
//    public void onResume()
//    {
//        super.onResume();
//        status("online");
//        //Yousra check vedio again in logout
//    }
//
//    @Override
//    public void onPause()
//    {
//        super.onPause();
//        status("offline");
//    }
}
