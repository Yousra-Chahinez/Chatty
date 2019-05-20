package com.example.azzem.chatty.Adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.example.azzem.chatty.Fragments.HomeFragment;
import com.example.azzem.chatty.MessageActivity;
import com.example.azzem.chatty.Model.Messages;
import com.example.azzem.chatty.Model.User;
import com.example.azzem.chatty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>
{
    private final String TAG = "UserAdapter";
    private Context mContext;
    private List<User> mUsers;
    private String letter;
    private ColorGenerator generator = ColorGenerator.MATERIAL;
    //private int color = R.color.gradient_end_color;
    private boolean ischat; //pour le status online/offline
    int color;
    private String theLastMessage;
    private FirebaseUser fuser;

    public UserAdapter(Context mContext, List<User> mUsers, boolean ischat)
    {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
            /*permet de créer un ViewHolder à partir du layout XML représentant chaque ligne de la RecyclerView.
             CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT*/
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        return new UserAdapter.ViewHolder(view);
    }

    /*public String getItem(int position)
    {
        return String.valueOf(mUsers.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }*/

    /* Cette méthode est appelée pour chacune des lignes visibles affichées dans notre RecyclerView.
       grâce à la variable position, on peut récupérer l'objet User correspondant dans notre liste d'objet.*/
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position)
    {

        //-------------------SHOW USERNAME AND PROFILE IMAGE IN USER ITEM-------------------------//

        final User user = mUsers.get(position);
        holder.username.setText(user.getUsername());

        if(user.getImageURL().equals("default"))
        {
            //holder.profile_image.setImageResource(R.drawable.avatar);

            String letter = String.valueOf(getItem(position).charAt(0));

            color = generator.getColor(getItem(position));
            //int color = generator.getRandomColor();

            TextDrawable drawable = TextDrawable.builder().buildRound(letter, color); // radius in px

            holder.letter.setBackground(drawable);
        }
        else
        {
            Picasso.get().load(user.getImageURL()).into(holder.letter);
        }

        //---------------------------STATUS ONLINE/OFFLINE----------------------------------------//
         if(ischat)
        {
            lastMessage(user.getId(), holder.last_message);
        }
        else
        {
           holder.last_message.setVisibility(View.GONE);
        }
       // if(ischat)
        //{
            if(user.getStatus().equals("online"))
            {
                System.out.println("HNA"+ user.getStatus());
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            }
            else
            {
                System.out.println("HNA2"+ user.getStatus());
                holder.img_off.setVisibility(View.VISIBLE);
                holder.img_on.setVisibility(View.GONE);
            }
       // }
       /* else
        {
            holder.img_off.setVisibility(View.GONE);
            holder.img_on.setVisibility(View.GONE);
        }*/

        //-------------------GO TO MESSAGE ACTIVITY BY CLICKING IN USER ITEM---------------------//

        //Car onBindViewHolder est executé selon le nombre des items qu'on a c-a-d nomdre des utilisateurs.
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /*CharSequence[] options = new CharSequence[]{"Send Message" , "Open Profile"};
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Select options");
                builder.setItems(options, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if(i==0)
                        {*/
                            Intent message_intent = new Intent(mContext, MessageActivity.class);
                            //passer une donnée (extras) qui est userid à MessageActivity.
                            message_intent.putExtra("userid", user.getId());
                            message_intent.putExtra("name", user.getUsername());
                            message_intent.putExtra("couleur", color);
                            mContext.startActivity(message_intent);

                            SharedPreferences preferences = mContext.getSharedPreferences("Receiver_id", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("userid", user.getId());
                            editor.apply();

                            SharedPreferences preferences5 = mContext.getSharedPreferences("Receiver_id2", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor5 = preferences5.edit();
                            editor5.putString("userid2", user.getId());
                            editor5.apply();

                            SharedPreferences preferences2 = mContext.getSharedPreferences("Name of the user", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = preferences2.edit();
                            editor2.putString("username", user.getUsername());
                            editor2.apply();

                            //From MessagesAdapter.
                            SharedPreferences preferences3 = mContext.getSharedPreferences("time", Context.MODE_PRIVATE);
                            preferences3.getString("time", "");
                            String time = preferences3.getString("time", "");
                            Log.d(TAG, time);
                            System.out.println("hna time ta3 UserAdapter " + time);
                            holder.time_text.setText(time);


                            /*Intent home_fragment_intent = new Intent(mContext, HomeFragment.class);
                            home_fragment_intent.putExtra("name", user.getUsername());
                            mContext.startActivity(home_fragment_intent);*/

                            /*HomeFragment homeFragment = new HomeFragment();
                            Bundle b = new Bundle();
                            b.putString("user_name", user.getUsername());
                            homeFragment.setArguments(b);*/
                     /*   }
                    }
                });
                builder.show();*/
            }
        });

        /*----------------------------CREATE A GROUP OF CHAT------------------------------------//
        holder.m_add.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final User user1 = mUsers.get(position);
                //Any time the user clicks on the check box
                mUsers.get(holder.getAdapterPosition()).setSelected(isChecked); //this boolean will be true if the user change it to selected
                //and false if user removed selected

                Intent group_intent = new Intent();
                String test = user1.getId();
                group_intent.putExtra("userid1", test);
                Toast.makeText(mContext, test, Toast.LENGTH_LONG).show();
            }
        });
        --------------------------------------------------------------------------------------*/
    }

    private String getItem(int position)
    {
        return String.valueOf(mUsers.get(position).getUsername());
    }

    @Override
    public int getItemCount()
    {
        return mUsers.size();
    }


    //---------------------------------VIEW HOLDER CLASS------------------------------------------//

    /* le ViewHolder est le modèle pour toutes nos cellules.
    * Le ViewHolder est un objet utilisé pour accélérer le rendu de votre liste.
    * nous récupérions nos vues directement dans la méthode getView() avec nos findViewById()
    * ce qui signifiait qu’à chaque fois qu’une cellule se construisait, toutes les vues étaient
    * de nouveau récupérées, liées.( récupérer la vue une seule fois par finViewById )*/

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView username, last_message, time_text;
        public CircleImageView letter, img_on, img_off;

        public ViewHolder(View itemView)
        {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            letter = itemView.findViewById(R.id.profile_image);
            img_off = itemView.findViewById(R.id.img_off);
            img_on = itemView.findViewById(R.id.img_on);
            last_message = itemView.findViewById(R.id.last_message);
            time_text = itemView.findViewById(R.id.time_text_view);
        }
    }
    //--------------------------------------------------------------------------------------------//
    private void lastMessage(final String userid, final TextView last_message)
    {
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        DatabaseReference refernce = FirebaseDatabase.getInstance().getReference("messages").child(userid).child(firebaseUser.getUid());
            refernce.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Messages messages = snapshot.getValue(Messages.class);
                        assert messages != null;
                        if (!(messages.getFrom().equals(firebaseUser.getUid())) && (messages.getFrom().equals(userid)) ||
                                (!(messages.getFrom().equals(userid))) && (messages.getFrom().equals(firebaseUser.getUid())))
                        {
                            theLastMessage = messages.getMessage();
                        }
                    }
                        switch (theLastMessage)
                        {
                            case "default":
                                last_message.setText("No message");
                                break;
                            default:
                                last_message.setText(theLastMessage);
                                break;
                        }
                        theLastMessage = "default";
                        }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {
                    Log.d(TAG, databaseError.getMessage());
                }
            });
        }
}