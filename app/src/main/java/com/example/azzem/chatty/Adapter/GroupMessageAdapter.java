package com.example.azzem.chatty.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.azzem.chatty.Model.MessageG;
import com.example.azzem.chatty.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.GroupMessageViewHolder> {
    public static final int MSG_RECEIVERS = 1;
    public static final int MSG_SENDER = 0;

    private List<MessageG> mMessageList;
    private Context mContext;
    FirebaseFirestore receiversInfo;

    public GroupMessageAdapter(List<MessageG> mMessageList, Context mContext) {
        this.mMessageList = mMessageList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public GroupMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        receiversInfo = FirebaseFirestore.getInstance();
        if (viewType == MSG_SENDER)
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
        private TextView messages_textView, participantsName;
        private ImageView imageView;
        private CircleImageView profileImage;

        public GroupMessageViewHolder(@NonNull View itemView)
        {
            super(itemView);
            messages_textView = itemView.findViewById(R.id.group_chat_text_display);
            imageView = itemView.findViewById(R.id.message_image_layout2);
            profileImage = itemView.findViewById(R.id.profile_image);
            participantsName = itemView.findViewById(R.id.name_of_participants);
            imageView.setClipToOutline(true);
        }
    }



    @Override
    public void onBindViewHolder(@NonNull final GroupMessageViewHolder groupMessageViewHolder, int position)
    {
        MessageG groups = mMessageList.get(position);

        String id = groups.getFrom();

        receiversInfo.collection("Users")
                     .document(id)
                     .get()
                     .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                     {
                         @Override
                         public void onSuccess(DocumentSnapshot documentSnapshot)
                         {
                             String imageURL = documentSnapshot.getString("imageURL");
                             String name = documentSnapshot.getString("username");

                             System.out.println(name+"name ???");

                             groupMessageViewHolder.participantsName.setText(name);

                             if(imageURL.equals("default"))
                             {
                                 String letter = null;
                                 if (name != null)
                                 {
                                     letter = String.valueOf(name.toUpperCase().charAt(0));
                                 }
                                 int color = R.color.colorPrimaryDark;
                                 TextDrawable drawable = TextDrawable.builder().buildRound(letter, color);
                                 groupMessageViewHolder.profileImage.setBackground(drawable);
                             }
                             else
                             {
                                 Picasso.get().load(imageURL).into(groupMessageViewHolder.profileImage);
                             }
                         }
                     });

        if(groups.getType().equals("text"))
        {
            groupMessageViewHolder.messages_textView.setText(groups.getMsg());
            groupMessageViewHolder.imageView.setVisibility(View.GONE);
        }
        else
        {
            groupMessageViewHolder.messages_textView.setVisibility(View.GONE);
            Picasso.get().load(groups.getMsg()).placeholder(R.drawable.image_maker).into(groupMessageViewHolder.imageView);
        }
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