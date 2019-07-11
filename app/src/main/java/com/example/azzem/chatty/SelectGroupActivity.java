package com.example.azzem.chatty;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.azzem.chatty.Adapter.SelectGroupAdapater;
import com.example.azzem.chatty.Model.GroupsFireStore;
import com.example.azzem.chatty.Model.MessageG;
import com.example.azzem.chatty.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SelectGroupActivity extends AppCompatActivity {
    private List<User> mUsers;
    private RecyclerView mRecyclerView;
    private SelectGroupAdapater selectGroupAdapater;
    private Button btn_create_group_chat, groupImage;
    private EditText searchEditText;
    private TextView stateText;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String currentTime;

    FirebaseUser firebaseUser;
    FirebaseFirestore userRef;
    private Uri resultUri;
    //The requestCode for --> identify from which Intent you came back.
    private static final int GALLERY_PICK = 1; ///for request.

    Date currentTime2 = Calendar.getInstance().getTime();
    StorageReference groupImageReference;
    FirebaseFirestore firebaseFirestore;
    CollectionReference groupRef;

    public SelectGroupActivity()
    {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_groupe);

        //Initialize Controllers.
        initViews();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUsers = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        groupRef = firebaseFirestore.collection("groups");
        groupImageReference = FirebaseStorage.getInstance().getReference();
        userRef = FirebaseFirestore.getInstance();
        //documentReference = groupRef.document();

        btn_create_group_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //createChat();
                RequestNewGroup();
            }
        });

        readUsers();

        selectGroupAdapater = new SelectGroupAdapater(SelectGroupActivity.this, mUsers, true);
        mRecyclerView.setAdapter(selectGroupAdapater);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                readUsers();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                searchUsers(s.toString().toLowerCase());
            }
            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

    }

    private void initViews()
    {
        mRecyclerView = findViewById(R.id.recycler_view_select_groups);
        btn_create_group_chat = findViewById(R.id.create_group_chat);
        searchEditText = findViewById(R.id.search_editText);
        toolbar = findViewById(R.id.toolbar);
        swipeRefreshLayout = findViewById(R.id.refresh_select_user);
    }

    private void readUsers()
    {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef.collection("Users")
               .get()
               .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
               {
                   @Override
                   public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                   {
                       mUsers.clear();
                       for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                       {
                           User user1 = documentSnapshot.toObject(User.class);

                           assert firebaseUser != null;
                           if (user1.getId() != null && !user1.getId().equals(firebaseUser.getUid()))
                           {
                               mUsers.add(user1);
                           }
                       }
                       selectGroupAdapater.notifyDataSetChanged();
                   }
               });
    }

    private void RequestNewGroup()
    {
        final Dialog dialogCreateGroup = new Dialog(SelectGroupActivity.this);
        dialogCreateGroup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCreateGroup.setContentView(R.layout.custom_dialog);
        dialogCreateGroup.getWindow().setBackgroundDrawableResource(android.R.color.transparent);//for radius...
        final MaterialEditText groupNameField = dialogCreateGroup.findViewById(R.id.input);
        Button btn_create_group = dialogCreateGroup.findViewById(R.id.btn_create_group);
        Button btn_cancel_create_group = dialogCreateGroup.findViewById(R.id.btn_cancel_create_group);
        stateText = dialogCreateGroup.findViewById(R.id.textSuccess);

        groupImage = dialogCreateGroup.findViewById(R.id.addGroupImage);

        dialogCreateGroup.show();

        //------ADD GROUP IMAGE------//
        groupImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select image"), GALLERY_PICK);
            }
        });
        //------------------------------//

        //---------ADD GROUP NAME--------//
        btn_create_group.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String groupName = groupNameField.getText().toString().trim();
                if (!TextUtils.isEmpty(groupName))
                {
                    dialogCreateGroup.dismiss();
                    //groupNameField.setError("Please enter the group name");
                    CreateNewGroupWithFireStore(groupName);
                    Intent intent_message_group = new Intent(SelectGroupActivity.this, MainActivity.class);
                    startActivity(intent_message_group);
                    finish();
                }
            }
        });
        //------------------------------//


        //----CANCEL CREATE GROUP-----//
       btn_cancel_create_group.setOnClickListener(new View.OnClickListener()
       {
           @Override
           public void onClick(View view)
           {
               dialogCreateGroup.hide();
           }
       });
       //------------------------------//
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //Check the request code.

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK)
        {
            //I get the result as data.
            //the uri that i will get from the galleryIntent if i select image i will get the uri...
            Uri imageUri = data.getData();

            //Start the image CropActivity.
            CropImage.activity(imageUri)
                     //maintain a square pixel image.
                    //crop the image in square pixels.
                     .setAspectRatio(1, 1)
                     .start(SelectGroupActivity.this);
        }
        //If the request code pass to the CropActivity.
        //Make sure that the result which are getting is from the CropActivity they have created...
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            //Store the result in "result" variable and get the data from CropImageActivity.
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK)
            {
                stateText.setText("Uploading...");
                //----------------------Create ProgressDialog-----------------------------------//
//                //create AlertDialog which will show this layout with ProgressBar.
//                final Dialog dialog = new Dialog(SelectGroupActivity.this);
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.setCancelable(false);
//                dialog.setContentView(R.layout.progressdialog);
//                TextView textProgressDialog = dialog.findViewById(R.id.text_progressBar);
//                textProgressDialog.setText("Uploading image...");
//                dialog.show();
                //--------------------------------------------------------------------------------//

                //Give the uri of the data.
                //Uri of the CroppedImage. !
                resultUri = result.getUri();

                if(resultUri != null)
                {
                    stateText.setText("Success Uploading...");
                }

                //Now store it this Uri in Firebase Storage.
                //The last child is our file.
                //In the last child i want to store the name of the image.
                //To store a really good name store the id of the user every time the user store an image !
            }
        }
    }

    //SnapshotListener ---> Load data in real time.

    private void CreateNewGroupWithFireStore(String groupName)
    {
        SharedPreferences myPrefUsername = SelectGroupActivity.this.getSharedPreferences("username", Context.MODE_PRIVATE);
        Set<String> set2 = myPrefUsername.getStringSet("name", null);
        assert set2 != null;
        ArrayList<String> par2 = new ArrayList<>(set2);

        final String str2[] = new String[par2.size()];

        for (int j = 0; j < par2.size(); j++)
        {
            str2[j] = par2.get(j);
        }

        System.out.println("set2 wech djib "+set2);

        SharedPreferences myPref = SelectGroupActivity.this.getSharedPreferences("idd", Context.MODE_PRIVATE);
        Set<String> set1 = myPref.getStringSet("id", null);

        ArrayList<String> par = null;
        if (set1 != null)
        {
            btn_create_group_chat.setEnabled(false);

            par = new ArrayList<>(set1);
            par.add(firebaseUser.getUid());

            String str[] = new String[par.size()];

            for (int j = 0; j < par.size(); j++) {
                str[j] = par.get(j);
            }
            final GroupsFireStore groupsFireStore = new GroupsFireStore(Arrays.asList(str), groupName, firebaseUser.getUid(),
                    "default");
            firebaseFirestore.collection("groups")
                    .add(groupsFireStore)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(final DocumentReference documentReference) {
                            groupsFireStore.setDocumentId(documentReference.getId());
                            groupsFireStore.setParticipants_names(Arrays.asList(str2));
//                        Map<String, Object> msg = new HashMap<>();
//
//               msg.put("msgBody", "Hi !");
//                        msg.put("time", com.google.firebase.firestore.FieldValue.serverTimestamp());
//                        msg.put("type", "text");

                            if (resultUri != null) {
                                System.out.println("result uri " + resultUri);

                                //create sub-collection of messages with auto first msg.
                                final StorageReference filepath = groupImageReference.child("group_images").child(firebaseUser.getUid() + "jpg");
                                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String downloadUrl = uri.toString();
                                                    Log.d("SelectGroupActivity", "Uri Group Image " + uri);
                                                    //Store download Url in firestore.

                                                    HashMap<String, Object> imageMap = new HashMap<>();
                                                    imageMap.put("imageUrl", downloadUrl);

                                                    groupRef.document(documentReference.getId())
                                                            .update(imageMap)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(SelectGroupActivity.this, "Success Adding",
                                                                                Toast.LENGTH_LONG).show();
                                                                    } else {
                                                                        Toast.makeText(SelectGroupActivity.this, "Success Adding",
                                                                                Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            });

                                        } else {
                                            Toast.makeText(SelectGroupActivity.this, "Error in Uploading...", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            //--------------------------------------------------------------------------------//

                            //---Now get the time
                            Calendar calForTime = Calendar.getInstance();
                            //And for format pm, am...--> hh, time --> mm and ss for seconds if u want
                            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm");
                            currentTime = currentTimeFormat.format(calForTime.getTime());
                            //---------------------

                            MessageG messageGroup = new MessageG("Hi", firebaseUser.getUid(), "text");

//                        Map<String, Object> msg = new HashMap<>();
//                        msg.put("msgBody", "Hi !");
//                        msg.put("Admin", firebaseUser.getUid());
//                        msg.put("time", com.google.firebase.firestore.FieldValue.serverTimestamp());
//                        msg.put("type", "text");

                            documentReference.collection("messages")
                                    .document()
                                    .set(messageGroup)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("MESSAGE", "SUCCESS ADDING");
                                            }
                                        }
                                    });
                            Log.d("Success Add Group data", "DocumentSnapshot added with ID: " + documentReference.getId());
                            //RetreiveNewGroupWithFirestore(documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Failed Add Group data", "Error adding document", e);
                        }
                    });
        }

        else
            btn_create_group_chat.setEnabled(true);

    }

    private void searchUsers(String s)
    {
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
                     for(QueryDocumentSnapshot documentSnapshot :queryDocumentSnapshots)
                     {
                         User user = documentSnapshot.toObject(User.class);
                         if(firebaseUser != null)
                         {
                             if(!user.getId().equals(firebaseUser.getUid()))
                             {
                                 mUsers.add(user);
                             }
                             selectGroupAdapater.notifyDataSetChanged();
                         }
                     }
                 }
             });
    }


