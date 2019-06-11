package com.example.azzem.chatty.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.azzem.chatty.Adapter.GroupAdapter;
import com.example.azzem.chatty.Model.GroupsFireStore;
import com.example.azzem.chatty.R;
import com.example.azzem.chatty.SelectGroupActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class GroupsFragment extends Fragment {
    private RecyclerView group_list;
    private FloatingActionButton create_groups;
    private DatabaseReference groupNameRef, GroupRef;
    private List<GroupsFireStore> mGroups;
    private TextView roomNumber;

    private GroupAdapter groupAdapter;
    private int count = 0;

    private Intent intent_message_group;
    private String groupName;
    private FirebaseUser fuser;
    private Set<String> set1;
    private SharedPreferences myPref;
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore firebaseFirestore;
    CollectionReference groupRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View groupfragmenview = inflater.inflate(R.layout.fragment_groups, container, false);

        roomNumber = groupfragmenview.findViewById(R.id.nb_of_rooms);

        group_list = groupfragmenview.findViewById(R.id.recycler_view_groups);
        group_list.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        group_list.setLayoutManager(mLayoutManager);

        create_groups = groupfragmenview.findViewById(R.id.main_create_group_options);
        GroupRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        firebaseFirestore = FirebaseFirestore.getInstance();
        groupRef = firebaseFirestore.collection("groups");

        mGroups = new ArrayList<>();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        //intent_message_group = Objects.requireNonNull(getActivity()).getIntent();
        //groupName = intent_message_group.getStringExtra("nom_du_groupe");

        myPref = inflater.getContext().getSharedPreferences("idd", Context.MODE_PRIVATE);

        RetrievedAndDisplayGroups();

        //-----------------------------------CREATE GROUP-----------------------------------------//
        create_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //RequestNewGroup();
                Intent intent = new Intent(getContext(), SelectGroupActivity.class);
                startActivity(intent);
            }
        });
        //----------------------------------------------------------------------------------------//

        groupAdapter = new GroupAdapter(getContext(), mGroups);
        group_list.setAdapter(groupAdapter);

        SharedPreferences myPref2 = inflater.getContext().getSharedPreferences("Data", Context.MODE_PRIVATE);
        set1 = myPref2.getStringSet("key", null);

        if(groupAdapter != null)
        {
            count = groupAdapter.getItemCount();
            roomNumber.setText("You have " + count + "rooms");
        }
        else
        {
            roomNumber.setText("You have 0 rooms");
        }

        int itemCount = group_list.getAdapter().getItemCount();
        System.out.println("item Count " + itemCount);
        System.out.println("Count " + count);
        return groupfragmenview;
    }

    private void RetrievedAndDisplayGroups()
    {
            groupRef.whereArrayContains("participants", fuser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            mGroups.clear();
                            //QuerySnapshot contains multiple document Snapshot.
                            //to get single document Snapshot --> Iterate...
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                            {
                                //QueryDocumentSnapshot --> sub class of DocumentSnapshot
                                //with only difference QueryDocumentSnapshot is always access.
                                //but with DocumentSnapshot --> I check if documentSnapshot is access or not.
                                //Now Create groupFireStore object for collection !
                                GroupsFireStore groupsFireStore = documentSnapshot.toObject(GroupsFireStore.class);
                                //Return the id for this particular document and save in groupFireStore object.
                                groupsFireStore.setDocumentId(documentSnapshot.getId());
                                System.out.println(groupsFireStore.getAdmin());
                                System.out.println(groupsFireStore.getParticipants().size());
                                mGroups.add(groupsFireStore);
                                groupAdapter.notifyDataSetChanged();
                            }
                        }
                    });

    }

}

/*
* //        //get data from firestore then see !!!
//        firestore.collection("groups").whereArrayContains("participants", fuser.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful())
//                {
//                    System.out.println(task.getResult().getDocuments().size());
//                    for (QueryDocumentSnapshot document : task.getResult())
//                    {
//                        Log.d("Group Data", document.getId() + " => " + document.getData());
//                    }
//                }
//                else
//                    {
//                    Log.w("Group Error", "Error getting documents.", task.getException());
//                    }
//
//            }
//        });


        Set<String> set1 = myPref.getStringSet("id", null);
        System.out.println("wech taffichi set1 " + set1);

        assert set1 != null;
        ArrayList<String> par = new ArrayList<>(set1);

        String str[] = new String[par.size()];

        for (int j = 0; j < par.size(); j++) {
            str[j] = par.get(j);
        }
        System.out.println("hona " + Arrays.asList(str));

        //System.out.println("contain werch fiha "+ groupsFireStore.getParticipants());
//        if(par.contains())*/
