package com.example.azzem.chatty.Adapter;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.azzem.chatty.MessageActivity;
import com.example.azzem.chatty.Model.Chat;
import com.example.azzem.chatty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ChatsViewHolder2>
{
    private Context mContext;
    private List<Chat> mChats;

    private ColorGenerator generator = ColorGenerator.MATERIAL;
    private int color;

    FirebaseUser fuser;
    FirebaseFirestore lastMsgRef;

    public UserAdapter(Context mContext, List<Chat> mUsers)
    {
        this.mContext = mContext;
        this.mChats = mUsers;
    }

       public class ChatsViewHolder2 extends RecyclerView.ViewHolder
    {
        private TextView username, lastMessage, lastTimeTextView;
        private CircleImageView profile_image;

        public ChatsViewHolder2(View itemView)
        {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            lastMessage = itemView.findViewById(R.id.last_message);
            lastTimeTextView = itemView.findViewById(R.id.time_text_view);
        }
    }

    @NonNull
    @Override
    public ChatsViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        lastMsgRef = FirebaseFirestore.getInstance();

        return new ChatsViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatsViewHolder2 holder, final int position) {

        //-------------------SHOW USERNAME AND PROFILE IMAGE IN USER ITEM-------------------------//
        final Chat chat = mChats.get(position);
        holder.username.setText(chat.getReceiverName());

        if (chat.getReceiverImage().equals("default")) {
            //holder.profile_image.setImageResource(R.drawable.avatar);

            String letter = String.valueOf(getItem(position).toUpperCase().charAt(0));

            color = generator.getColor(getItem(position));

            TextDrawable drawable = TextDrawable.builder().buildRound(letter, color); // radius in px

            holder.profile_image.setBackground(drawable);
        }
        else
            {
            Picasso.get().load(chat.getReceiverImage()).into(holder.profile_image);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent message_intent = new Intent(mContext, MessageActivity.class);
                    message_intent.putExtra("documentId", chat.getDocumentid());
                    message_intent.putExtra("receiverId", chat.getReceiverId());
                    message_intent.putExtra("username", chat.getReceiverName());
                    message_intent.putExtra("receiverImage", chat.getReceiverImage());
                    mContext.startActivity(message_intent);
                }
            });


         /* FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            Query query = rootRef.collection("High Volume")
                .orderBy("Time", Query.Direction.DESCENDING)
                .limit(1);
            query.get().addOnCompleteListener();*/

        Query query = lastMsgRef.collection("Chats")
                                .document(chat.getDocumentid())
                                .collection("Messages")
                                .orderBy("time", Query.Direction.DESCENDING)
                                .limit(1);

        Query queryTime = lastMsgRef.collection("Chats")
                .document(chat.getDocumentid())
                .collection("Messages")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(1);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if(task.isSuccessful())
                {
                    for(DocumentSnapshot snapshot : task.getResult())
                    {
                        String lastMsgSend = snapshot.getString("message");
                        String fromId = snapshot.getString("from");

                        if(fromId.equals(fuser.getUid()))
                        {
                            if (lastMsgSend != null && lastMsgSend.equals("Hi !")) {
                                holder.lastMessage.setText("No message");
                            } else {
                                holder.lastMessage.setText("You: " +lastMsgSend);
                            }
                        }
                        else
                        {
                            holder.lastMessage.setText(lastMsgSend);
                        }
                    }
                }
                else
                {
                    System.out.println("madjjblich last message "+task.getException());
                }
            }
        });

        queryTime.get()
                 .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                 {
                     @Override
                     public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                     {
                         for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                         {
                             String lastTime = documentSnapshot.getString("time");
                             String lastMsgSend = documentSnapshot.getString("message");

                             if(lastMsgSend.equals("Hi !"))
                             {
                                 holder.lastTimeTextView.setText("");
                             }
                             else
                                 {
                                 holder.lastTimeTextView.setText(lastTime);
                                 }
                         }
                     }
                 });

    }

    private String getItem(int position)
    {
        return String.valueOf(mChats.get(position).getReceiverName());
    }

    @Override
    public int getItemCount()
    {
        return mChats.size();
    }
}