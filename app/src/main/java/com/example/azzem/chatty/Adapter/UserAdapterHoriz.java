package com.example.azzem.chatty.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.azzem.chatty.Model.User;
import com.example.azzem.chatty.ProfileActivity;
import com.example.azzem.chatty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapterHoriz extends RecyclerView.Adapter<UserAdapterHoriz.UsersViewHolder>
{
    private Context mContext;
    private List<User> mUsers;

    private ColorGenerator generator = ColorGenerator.MATERIAL;
    private int color;


    FirebaseUser fuser;

    public UserAdapterHoriz(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder
    {
        private CircleImageView usersImages;
        private TextView username;

        public UsersViewHolder(View itemView)
        {
            super(itemView);
            usersImages = itemView.findViewById(R.id.usersImage);
            username = itemView.findViewById(R.id.username);
        }
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item_horiz, parent, false);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position)
    {
         final User user = mUsers.get(position);

         holder.username.setText(user.getUsername());

        if (user.getImageURL().equals("default"))
        {
            //holder.profile_image.setImageResource(R.drawable.avatar);

            String letter = String.valueOf(getItem(position).toUpperCase().charAt(0));

            color = generator.getColor(getItem(position));

            TextDrawable drawable = TextDrawable.builder().buildRound(letter, color); // radius in px

            holder.usersImages.setBackground(drawable);
        }
        else
        {
            Picasso.get().load(user.getImageURL()).into(holder.usersImages);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                Intent profile_intent = new Intent(mContext, ProfileActivity.class);
//                profile_intent.putExtra("userid", user.getId());
//                mContext.startActivity(profile_intent);
            }
        });

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
}
