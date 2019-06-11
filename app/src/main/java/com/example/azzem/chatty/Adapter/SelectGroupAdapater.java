package com.example.azzem.chatty.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.azzem.chatty.Model.User;
import com.example.azzem.chatty.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectGroupAdapater extends RecyclerView.Adapter<SelectGroupAdapater.SelectGroupViewHolder>
{
    private Context mContext;
    private List<User> mUsers;
    private boolean isSelected;
    private ArrayList<Integer> positions = new ArrayList<>();
    private ArrayList<String> usernames = new ArrayList<>();
    private ArrayList<String> ids = new ArrayList<>();
    List<String> ids2;

    CollectionReference firestore;

    public SelectGroupAdapater(Context mContext, List<User> mUsers, boolean isSelected)
    {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.isSelected = isSelected;
    }

    @NonNull
    @Override
    public SelectGroupAdapater.SelectGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position)
    {
        View v = LayoutInflater.from(mContext).inflate(R.layout.user_item_group, parent, false);
        firestore = FirebaseFirestore.getInstance().collection("groups");
        return new SelectGroupAdapater.SelectGroupViewHolder(v);
    }

    public class SelectGroupViewHolder extends RecyclerView.ViewHolder
    {
        private TextView username;
        private CircleImageView profile_image;
        private CheckBox mCheckBox;
        public SelectGroupViewHolder(View itemView)
        {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            mCheckBox = itemView.findViewById(R.id.checkbox);
        }
    }

    public ArrayList<Integer> getPositions()
    {
        return positions;
    }
    public ArrayList<String> getUsernames()
    {
        return usernames;
    }
    public ArrayList<String> getIds()
    {
        return ids;
    }

    @Override
    public void onBindViewHolder(@NonNull final SelectGroupViewHolder selectGroupViewHolder, final int position)
    {
        final User user = mUsers.get(position);
        selectGroupViewHolder.username.setText(user.getUsername());

        //holder.profile_image.setImageResource(R.drawable.avatar);
        if(user.getImageURL().equals("default"))
        {
            String letter = String.valueOf(user.getUsername().charAt(0));
            int color = 0;
            //int color = generator.getRandomColor();
            TextDrawable drawable = TextDrawable.builder().buildRound(letter, color); // radius in px
            selectGroupViewHolder.profile_image.setBackground(drawable);
        }
        else
        {
            selectGroupViewHolder.profile_image.setBackground(null);
            Picasso.get().load(user.getImageURL())
                    //I want to load the image offline.
                    //This line of code retrieve the image offline.
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    //Callback for checking if the task is successful.
                    .into(selectGroupViewHolder.profile_image, new Callback()
                    {
                        @Override
                        public void onSuccess()
                        {
                            //If the task isSuccessful --> Any things.
                        }
                        @Override
                        public void onError(Exception e)
                        {
                            //if the task is not successful load the image online.
                            Picasso.get().load(user.getImageURL()).into(selectGroupViewHolder.profile_image);
                        }
                    });
        }


        //Will be calls any time the user click in the CheckBox.
        selectGroupViewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                //isChecked will be true if the user changes into selected else
                //If the user remove the selected
                mUsers.get(selectGroupViewHolder.getAdapterPosition()).setSelected(isChecked);
                if(selectGroupViewHolder.mCheckBox.isChecked())
                {
                    String ids_users = user.getId();
                    ids.add(ids_users);

                    // Convert ArrayList to object array
                    Object[] objArr = ids.toArray();

                    // convert Object array to String array
                    String[] str = Arrays
                            .copyOf(objArr, objArr
                                            .length,
                                    String[].class);

                    //turn Arrays to list, firestore doesn't accept arrays.

                    //to hashSet
                    HashSet<String> hashset = new HashSet<>(Arrays.asList(str));

                    SharedPreferences myPref = mContext.getSharedPreferences("idd", Context.MODE_PRIVATE);
                    SharedPreferences.Editor myEditor = myPref.edit();
                    myEditor.putStringSet("id", hashset);
                    System.out.println("hna wechnhi djibli " + hashset);
                    myEditor.apply();


                    String user_names = user.getUsername();
                    usernames.add(user_names);

                    // Convert ArrayList to object array
                    Object[] objArr_username = usernames.toArray();

                    // convert Object array to String array
                    String[] str_username = Arrays
                            .copyOf(objArr_username, objArr_username
                                            .length,
                                    String[].class);

                    HashSet<String> hashsetUsername = new HashSet<>(Arrays.asList(str_username));

                    SharedPreferences myPrefUsername = mContext.getSharedPreferences("username", Context.MODE_PRIVATE);
                    SharedPreferences.Editor myEditor1 = myPrefUsername.edit();
                    myEditor1.putStringSet("name", hashsetUsername);
                    System.out.println("f SelectGroupActivity "+ hashsetUsername);
                    myEditor1.apply();





                }
                else
                {
                    Toast.makeText(mContext, "not selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mUsers.size();
    }
}
