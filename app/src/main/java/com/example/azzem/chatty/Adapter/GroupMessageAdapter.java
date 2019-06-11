package com.example.azzem.chatty.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.azzem.chatty.Model.MessageG;
import com.example.azzem.chatty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.GroupMessageViewHolder> {
    public static final int MSG_RECEIVERS = 1;
    public static final int MSG_SENDER = 0;

    private List<MessageG> mMessageList;
    private Context mContext;

    public GroupMessageAdapter(List<MessageG> mMessageList, Context mContext) {
        this.mMessageList = mMessageList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public GroupMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_SENDER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_sender, parent, false);
            return new GroupMessageViewHolder(view);
        } else {
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
    public void onBindViewHolder(@NonNull final GroupMessageViewHolder groupMessageViewHolder, int position) {
        MessageG groups = mMessageList.get(position);
        System.out.println("wech men msg hada" + groups.getMsg());
        groupMessageViewHolder.messages_textView.setText(groups.getMsg());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        if (mMessageList.get(position).getFrom().equals(firebaseUser.getUid()))
        {
            return MSG_SENDER;
        }
        else
            {
            return MSG_RECEIVERS;
           }
    }
}