package com.example.azzem.chatty.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.azzem.chatty.Model.User;
import com.example.azzem.chatty.ProfileActivity;
import com.example.azzem.chatty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>
{
    private Context mContext;
    private List<User> mUsers;

    private FirebaseUser fuser;

    private ColorGenerator generator = ColorGenerator.MATERIAL;

    public ChatsAdapter(Context mContext, List<User> mUsers)
    {
        this.mContext = mContext;
        this.mUsers = mUsers;
        //Create a new ArrayList which contain the same items as mUsers List.
        // mUsersFull = new ArrayList<>(mUsers);
    }

    public class ChatsViewHolder extends RecyclerView.ViewHolder
    {
        private TextView username, last_message, time_text;
        private CircleImageView profile_image;
        public CircleImageView img_on, img_off;

        public ChatsViewHolder(View itemView)
        {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_off = itemView.findViewById(R.id.img_off);
            img_on = itemView.findViewById(R.id.img_on);
            last_message = itemView.findViewById(R.id.last_message);
            time_text = itemView.findViewById(R.id.time_text_view);
        }
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        return new ChatsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatsViewHolder chatsViewHolder, int position)
    {
        final User user = mUsers.get(position);
        chatsViewHolder.username.setText(user.getUsername());

        chatsViewHolder.last_message.setVisibility(View.GONE);
        chatsViewHolder.time_text.setVisibility(View.GONE);

        if(user.getImageURL().equals("default"))
        {
            String letter = String.valueOf(user.getUsername().charAt(0));

            int color = R.color.colorPrimaryDark;
            TextDrawable drawable = TextDrawable.builder().buildRound(letter, color); // radius in px
            chatsViewHolder.profile_image.setBackground(drawable);
        }
        else
        {
            Picasso.get().load(user.getImageURL()).into(chatsViewHolder.profile_image);
        }

        chatsViewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Go TO PROFILE ACTIVITY.
                Intent profile_intent = new Intent(mContext, ProfileActivity.class);
                //passer une donnée (extras) qui est userid à MessageActivity.
                profile_intent.putExtra("name", user.getUsername());
                profile_intent.putExtra("userid", user.getId());
                profile_intent.putExtra("imageURL", user.getImageURL());
                mContext.startActivity(profile_intent);

                SharedPreferences preferences = mContext.getSharedPreferences("ReceiverId", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("userid", user.getId());
                editor.apply();
            }
        });
    }

    @Override
    public int getItemViewType(int position)
    {
        return super.getItemViewType(position);
    }


    @Override
    public int getItemCount()
    {
        return mUsers.size();
    }
}
