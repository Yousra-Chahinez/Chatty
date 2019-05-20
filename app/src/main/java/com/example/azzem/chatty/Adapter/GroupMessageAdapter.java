package com.example.azzem.chatty.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.azzem.chatty.GroupChatActivity;
import com.example.azzem.chatty.Model.Groups;
import com.example.azzem.chatty.Model.MessageG;
import com.example.azzem.chatty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.GroupMessageViewHolder>
{
    public static final int MSG_RECEIVERS = 1;
    public static final int MSG_SENDER = 0;

    private List<MessageG> mMessageList;
    private Context mContext;

    public GroupMessageAdapter(List<MessageG> mMessageList, Context mContext)
    {
        this.mMessageList = mMessageList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public GroupMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(viewType == MSG_SENDER)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_sender, parent, false);
            return new GroupMessageViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_receivers, parent, false);
            return new GroupMessageViewHolder(view);
        }
    }

    public class GroupMessageViewHolder extends RecyclerView.ViewHolder
    {
        private TextView messages_textView;
        public GroupMessageViewHolder(@NonNull View itemView)
        {
            super(itemView);
            messages_textView = itemView.findViewById(R.id.group_chat_text_display);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupMessageViewHolder groupMessageViewHolder, int position)
    {
        MessageG groups = mMessageList.get(position);
        System.out.println("wech men msg hada" + groups.getMsg());
        groupMessageViewHolder.messages_textView.setText(groups.getMsg());
    }
    @Override
    public int getItemCount()
    {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        if(mMessageList.get(position).getFrom().equals(firebaseUser.getUid()))
        {
            return MSG_SENDER;
        }
        else
        {
            return MSG_RECEIVERS;
        }
    }
}
