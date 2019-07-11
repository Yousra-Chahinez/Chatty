package com.example.azzem.chatty.Adapter;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.azzem.chatty.GroupChatActivity;
import com.example.azzem.chatty.Model.GroupsFireStore;
import com.example.azzem.chatty.R;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.groupsViewHolder>
{
    private Context mContext;
    private List<GroupsFireStore> mGroups;
    private DatabaseReference ref;

    public GroupAdapter(Context mContext, List<GroupsFireStore> mGroups)
    {
        this.mContext = mContext;
        this.mGroups = mGroups;
    }

    @NonNull
    @Override
    public groupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.groups_item, parent, false);
        return new GroupAdapter.groupsViewHolder(view);
    }


    public class groupsViewHolder extends RecyclerView.ViewHolder
    {
        public final TextView group_name;
        public final ImageView group_image;

        public groupsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            group_image = itemView.findViewById(R.id.group_image);
            group_name = itemView.findViewById(R.id.group_name);
            group_image.setClipToOutline(true);
        }
    }



    @Override
    public void onBindViewHolder(@NonNull groupsViewHolder groupsViewHolder, int position)
    {

        final GroupsFireStore groups = mGroups.get(position);
        groupsViewHolder.group_name.setText(groups.getGroupName());
        //System.out.println("group document id" + groups.getDocumentId());

//        assert set1 != null;
//        if(!groups.getMembers().equals(set1.toString().replace("[[", "").replace("]]", "")))
//        {
//            groupsViewHolder.itemView.setVisibility(View.GONE);
//        }

        if(groups.getImageUrl().equals("default"))
        {
            groupsViewHolder.group_image.setImageResource(R.drawable.image);
        }
        else
        {
            Picasso.get().load(groups.getImageUrl()).into(groupsViewHolder.group_image);
        }

        groupsViewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent message_group_intent = new Intent(mContext, GroupChatActivity.class);
                message_group_intent.putExtra("documentId", groups.getDocumentId());
                message_group_intent.putExtra("groupName", groups.getGroupName());
                if(!groups.getImageUrl().equals("default"))
                {
                    message_group_intent.putExtra("groupImage", groups.getImageUrl());
                    System.out.println("groupImage "+groups.getImageUrl());
                }
                mContext.startActivity(message_group_intent);
            }
        });

//        SharedPreferences myPrefUsername = mContext.getSharedPreferences("username", Context.MODE_PRIVATE);
//        Set<String> nameSet = myPrefUsername.getStringSet("name", null);
//
//        assert nameSet != null;
//        for(int j=0; j<nameSet.size(); j++)
//        {
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append(nameSet);
//            groupsViewHolder.user_name.setText(stringBuilder.toString().replace(",", "&")
//                    .replace("]", "").replace("[", ""));
//        }
    }
    @Override
    public int getItemCount()
    {
        return mGroups.size();
    }
}