//    private void RetreiveNewGroupWithFirestore(final String id)
//    {
//        groupRef.get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        //QuerySnapshot contains multiple document Snapshot.
//                        //to get single document Snapshot --> Iterate...
//                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
//                        {
//                            //QueryDocumentSnapshot --> sub class of DocumentSnapshot
//                            //with only difference QueryDocumentSnapshot is always access.
//                            //but with DocumentSnapshot --> I check if documentSnapshot is access or not.
//                            //Now Create groupFireStore object for collection !
//                            GroupsFireStore groupsFireStore = documentSnapshot.toObject(GroupsFireStore.class);
//                            //Return the id for this particular document and save in groupFireStore object.
//
//                            String documentId = groupsFireStore.getDocumentId();
//                            String imgUrl = groupsFireStore.getImageUrl();
//                            String group_name = groupsFireStore.getGroupName();
//                            System.out.println("le nom du groupe " + group_name);
//                            System.out.println("id du groupe " + id);
//                            System.out.println("image du groupe " + imgUrl);
//                        }
//                    }
//                });
//    }
    /*
    *     //DocumentReference documentReference;

//    @Override
//    protected void onStart() {
//        super.onStart();
//        groupRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                //check if e is not null.
//                if (e != null) {
//                    return;
//                }
//                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                    GroupsFireStore groupsFireStore = documentSnapshot.toObject(GroupsFireStore.class);
//                    String group_name = groupsFireStore.getGroupName();
//                }
//            }
//        });
//    }*/
}