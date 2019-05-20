package com.example.azzem.chatty.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.example.azzem.chatty.MessageActivity;
import com.example.azzem.chatty.Model.User;
import com.example.azzem.chatty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> implements Filterable
{
    private Context mContext;
    private List<User> mUsers;

    private FirebaseUser fuser;

    //Create a copy of the users list for search...
    private List<User> mUsersFull;

    public static final int SECTION_View = 0;
    public static final int CONTENT_VIEW = 1;

    public ChatsAdapter(Context mContext, List<User> mUsers)
    {
        this.mContext = mContext;
        this.mUsers = mUsers;
        //Create a new ArrayList which contain the same items as mUsers List.
        mUsersFull = new ArrayList<>(mUsers);
    }

    public class ChatsViewHolder extends RecyclerView.ViewHolder
    {
        private TextView username, last_message, time_text;;
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

    public class SectionView extends RecyclerView.ViewHolder
    {
        private TextView sectionView;
        public SectionView(@NonNull View itemView)
        {
            super(itemView);
            sectionView = itemView.findViewById(R.id.section_view);
        }
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
//        if(viewType == SECTION_View)
//        {
//            return new SectionView(LayoutInflater.from(mContext).inflate(R.layout.section_header, parent, false));
//        }
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
            //int color = generator.getRandomColor();
            TextDrawable drawable = TextDrawable.builder().buildRound(letter, color); // radius in px
            chatsViewHolder.profile_image.setBackground(drawable);
        }
        else
        {
            chatsViewHolder.profile_image.setBackground(null);
            Picasso.get().load(user.getImageURL())
                    //I want to load the image offline.
                    //This line of code retrieve the image offline.
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    //Callback for checking if the task is successful.
                    .into(chatsViewHolder.profile_image, new Callback()
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
                            Picasso.get().load(user.getImageURL()).into(chatsViewHolder.profile_image);
                        }
                    });
        }
        chatsViewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent message_intent = new Intent(mContext, MessageActivity.class);
                //passer une donnée (extras) qui est userid à MessageActivity.
                message_intent.putExtra("userid", user.getId());
                message_intent.putExtra("name", user.getUsername());
                mContext.startActivity(message_intent);
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

    //--------------------------------------------------------------------------------------------//
    @Override
    public Filter getFilter()
    {
        return UsersFilter;
    }
    private Filter UsersFilter = new Filter()
    {
        //first method executed in background -> + (perform Filtering automatically).
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            //this list contains the filters items.
            List<User> filteredList = new ArrayList<>();
            //I will user the variable CharSequence to define the filter object.
            if(constraint == null || constraint.length() == 0)
            {
                //Show all the list...Any result
                //Show all the results.
                //mUsersFull --> copy of the model list which always contains all the items.
                filteredList.addAll(mUsersFull);
                System.out.println("??? full" + mUsersFull);
                System.out.println("??? filteredList" + filteredList);
            }
            else  //Somethings is typing into the search field.
            {
                //trim for eliminate the empty spaces at the begging and the end of the input.
                String filterPattern = constraint.toString().toLowerCase().trim();
                //Now iterate thorough my items in my List and see which one similar to this filterPattern.
                //and also add them to FilterList.
                for(User user : mUsersFull)
                {
                    if(user.getUsername().toLowerCase().contains(filterPattern))
                    {
                        filteredList.add(user);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            //return filter result.
            return results;
        }
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results)
        {
            mUsers.clear(); //Remove any item in it.
            //Put the items in mUsers from my filteredList. c'est pour ca clear...
            //Now mUsers contain just my filtered items.
            mUsers.addAll((List) results.values);
            //Refresh the list.
            notifyDataSetChanged();
        }
    };
    //--------------------------------------------------------------------------------------------//
}
